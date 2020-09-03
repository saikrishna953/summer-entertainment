package org.acme.Sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.acme.Constants;
import org.acme.User;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.IntStream;

@ApplicationScoped
public class SheetOperations {

    private Sheets service;


    SheetOperations() throws IOException, GeneralSecurityException {
        this.service = SheetInstance.getSheetInstance();
    }

    public ArrayList<String> getParticipants() throws IOException {
        ArrayList<String> sheetParticipants = new ArrayList<>();
        final String range = "Sheet1!E1:1";
        ValueRange response = service.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, range)
                .setMajorDimension("COLUMNS")
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {

                sheetParticipants.add(row.get(0).toString());
            }
        }
        return sheetParticipants;

    }

    public ArrayList<Double> getTotals(LinkedHashMap<String, User> participants) throws IOException {

        ArrayList<Double> totals = new ArrayList<>();

        String range = "Sheet1!E2:" + Constants.ALPHABET.charAt(participants.size() - 1 + 4) + "2";
        ValueRange response = service.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, range)
                .setMajorDimension("COLUMNS")
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found for totals.");
            IntStream.range(0, participants.size()).forEach(e -> totals.add(Double.valueOf("0")));
        } else {
            for (List row : values) {
                totals.add(Double.parseDouble((String) row.get(0)));
            }
        }
        return totals;

    }

    public void updateTotals(LinkedHashMap<String, User> participants, ArrayList<Double> totals) throws IOException {

        List<List<Object>> cellValues = new ArrayList<>();
        String range = "Sheet1!E2:" + Constants.ALPHABET.charAt(participants.size() - 1 + 4) + "2";
        int i = 0;
        for (User user : participants.values()) {
            cellValues.add(Arrays.asList(user.getCreditAmount() + totals.get(i)));
            i++;
        }
        ValueRange body = new ValueRange()
                .setValues(cellValues).setMajorDimension("COLUMNS");
        Sheets.Spreadsheets.Values.Update request =
                service.spreadsheets().values().update(Constants.SPREADSHEET_ID, range, body);
        request.setValueInputOption("RAW");

        UpdateValuesResponse response = request.execute();

    }

    public void writeResultsToSheet(LinkedHashMap<String, User> participants, String todayMatch,
                                    String matchWinner, boolean noMatchDay) throws IOException {

        List<List<Object>> cellValues = new ArrayList<>();
        cellValues.add(Arrays.asList(LocalDate.now().toString()));
        cellValues.add(Arrays.asList(todayMatch));
        cellValues.add(Arrays.asList(matchWinner));
        cellValues.add(Arrays.asList(participants.values().iterator().next().getBetAmount()));
        if (noMatchDay) {
            participants.entrySet().forEach(e -> {
                User currentUser = e.getValue();
                cellValues.add(Arrays.asList("NMD"));
            });
        } else {
            ArrayList<Double> totals = getTotals(participants);
            participants.entrySet().forEach(e -> {
                User currentUser = e.getValue();
                cellValues.add(Arrays.asList(new StringBuilder(currentUser.getChosenTeam()).append(",")
                        .append(Double.toString(currentUser.getCreditAmount())).toString()));
            });
            updateTotals(participants, totals);
        }

        ValueRange body = new ValueRange()
                .setValues(cellValues).setMajorDimension("COLUMNS");
        AppendValuesResponse result =
                service.spreadsheets().values().append(Constants.SPREADSHEET_ID, "A1", body)
                        .setValueInputOption("RAW")
                        .setInsertDataOption("INSERT_ROWS")
                        .execute();

        System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
    }
}