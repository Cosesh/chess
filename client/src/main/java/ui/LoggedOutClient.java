package ui;
import model.AuthData;
import model.UserData;

import java.util.Arrays;
import java.util.Scanner;

public class LoggedOutClient {


    private State state = State.LOGGED_OUT;
    private final ServerFacade server;
    private AuthData myauth;

    public LoggedOutClient(String serverUrl)  {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println( " Welcome to Colton's Chess Lobby. Press enter for options");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
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
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    public String help() {
        return """
                - login <USERNAME> <PASSWORD>
                - register <USERNAME> <PASSWORD> <EMAIL>
                - quit
                - help
                """;
    }
    public String quit() {
        return "quit";
    }

    public String login (String... params) {


        var name = params[1];
        var pass = params[2];

        return "that was so fun";
    }

    public String register (String... params) throws ResponseException {
        state = State.LOGGED_IN;
        var name = params[0];
        var pass = params[1];
        var email = params[2];
        UserData user = new UserData(name, pass, email);
        server.register(user);
        var logged = new LoggedInClient(server.getURL());
        logged.run();
        return "you registered a new user";
    }

    private void printPrompt() {
        System.out.print("\n" + state  + " >>> " );
    }

}
