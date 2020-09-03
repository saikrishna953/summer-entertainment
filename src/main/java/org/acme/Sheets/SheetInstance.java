package org.acme.Sheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import org.acme.Constants;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SheetInstance {

    public static Sheets getSheetInstance() throws IOException, GeneralSecurityException {
        System.out.println("In getSheetInstance() method");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, Constants.JSON_FACTORY, Credential.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(Constants.APPLICATION_NAME)
                .build();
        return service;
    }
}
