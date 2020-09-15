package org.acme.Sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.acme.Constants;

import java.io.IOException;
import java.util.Collections;

public class Credential {

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */


    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */

    public static com.google.api.client.auth.oauth2.Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        /*System.out.println("In getCredentials() method");
        InputStream in = SheetOperations.class.getResourceAsStream(Constants.CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + Constants.CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Constants.JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, Constants.JSON_FACTORY, clientSecrets, Constants.SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(Constants.TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");*/
        GoogleCredential credential = GoogleCredential.fromStream(SheetOperations.class.getResourceAsStream(Constants.CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        return credential;
    }
}
