package client;

import chess.*;

public class ChessBoard {
    // colors for the squares
    private static final String reset = "\u001B[0m";
    private static final String whiteSquare = "\u001B[47m";
    private static final String blackSquare = "\u001B[100m";
    private static final String whiteText = "\u001B[37m";
    private static final String blackText = "\u001B[30m";
    // \u2003 is emspace

    public static void printDefaultBoard(ChessBoard board, String color){
        System.out.println("    a  b  c  d  e  f  g  h  ");
        // rows
        boolean isBlack = false;
        for (int r = 8; r > 0; r--){
            System.out.print(r + " ");
            // cols
            for (int c = 0; c < 9; c++){

                String squareColor;
                if(isBlack){
                    squareColor = blackSquare
                }
                System.out.print("\u2003 ");
            }
            System.out.println(r);
        }

        System.out.println("    a  b  c  d  e  f  g  h  ");

    }
}
