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
                String result = evaluate(userInput);
                if (!result.equals("quit") && !result.equals("help")) {
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
        String help = "register - create an account " +
                "\nlogin - login to an existing account" +
                "\nquit - quit program" +
                "\nhelp - list available commands";
        if (command.equals("help")) {
            System.out.println(help);
            return "help";
        }
        else if (command.equals("register")){
            return register(tokens);
        }
        else if (command.equals("login")){
            return login(tokens);
        }
        else if (command.equals("quit")){
            return "quit";
        }
        else{
            throw new RuntimeException("Unknown command, valid commands are as follows:\n" + help);
        }
    }

    // function to actually register instead of just saying it
    public String register(String[] tokens) {
        if (tokens.length == 4){
            try{
                // tries to register with the user pass and email (index 0 is "register")
                var authData = serverFacade.register(tokens[1], tokens[2], tokens[3]);
                // makes the token the authData's token and welcomes user
                this.authToken = authData.authToken();
                String username = authData.username();
                System.out.println("Registered and logged in as " + username);
                //this shifts gears to the postlogin stuff
                PostloginClient postloginClient = new PostloginClient(serverFacade, authToken);
                postloginClient.run();
                // once this is done running, logs out
                return ("Logout successful");
            } catch (Exception error){
                return "Registration failed: " + error.getMessage();
            }
        }
        return "Usage: register <USERNAME> <PASSWORD> <EMAIL>";
    }

    public String login(String[] tokens) {
        if (tokens.length == 3){
            try{
                // tries to log in with the username and password (index 0 is "login")
                var authData = serverFacade.login(tokens[1], tokens[2]);
                // makes the token the authData's token and welcomes user
                this.authToken = authData.authToken();
                String username = authData.username();
                // switches to postlogin
                System.out.println("Logged in as " + username);
                PostloginClient postloginClient = new PostloginClient(serverFacade, authToken);
                postloginClient.run();
                // log out
                return ("Logout successful");
            } catch (Exception error){
                return "Login failed: " + error.getMessage();
            }
        }
        return "Usage: login <USERNAME> <PASSWORD>";
    }
}

//System.out.println("create <NAME> - create game with name <NAME>");
//                System.out.println("list - list all games");
//                System.out.println("observe <ID> - observe game with ID");
//                System.out.println("logout - logout of account");
//                System.out.println("quit - quit program");
//                System.out.println("help - list available commands");