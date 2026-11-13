package client;

import chess.*;

public class ChessBoardDisplay {
    // colors for the squares
    private static final String RESET = "\u001B[0m";
    private static final String WHITE_SQUARE = "\u001B[47m";
    private static final String BLACK_SQUARE = "\u001B[100m";
    // \u2003 is emspace

    public static void printDefaultBoard(ChessBoard board, String playerColor){
        //perspective determiner
        boolean whitePov = true;
        if (playerColor != null && playerColor.toLowerCase().equals("black")){
            whitePov = false;
        }

        // flips coords based on pov
        int scaler;
        if (whitePov){
            scaler = 1;
            System.out.println("    a  b  c  d  e  f  g  h  ");
        }
        else{
            scaler = -1;
            System.out.println("    h  g  f  e  d  c  b  a  ");
        }

        // rows
        // if scaler is 1, r = 8, stops at 0
        // if scaler is -1, r = 1, ends at 9
        for (int r = (9 + 7 * scaler) / 2; r != (9 - 9 * scaler) / 2; r -= scaler){
            System.out.print(r + " ");
            // this makes the black
            boolean isBlack = r % 2 == 0;
            // cols with the same logic as rows
            for (int c = (9 - 7 * scaler) / 2; c != (9 + 9 * scaler) / 2; c += scaler){

                String squareColor;
                if(isBlack){
                    squareColor = BLACK_SQUARE;
                }
                else{
                    squareColor = WHITE_SQUARE;
                }
                isBlack = !isBlack;
                // gets the piece at r c
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                System.out.print(squareColor + "\u2003" + pieceChar(piece) + "\u2003" + RESET);
            }
            System.out.println(" " + r);
        }

        if (whitePov){
            System.out.println("    a  b  c  d  e  f  g  h  ");
        }
        else{
            System.out.println("    h  g  f  e  d  c  b  a  ");
        }

    }
    // turns pieces into unicode chars
    private static String pieceChar(ChessPiece piece){
        String character = "";

        // checks for null for an empty space
        if (piece == null){
            return " ";
        }
        // looks for piece type and color, and sets the according unicode char
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                character = "♙";
            }
            else{
                character = "♟";
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                character = "♖";
            }
            else{
                character = "♜";
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                character = "♘";
            }
            else{
                character = "♞";
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                character = "♗";
            }
            else{
                character = "♝";
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                character = "♕";
            }
            else{
                character = "♛";
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                character = "♔";
            }
            else{
                character = "♚";
            }
        }
        return character;
    }
}
