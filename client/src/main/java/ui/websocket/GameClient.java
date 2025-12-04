package ui.websocket;

import model.AuthData;
import model.GameInfo;
import ui.ServerFacade;
import ui.State;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class GameClient implements NotificationHandler{
    private State state = State.LOGGED_IN;
    private final ServerFacade server;
    private final AuthData myauth;
    private ArrayList<GameInfo> theGameList;
    private final WebSocketFacade ws;

    public GameClient(String serverUrl, AuthData auth) throws Exception {
        server = new ServerFacade(serverUrl);
        myauth = auth;
        ws = new WebSocketFacade(serverUrl,this);
    }

    public void run() {
        System.out.println( " You're in the game");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
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
                case "leave" -> logout();
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

    private String resign() {
        return "resign big boy";
    }

    private String makeMove(String[] params) {
        return "made move";
    }

    private String redraw() {

        return "board";
    }


    @Override
    public void notify(ServerMessage message) {

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
    public String logout() {
        state = State.LOGGED_OUT;
        return "logout";
    }



    private void printPrompt() {
        System.out.print("\n" + state  + " >>> " );
    }
}
