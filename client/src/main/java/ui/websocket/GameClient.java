package ui.websocket;

import model.AuthData;
import model.GameInfo;
import ui.ResponseException;
import ui.ServerFacade;
import ui.State;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class GameClient implements NotificationHandler{
    private State state = State.IN_GAME;
    private final ServerFacade server;
    private final AuthData myauth;
    private final int iD;
    private ArrayList<GameInfo> theGameList;
    private final WebSocketFacade ws;

    public GameClient(String serverUrl, AuthData auth, int iD) throws Exception {
        server = new ServerFacade(serverUrl);
        myauth = auth;
        this.iD = iD;
        ws = new WebSocketFacade(serverUrl,this);
    }

    public void run() throws ResponseException {
        ws.connect(myauth.authToken(), iD );
        System.out.println( " You're in the game");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("you left the game")) {
            result = getResult(scanner, result);
        }
        System.out.println();
    }

    private String getResult(Scanner scanner, String result) {
        printPrompt();
        String line = scanner.nextLine();
        try {
            result = eval(line);
            System.out.print(result);
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(msg);
        }
        return result;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "makeMove" -> makeMove(params);
                case "resign"-> resign();
                case "highlight" -> highlight();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String highlight() {
        return "highlighted board";
    }

    private String resign() throws ResponseException {
        ws.resign(myauth.authToken(), iD);
        return "you resigned";
    }

    private String makeMove(String[] params) {
        return "made move";
    }

    private String redraw() {

        return "board";
    }


    @Override
    public void notify(NotificationMessage message) {
            System.out.println(message.getMessage());

    }

    @Override
    public void load(ServerMessage message) {

    }

    @Override
    public void error(ServerMessage message) {

    }


    public String help() {
        return """
                - help
                - redraw
                - leave
                - makeMove
                - resign
                - highlight
                """;
    }
    public String leave() throws ResponseException {
        ws.leave(myauth.authToken(),iD);
        state = State.LOGGED_IN;
        return "you left the game";
    }



    private void printPrompt() {
        System.out.print("\n" + state  + " >>> " );
    }
}
