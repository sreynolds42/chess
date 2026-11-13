package client;

public class ChessBoard {


    public static void printDefaultBoard(){
        System.out.println("    a  b  c  d  e  f  g  h  ");
        // rows
        for (int r = 8; r > 0; r--){
            System.out.print(r + " ");
        }
        // cols
        for (int c = 0; c < 9; c++){
            System.out.print(c + " ");
        }
    }
}
