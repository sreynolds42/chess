package client;

import java.util.Scanner;

public class PreloginClient {
    private final ServerFacade serverFacade;
    private String authToken = null;

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
                evaluate("help");

            }
        }


    }
    // reads the command input and outputs accordingly as well as cleaning up the command
    public String evaluate(String input){
        String[] tokens = input.split("\\s+");
        String command = tokens.length > 0 ? tokens[0].toLowerCase() : "";
        String help = "register - create an account " +
                "\nlogin - login to an existing account" +
                "\nquit - quit program" +
                "\nhelp - list available commands";
        if (command.equals("help")) {
            System.out.println(help);
            return "help";
        }
        else if (command.equals("register")){
            // makes sure arg number is correct
            if (tokens.length != 4){
                throw new RuntimeException("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            }
            try {
                // tries putting in the vals as tokens, and if it works registers
                String username = tokens[1];
                String password = tokens[2];
                String email = tokens[3];
                System.out.println("Registration successful");
                return "register";
            } catch (Exception error){
                throw new RuntimeException("Registration unsuccessful");
            }

            //implement register
        }
        else if (command.equals("login")){
            // makes sure arg number is correct
            if (tokens.length != 3){
                throw new RuntimeException("Usage: login <USERNAME> <PASSWORD>");
            }
            try {
                // tries putting in the vals as tokens, and if it works registers
                String username = tokens[1];
                String password = tokens[2];
                System.out.println("Login successful");
                return "login";
            } catch (Exception error){
                throw new RuntimeException("Login unsuccessful");
            }
            // implement login
        }
        else if (command.equals("quit")){
            return "quit";
            // implement quit
        }
        else{
            throw new RuntimeException("Unknown command, valid commands are as follows:\n" + help);
        }
    }
    // function to actually tegister instead of just saying it
    public String register(String[] tokens) throws Exception{
        if (tokens.length != 4){
            try{
                // tries to register with the user pass and email
                var authData = serverFacade.register(tokens[0], tokens[1], tokens[2]);
                // makes the token the authData's token and welcomes user
                this.authToken = authData.authToken();
                String username = authData.username();
                return "Registered and logged in as " + username;
            } catch (Exception error){
                return "Registration failed: " + error.getMessage();
            }
        }
        return ("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
    }
    public String login(String[] tokens) throws Exception{
        if (tokens.length != 3){
            try{
                // tries to log in with the username and password
                var authData = serverFacade.login(tokens[0], tokens[1]);
                // makes the token the authData's token and welcomes user
                this.authToken = authData.authToken();
                String username = authData.username();
                return "Logged in as " + username;
            } catch (Exception error){
                return "Login failed: " + error.getMessage();
            }
        }
        return ("Usage: login <USERNAME> <PASSWORD>");
    }

}



//System.out.println("create <NAME> - create game with name <NAME>");
//                System.out.println("list - list all games");
//                System.out.println("observe <ID> - observe game with ID");
//                System.out.println("logout - logout of account");
//                System.out.println("quit - quit program");
//                System.out.println("help - list available commands");