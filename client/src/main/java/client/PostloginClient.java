package client;

import model.GameData;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostloginClient {
    private final ServerFacade serverFacade;
    private String authToken;
    // tjis is so that the IDs can count up from 1, 2, 3 etc.
    private List<GameData> numberedGames = new ArrayList<>();

    public PostloginClient(ServerFacade serverFacade, String authToken) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;
    }

    // main running loop for the game
    public void run(){
        System.out.println("Welcome to 240 Chess. Type 'help' to get started.");
        Scanner scanner = new Scanner(System.in);
        String userInput = "";

        // main internal loop, stops when quit OR logout is put in by user
        while(!userInput.equals("quit") && !userInput.equals("logout")){
            //because they ARE logged in (POSTlogin) it says logged in b4 everything
            System.out.print("[LOGGED IN] >>> ");
            userInput = scanner.nextLine().trim();

            //in order to avoid errors being thrown if the input is garblediegook, and to make sure it doesnt print empty lines
            try {
                String result = evaluate(userInput);
                if (!result.equals("quit") && !result.equals("help") && !result.isEmpty()) {
                    System.out.println(result);
                }
            }
            catch (Exception error){
                System.out.println(error.getMessage());
            }
        }

    }

    // reads the command input and outputs accordingly as well as cleaning up the command
    public String evaluate(String input) throws Exception {
        String[] tokens = input.split("\\s+");
        String command = tokens.length > 0 ? tokens[0].toLowerCase() : "";
        String help = "create <NAME> - create game with name <NAME>" +
                "\nlist - list all games" +
                "\nplay <ID> [WHITE|BLACK] - join a game" +
                "\nobserve <ID> - observe game with ID" +
                "\nlogout - logout of account" +
                "\nquit - quit program" +
                "\nhelp - list available commands";

        if (command.equals("help")) {
            if (tokens.length > 1) {
                return "Usage: help";
            }
            System.out.println(help);
            return "help";
        }
        else if (command.equals("create")){
            return create(tokens);
        }
        else if (command.equals("list")){
            return list();
        }
        else if (command.equals("play")){
            return play(tokens);
        }
        else if (command.equals("observe")){
            return observe(tokens);
        }
        else if (command.equals("logout")){
            if (tokens.length > 1) {
                return "Usage: logout";
            }
            return logout();
        }
        else if (command.equals("quit")){
            if (tokens.length > 1) {
                return "Usage: quit";
            }
            logout();
            System.exit(0);
            return "quit";
        }
        else{
            throw new RuntimeException("Unknown command, valid commands are as follows:\n" + help);
        }
    }

    // function to actually register instead of just saying it
    public String create(String[] tokens) {
        if (tokens.length == 2) {
            try {
                // makes a game with tokens[1] (the name)
                serverFacade.createGame(authToken, tokens[1]);
                return ("Successfully created game " + tokens[1]);
            } catch (Exception error) {
                return "Failed to create game: " + error.getMessage();
            }
        }
        return "Usage: create <NAME>";
    }

    public String list(){
        try {
            var result = serverFacade.listGames(authToken);
            if (result.games().isEmpty()){
                return "No games found.";
            }
            numberedGames = new ArrayList<>(result.games());
            String gameList = new String();
            int gameNumber = 1;
            for(var game : numberedGames){
                // adds a line that goes Game number: <Game number> | Name: <Name>/n for each game in the list.
                String whitePlayer = "Empty";
                String blackPlayer = "Empty";
                if(game.whiteUsername() != null){
                    whitePlayer = game.whiteUsername();
                }
                if(game.blackUsername() != null){
                    blackPlayer = game.blackUsername();
                }
                gameList = gameList + "Game #: " + gameNumber
                        + " | Name: " + game.gameName()
                        + " | White player: " + whitePlayer
                        + " | Black player: " + blackPlayer
                        + "\n";
                gameNumber++;
            }
            return gameList;

        } catch (Exception error) {
            return "Failed to get list: " + error.getMessage();
        }
    }

    public String play(String[] tokens) {
        if (tokens.length == 3){
            try {
                int ID = numberedGames.get(Integer.parseInt(tokens[1]) - 1).gameID();
                String color = tokens[2].toUpperCase();
                serverFacade.joinGame(authToken, ID, color);

                //board drawing -- to be replaced
                chess.ChessBoard board = new chess.ChessBoard();
                board.resetBoard();
                ChessBoardDisplay.printDefaultBoard(board, color);

                return ("Successfully joined game " + tokens[1] + " as " + color);
            } catch (Exception error) {
                // this checks what KIND of error it is
                if (!tokens[1].matches("\\d+")) {
                    return "Failed to join game: Game ID must be a valid number";
                }
                if (!tokens[2].toUpperCase().matches("WHITE") && !tokens[2].toUpperCase().matches("BLACK")) {
                    return "Failed to join game: Color must be white or black";
                }
                return "Failed to join game: " + error.getMessage();
            }
        }
        return "Usage: play <ID> [WHITE|BLACK]";
    }

    public String observe(String[] tokens) {
        if (tokens.length == 2){
            try {
                int ID = numberedGames.get(Integer.parseInt(tokens[1]) - 1).gameID();
                serverFacade.joinGame(authToken, ID, null);

                //board drawing -- to be replaced
                chess.ChessBoard board = new chess.ChessBoard();
                board.resetBoard();
                ChessBoardDisplay.printDefaultBoard(board, null);

                return ("Successfully joined game " + tokens[1] + " as observer");
            } catch (Exception error) {
                return "Failed to join game as observer: " + error.getMessage();
            }
        }
        return "Usage: observe <ID>";
    }

    public String logout() {
        try {
            serverFacade.logout(authToken);
            return "";
        } catch (Exception error) {
            return "Logout failed: " + error.getMessage();
        }
    }
}
