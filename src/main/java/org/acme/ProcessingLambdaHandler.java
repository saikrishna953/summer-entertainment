package org.acme;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Named("processing")
public class ProcessingLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    ExampleResource exampleResource;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Map<String, String> query = request.getQueryStringParameters();

        String httpMethod = request.getHttpMethod();
        String result = "";

        Map<String, String> pathParameters = request.getPathParameters();

        switch (httpMethod) {
            case "POST":
                String body = request.getBody();
                try {
                    Root requestBody = mapper.readValue(body, Root.class);
                    System.out.println(requestBody.toString());
                    result = exampleResource.updateSheet(requestBody);

                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                break;
        }

        return new APIGatewayProxyResponseEvent().withBody(result).withStatusCode(200);
    }
}
