package org.acme;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Path("/hello")
public class ExampleResource {


    public static final String matchString = "***Match -";
    public static final String matchWinnerString = "***Match Winner -";
    public static final String participantString = "***Today %s Choice -";
    public static final String noMatchString = "***No Match Day";
    public static Map<String, User> participants = new HashMap<>();

    static {
        for (Participants value : Participants.values()) {
            participants.putIfAbsent(value.toString(), new User());
        }
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/update")
    public String updateSheet() throws IOException, URISyntaxException, GeneralSecurityException {

        java.nio.file.Path path = Paths.get(getClass().getClassLoader()
                .getResource("chat.txt").toURI());

        Supplier<Stream<String>> lines = () -> {
            try {
                return Files.lines(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };

        Optional<String> todayMatch = getTodaysMatch(lines.get(), matchString);
        if (todayMatch.isEmpty()) {
            return "Did you forgot to include today's scheduled match?";
        }
        System.out.println("Today's match is - " + todayMatch.get());
        String firstTeam = StringUtils.substringBefore(todayMatch.get().toUpperCase(), "VS").trim();
        String secondTeam = StringUtils.substringAfter(todayMatch.get().toUpperCase(), "VS").trim();
        ArrayList teams = new ArrayList();
        teams.add(firstTeam);
        teams.add(secondTeam);
        System.out.println("Today's first team is - " + firstTeam);
        System.out.println("Today's second team is - " + secondTeam);

        boolean noMatchDay = noMatchDay(lines.get());
        if (noMatchDay) {
            System.out.println("No Match today");
        } else {
            for (Participants value : Participants.values()) {
                Optional<String> team = searchForParticipantTeam(lines.get(), value.toString());
                User user = participants.get(value.toString());
                if (team.isEmpty()) {
                    user.setChosenTeam("");
                    participants.put(value.toString(), user);
                    System.out.println(value.toString() + " hasn't participated in today's match");
                } else {
                    if (teams.contains(team.get())) {
                        user.setChosenTeam(team.get());
                        participants.put(value.toString(), user);
                    } else {
                        System.out.println(value.toString() + " hasn't chosen proper team in today's match");
                    }

                }

            }
            System.out.println(participants.toString());

            Optional<String> todayMatchWinner = getTodaysMatch(lines.get(), matchWinnerString);
            if (todayMatchWinner.isEmpty()) {
                return "Did you forgot to publish today's scheduled match result?";
            } else if (!teams.contains(todayMatchWinner.get())) {
                return "Match winner is incorrect. It isn't matching with the provided scheduled match - " + todayMatch;
            }
            System.out.println("Today's match winner is - " + todayMatchWinner.get());

            populateCreditAmounts(todayMatchWinner);
            writeToSheet(noMatchDay);
        }
       /* String data = lines.get().collect(Collectors.joining("\n"));

        System.out.println(data);*/
        return "Done";
    }

    private void writeToSheet(boolean noMatchDay) throws IOException, GeneralSecurityException {

        if(noMatchDay){
            System.out.println("no match day in para sheets");
        }else{
            SheetQuickStart.mainSheet(participants);
        }

    }

    private boolean noMatchDay(Stream<String> stringStream) {
        return stringStream.anyMatch(e -> StringUtils.containsIgnoreCase(e, noMatchString));
    }

    private void populateCreditAmounts(Optional<String> todayMatchWinner) {
        Supplier<DoubleStream> intStream = () -> participants.values().stream().filter(user -> (!user.getChosenTeam().equalsIgnoreCase(todayMatchWinner.get())))
                .mapToDouble(User::getBetAmount);

        double losersAmount = intStream.get().sum();
        long countOfWinners = Participants.values().length - intStream.get().count();
        double distributionAmount = (losersAmount / countOfWinners);

        participants.entrySet().parallelStream().filter(e -> (e.getValue().getChosenTeam().equalsIgnoreCase(todayMatchWinner.get())))
                .forEach(e -> {
                    e.getValue().setCreditAmount(distributionAmount);
                });

        System.out.println(participants.toString());
    }

    private Optional<String> searchForParticipantTeam(Stream<String> lines, String toString) {

        Optional<String> todayTeamO = lines.filter(e -> StringUtils.containsIgnoreCase(e, String.format(participantString, toString))).findFirst();
        Optional<String> todayTeam = Optional.empty();
        if (todayTeamO.isPresent()) {
            todayTeam = Optional.of(todayTeamO.get().substring(todayTeamO.get().
                    lastIndexOf('-') + 1).replace('*', ' ').trim());
        }
        return todayTeam;
    }

    private static Optional<String> getTodaysMatch(Stream<String> lines, String keyString) {
        Optional<String> matchToday = lines.filter(e -> StringUtils.containsIgnoreCase(e, keyString)).findFirst();
        Optional<String> todayMatch = Optional.empty();
        if (matchToday.isPresent()) {
            todayMatch = Optional.of(matchToday.get().substring(matchToday.get().
                    lastIndexOf('-') + 1).replace('*', ' ').trim());
        }
        return todayMatch;
    }
}