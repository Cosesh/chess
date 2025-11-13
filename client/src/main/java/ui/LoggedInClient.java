package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.GameName;
import model.UserData;

import java.util.Arrays;
import java.util.Scanner;

public class LoggedInClient {


    private State state = State.LOGGED_IN;
    private final ServerFacade server;
    private AuthData myauth;

    public LoggedInClient(String serverUrl, AuthData auth)  {
        server = new ServerFacade(serverUrl);
        myauth = auth;
    }

    public void run() {
        System.out.println( " You're logged big boy. Now what?");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> create(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String create(String... params) throws ResponseException {
        GameName name = new GameName(params[0]);
        server.create(name, myauth);
        return "That mamma jamma was low key created my guy";

    }




    public String help() {
        return """
                - help
                - logout
                - create game
                - list games
                - play game
                - observe game
                - kill bin laden
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
