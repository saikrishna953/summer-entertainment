package org.acme;

import org.acme.Sheets.SheetOperations;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Path("/summerEntertainment")
public class ExampleResource {


    public static final String matchString = "###Match %s -";
    public static final String matchWinnerString = "###Match %s Winner -";
    public static final String participantString = "###Today Match %s ,%s Choice -";
    public static final String noMatchString = "###No Match %s today";
    public static final int maxMatchesPerDay = 2;
    public static LinkedHashMap<String, User> participants = new LinkedHashMap<>();

    /*static {
        for (Participants value : Participants.values()) {
            participants.putIfAbsent(value.toString(), new User());
        }
    }*/
    @Inject
    SheetOperations sheetOperations;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sheets/update")
    public String updateSheet(Root requestBody) throws IOException, URISyntaxException, GeneralSecurityException {

        /*java.nio.file.Path path = Paths.get(getClass().getClassLoader()
                .getResource("chat.txt").toURI());*/

        Supplier<Stream<String>> lines = () -> {
            return Arrays.stream(requestBody.getBody().split("\\r?\\n"));
            /*try {
                return Files.lines(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;*/
        };
        for (int m = 1; m <= maxMatchesPerDay; m++) {
            Optional<String> todayMatch = getTodaysMatch(lines.get(), matchString, m);
            if (todayMatch.isEmpty()) {
                continue;
            }
            System.out.println("Today's match " + m + " is - " + todayMatch.get());
            String firstTeam = StringUtils.substringBefore(todayMatch.get().toUpperCase(), "VS").trim();
            String secondTeam = StringUtils.substringAfter(todayMatch.get().toUpperCase(), "VS").trim();
            ArrayList teams = new ArrayList();
            teams.add(firstTeam);
            teams.add(secondTeam);
            System.out.println("Today's match " + m + " first team is - " + firstTeam);
            System.out.println("Today's match " + m + " second team is - " + secondTeam);


            boolean noMatchDay = noMatchDay(lines.get(), m);

            for (String value : sheetOperations.getParticipants()) {
                User user = new User();

                if (noMatchDay){
                    user.setChosenTeam("");
                    participants.put(value, user);
                }else{
                    Optional<String> team = searchForParticipantTeam(lines.get(), value, m);
                    if (team.isEmpty()) {
                        user.setChosenTeam("");
                        participants.put(value, user);
                        System.out.println(value + " hasn't participated in today's match");
                    } else {
                        if (teams.contains(team.get())) {
                            user.setChosenTeam(team.get());
                            participants.put(value, user);
                        } else {
                            System.out.println(value + " hasn't chosen proper team in today's match");
                        }

                    }
                }
            }

            System.out.println(participants.toString());

            Optional<String> todayMatchWinner = getTodaysMatch(lines.get(), matchWinnerString, m);
            if (noMatchDay) {
                todayMatchWinner = Optional.of("NMD");
            } else {
                if (todayMatchWinner.isEmpty() && !noMatchDay) {
                    return "Did you forgot to publish today's scheduled match result?";
                } else if (!teams.contains(todayMatchWinner.get())) {
                    return "Match winner is incorrect. It isn't matching with the provided scheduled match - " + todayMatch.get();
                }
            }

            System.out.println("Today's match " + m + " winner is - " + todayMatchWinner.get());

            populateCreditAmounts(todayMatchWinner);
            sheetOperations.writeResultsToSheet(participants, todayMatch.get(), todayMatchWinner.get(), noMatchDay);
        }
        return "Done";
    }

    private boolean noMatchDay(Stream<String> stringStream, int matchNumer) {
        return stringStream.anyMatch(e -> StringUtils.containsIgnoreCase(e,
                String.format(noMatchString, matchNumer)));
    }

    private void populateCreditAmounts(Optional<String> todayMatchWinner) {
        Supplier<DoubleStream> intStream = () -> participants.values().stream().filter(user -> (!user.getChosenTeam().equalsIgnoreCase(todayMatchWinner.get())))
                .mapToDouble(User::getBetAmount);

        double losersAmount = intStream.get().sum();
        long countOfWinners = participants.size() - intStream.get().count();
        double distributionAmount = (losersAmount / countOfWinners);

        participants.entrySet().parallelStream().filter(e -> (e.getValue().getChosenTeam().equalsIgnoreCase(todayMatchWinner.get())))
                .forEach(e -> {
                    e.getValue().setCreditAmount(distributionAmount);
                });

        System.out.println(participants.toString());
    }

    private Optional<String> searchForParticipantTeam(Stream<String> lines, String toString, int matchNumber) {

        Optional<String> todayTeamO = lines.filter(e -> StringUtils.containsIgnoreCase(e,
                String.format(participantString, matchNumber, toString))).findFirst();
        Optional<String> todayTeam = Optional.empty();
        if (todayTeamO.isPresent()) {
            todayTeam = Optional.of(todayTeamO.get().substring(todayTeamO.get().
                    lastIndexOf('-') + 1).replace('#', ' ').trim());
        }
        return todayTeam;
    }

    private static Optional<String> getTodaysMatch(Stream<String> lines, String keyString, int matchNumber) {
        Optional<String> matchToday = lines.filter(e -> StringUtils.containsIgnoreCase(e, String.format(keyString, matchNumber))).findFirst();
        Optional<String> todayMatch = Optional.empty();
        if (matchToday.isPresent()) {
            todayMatch = Optional.of(matchToday.get().substring(matchToday.get().
                    lastIndexOf('-') + 1).replace('#', ' ').trim());
        }
        return todayMatch;
    }
}