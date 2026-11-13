package client;

import chess.*;

public class ChessBoard {
    // colors for the squares
    private static final String RESET = "\u001B[0m";
    private static final String BG_LIGHT = "\u001B[47m"; // Light squares
    private static final String BG_DARK = "\u001B[100m";  // Dark squares
    private static final String TEXT_WHITE = "\u001B[37m";
    private static final String TEXT_BLACK = "\u001B[30m";

    public static void printDefaultBoard(){
        System.out.println("    a  b  c  d  e  f  g  h  ");
        // rows
        for (int r = 8; r > 0; r--){
            System.out.print(r + " ");
            // cols
            for (int c = 0; c < 9; c++){
                System.out.print("\u2003 "); // \u2003 is emspace
            }
            System.out.println(r);
        }

        System.out.println("    a  b  c  d  e  f  g  h  ");

    }
}
