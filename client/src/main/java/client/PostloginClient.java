package client;

import java.util.Scanner;

public class PostloginClient {
    private final ServerFacade serverFacade;
    private String authToken = null;

    public PostloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
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
        String help = "create <NAME> - create game with name <NAME>" +
                "\nlist - list all games" +
                "\nplay <ID> [WHITE|BLACK] - join a game" +
                "\nobserve <ID> - observe game with ID" +
                "\nlogout - logout of account" +
                "\nquit - quit program" +
                "\nhelp - list available commands";

        if (command.equals("help")) {
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
            return logout();
        }
        else if (command.equals("quit")){
            return "quit";
        }
        else{
            throw new RuntimeException("Unknown command, valid commands are as follows:\n" + help);
        }
    }

    // function to actually register instead of just saying it
    public String create(String[] tokens) {
        if (tokens.length == 2){
            try{

            }
        }
        return "Usage: create <NAME>";
    }

    public String list(){

    }

    public String play(String[] tokens) {
        if (tokens.length == 3){
            try{

            }
        }
        return "Usage: play <ID> [WHITE|BLACK]";
    }

    public String observe(String[] tokens) {
        if (tokens.length == 2){
            try{

            }
        }
        return "Usage: observe <ID>";
    }

    public String logout() {

    }
}
