import com.google.gson.Gson;
import model.*;
import server.JoinRequest;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData newUser) throws Exception {
        String path = "/user";
        return this.makeRequest("POST", path, newUser, null, AuthData.class);
    }

    public AuthData login(UserData user) throws Exception {
        String path = "/session";
        return this.makeRequest("POST", path, user, null, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        String path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
    }

    public Collection<GameData> list(String authToken) throws Exception {
        String path = "/game";
        record listResponse(Collection<GameData> games) {
        }
        listResponse response = this.makeRequest("GET", path, null, authToken, listResponse.class);
        return response.games();
    }

    public GameData create(GameData game, String authToken) throws Exception {
        String path = "/game";
        return this.makeRequest("POST", path, game, authToken, GameData.class);
    }

    public void join(JoinRequest joinRequest, String authToken) throws Exception {
        String path = "/game";
        this.makeRequest("PUT", path, joinRequest, authToken, null);
    }

    public void clear() throws Exception {
        String path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, header, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }


    private static void writeBody(Object request, String header, HttpURLConnection http) throws IOException {
        http.addRequestProperty("Content-Type", "application/json");
        if (header != null) {
            http.addRequestProperty("Authorization", header);
        }
        if (request != null) {
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        int status = http.getResponseCode();

        if (!isSuccessful(status)) {
            InputStream errorStream = http.getErrorStream();
            InputStreamReader errorMessage = new InputStreamReader(errorStream);
            Map<String, String> messageMap = new Gson().fromJson(errorMessage, Map.class);
            String message = messageMap.get("message");
            throw new Exception(message);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status == 200;
    }
}