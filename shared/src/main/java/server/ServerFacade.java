package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import model.GameData;
import model.JoinGameRequest;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthData register(UserData user) throws ResponseException {
        var path = "/user";
        String responseJson = makeRequest("POST", path, user, null);
        return gson.fromJson(responseJson, AuthData.class);
    }

    public AuthData login(UserData user) throws ResponseException {
        var path = "/session";
        String responseJson = makeRequest("POST", path, user, null);
        return gson.fromJson(responseJson, AuthData.class);
    }

    public void logout(AuthData authData) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, null, authData.authToken());
    }

    public int createGame(AuthData authData) throws ResponseException {
        var path = "/game";
        String responseJson = makeRequest("POST", path, null, authData.authToken());
        return gson.fromJson(responseJson, Integer.class);
    }

    public GameData[] listGames(AuthData authData) throws ResponseException {
        var path = "/game";
        String responseJson = makeRequest("GET", path, null, authData.authToken());
        return gson.fromJson(responseJson, GameData[].class);
    }

    public void joinGame(AuthData authData, int gameID, String playerColor) throws ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(gameID, playerColor);
        makeRequest("POST", path, request, authData.authToken());
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
                http.setRequestProperty("Authorization", "Bearer " + authToken);
            }

            OutputStream os = http.getOutputStream();
            os.write(gson.toJson(request).getBytes());

            http.connect();
            throwIfNotSuccessful(http);
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
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private String readBody(HttpURLConnection http) throws IOException {
        try (InputStream respBody = http.getInputStream()) {
            return new String(respBody.readAllBytes());
        }
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
