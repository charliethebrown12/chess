package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthData register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        UserData user = new UserData(username, password, email);
        String responseJson = makeRequest("POST", path, user, null);
        return gson.fromJson(responseJson, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        UserData user = new UserData(username, password, null);
        String responseJson = makeRequest("POST", path, user, null);
        return gson.fromJson(responseJson, AuthData.class);
    }

    public void logout(AuthData authData) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, null, authData.authToken());
    }

    public int createGame(AuthData authData, String gameName) throws ResponseException {
        var path = "/game";
        Map<String, String> request = Map.of("gameName", gameName);
        String responseJson = makeRequest("POST", path, request, authData.authToken());
        JsonObject json = gson.fromJson(responseJson, JsonObject.class);
        return json.get("gameID").getAsInt();
    }

    public GameData[] listGames(AuthData authData) throws ResponseException {
        var path = "/game";
        String responseJson = makeRequest("GET", path, null, authData.authToken());

        return gson.fromJson(responseJson, ListGamesResponse.class).games();
    }

    public void joinGame(AuthData authData, int gameID, String playerColor) throws ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(gameID, playerColor);
        makeRequest("PUT", path, request, authData.authToken());
    }

    public void deleteAll() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null);
    }

    private String makeRequest(String method, String path, Object request, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            if (request != null) {
                OutputStream os = http.getOutputStream();
                os.write(gson.toJson(request).getBytes());
                System.out.println(os);
            }

            http.connect();
            throwIfNotSuccessful(http);
            http.getResponseMessage();
            return readBody(http);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            String errorJson = readBody(http);
            ErrorData errorData = gson.fromJson(errorJson, ErrorData.class);
            throw new ResponseException(status, errorData.message());
        }
    }

    private String readBody(HttpURLConnection http) throws IOException {
        try (InputStream respBody = http.getInputStream()) {
            String response = new String(respBody.readAllBytes());
            System.out.println(response);
            return response;
        }
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
