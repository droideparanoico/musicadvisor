package advisor.auth;

import advisor.config.Params;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Authorization {

    private static String accessServer;

    public Authorization(String accessServer) {
        Authorization.accessServer = accessServer;
    }

    public void getToken() throws IOException, InterruptedException {

        System.out.println(Params.MAKING_HTTP_REQUEST_FOR_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(
                        "client_id=" + Params.CLIENT_ID
                                + "&client_secret=" + Params.CLIENT_SECRET
                                + "&grant_type=" + Params.GRANT_TYPE
                                + "&code=" + Params.AUTH_CODE
                                + "&redirect_uri=" + Params.REDIRECT_URI))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(accessServer + Params.TOKEN_PART))
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        Params.TOKEN_CODE = responseJson.get("access_token").getAsString();
    }
}
