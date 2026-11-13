package client;

import chess.*;

public class ChessBoardDisplay {
    // colors for the squares
    private static final String reset = "\u001B[0m";
    private static final String whiteSquare = "\u001B[47m";
    private static final String blackSquare = "\u001B[100m";
    private static final String whiteText = "\u001B[37m";
    private static final String blackText = "\u001B[30m";
    // \u2003 is emspace

    public static void printDefaultBoard(ChessBoard board, String playerColor){
        //perspective determiner
        boolean whitePov = true;
        if (playerColor != null && playerColor.toLowerCase().equals("black")){
            whitePov = false;
        }

        System.out.println("    a  b  c  d  e  f  g  h  ");
        // rows

        for (int r = 8; r > 0; r--){
            System.out.print(r + " ");
            // this makes the black
            boolean isBlack = r % 2 == 0;
            // cols
            for (int c = 1; c <= 8; c++){

                String squareColor;
                if(isBlack){
                    squareColor = blackSquare;
                }
                else{
                    squareColor = whiteSquare;
                }
                isBlack = !isBlack;
                // gets the piece at r c
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                System.out.print(squareColor + "\u2003" + pieceChar(piece) + "\u2003" + reset);
            }
            System.out.println(" " + r);
        }

        System.out.println("    a  b  c  d  e  f  g  h  ");

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
