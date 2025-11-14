package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class LoggedInClient {


    private State state = State.LOGGED_IN;
    private final ServerFacade server;
    private AuthData myauth;
    private ArrayList<GameInfo> theGameList;

    public LoggedInClient(String serverUrl, AuthData auth)  {
        server = new ServerFacade(serverUrl);
        myauth = auth;
    }

    public void run() {
        System.out.println( " You're logged in big boy. type help");

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
                case "join" -> joinGame(params);
                case "list" -> listGames();
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

    public String listGames() throws ResponseException {

        theGameList = server.listGames(myauth).games();

        String games = "";
        for(int i = 0; i < theGameList.size(); i++ ) {
            var spot = theGameList.get(i);
            games += "Game " + i + ": " + spot.gameName() + " : white: " + spot.whiteUsername() +
                    ", black: " + spot.blackUsername() + "\n";
        }
        return games;
    }

    public String joinGame(String... params) throws ResponseException {
        var color = params[0].toUpperCase();
        var chosenID = Integer.parseInt(params[1]);
        var iD = theGameList.get(chosenID).gameID();
        JoinGamer joiner = new JoinGamer(color, iD);

        server.joinGame(joiner, myauth);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var toPrint = boardString(board);
        printBoard(toPrint);
        return "";
    }


    private String[][] boardString(ChessBoard board) {
        String[][] boardString = new String[8][8];
        for (int i = 1; i <= 8 ; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                var piece = board.getPiece(pos);
                if(piece == null){
                    boardString[i-1][j-1] = "!";
                }else {
                    boardString[i-1][j-1] = piece.getPieceType().name().substring(0,1);
                }

            }
        }
        return boardString;
    }

    private void printBoard(String[][] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            for (int j = 0; j <toPrint[i].length ; j++) {
                System.out.print(toPrint[i][j] + "  ");
            }
            System.out.println();
        }
    }


    public String help() {
        return """
                - help
                - logout
                - create <game name>
                - list (must list before joining)
                - join <player color> <game id from list>
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
