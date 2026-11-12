package client;

import java.util.Scanner;

public class PreloginClient {
    private final ServerFacade serverFacade;

    public PreloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    // main running loop for the game
    public void run(){
        System.out.println("Welcome to 240 Chess. Type 'help' to get started.");
        Scanner scanner = new Scanner(System.in);
        String userInput = "";

        // main internal loop, stops when quit is put in by user
        while(!userInput.equals("quit")){
            //because theyre not logged in (PRElogin) it says logged out b4 everything
            System.out.print("[LOGGED OUT] >>> ");
            userInput = scanner.nextLine().trim();

            //in order to avoid errors being thrown if the input is garblediegook
            try {
                String command = evaluate(userInput);
            }
            catch (Exception error){
                System.out.println("Input not recognized, valid commands are as follows:");
                System.out.println("create <NAME> - create game with name <NAME>");
                System.out.println("list - list all games");
                System.out.println("observe <ID> - observe game with ID");
                System.out.println("logout - logout of account");
                System.out.println("quit - quit program");
                System.out.println("help - list available commands");

            }
        }


    }
    // reads the command input and outputs accordingly as well as cleaning up the command
    public String evaluate(String input){
        String[] tokens = input.split("\\s+");
        String command = tokens.length > 0 ? tokens[0].toLowerCase() : "";
        if (command.equals("help")) {
            System.out.println("register - create an account");
            System.out.println("login - login to an existing account");
            System.out.println("quit - quit program");
            System.out.println("help - list available commands");
        }
    }
}
