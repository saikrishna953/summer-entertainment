package org.acme;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Collections;
import java.util.List;

public class Constants {

    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String TOKENS_DIRECTORY_PATH = "tokens";
    public static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    public static final String SPREADSHEET_ID = "1rC8dHKL1g2Jw4_3mYqTIy19MSVdB4LnTq9Oaj2rsQ_Q";
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    public static final String CREDENTIALS_FILE_PATH = "/credentials.json";
}
