package ui;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {


    private State state = State.LOGGED_OUT;
    private final ServerFacade server;

    public ChessClient(String serverUrl)  {
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

        state = State.LOGGED_IN;

        return "you logged in";
    }

    private void printPrompt() {
        System.out.print(state  + " >>> " );
    }

}
