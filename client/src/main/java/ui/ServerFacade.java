package ui;

import com.google.gson.Gson;
import ui.ResponseException;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }



    public AuthData login(UserData user) throws ResponseException {
        var request = buildRequest("POST", "/session", user, null);
        var response = sendRequest(request);
        try {
            return handleResponse(response, AuthData.class);
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, "user does not exist");
        }


    }

    public AuthData register(UserData user) throws ResponseException {
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void create(GameName name, AuthData auth) throws ResponseException {
        var request = buildRequest("POST", "/game", name, auth);
        sendRequest(request);
    }

    public void joinGame (JoinGamer joiner, AuthData auth) throws ResponseException {
        var request = buildRequest("PUT", "/game", joiner, auth);
        var response = sendRequest(request);
        try {
            handleResponse(response, GameData.class);
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, "sorry! can't join that game!\n" +
                    "make sure that  that there are no\n " +
                    "players assigned to your desired color!");
        }

    }


    public GameList listGames (AuthData auth) throws ResponseException {
        var request = buildRequest("GET", "/game", null, auth);
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, GameList.class);
    }
    private HttpRequest buildRequest(String method, String path, Object body, AuthData header) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        } if (header != null) {
            request.header("authorization", header.authToken());
        }
        return request.build();
    }





    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public String getURL() {
        return serverUrl;
    }


}
