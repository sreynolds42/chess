package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(getPieceType() == PieceType.ROOK){
            return cross(board, myPosition);
        }
        if(type == PieceType.KNIGHT){
            return horse(board, myPosition);
        }
        if(type == PieceType.BISHOP){
            return diag(board, myPosition);
        }
        if(type == PieceType.KING){
            return king(board, myPosition);
        }
        if(type == PieceType.QUEEN){
            Collection<ChessMove> moves = new ArrayList<>();
            moves.addAll(cross(board, myPosition));
            moves.addAll(diag(board, myPosition));
            return moves;
        }
        if(type == PieceType.PAWN){
            return evilAhhPawns(board, myPosition);
        }
        return cross(board, myPosition);
    }

    public Collection<ChessMove> cross(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        //up

        for(int r = myPosition.getRow() + 1; r <= 8; r++){
            ChessPosition checkPos = new ChessPosition(r, myPosition.getColumn());
            if(board.getPiece(checkPos) == null){
                legal.add(new ChessMove(myPosition, checkPos, null));
            }
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }

        //down
        for(int r = myPosition.getRow() - 1; r >= 1; r--){
            ChessPosition checkPos = new ChessPosition(r, myPosition.getColumn());
            if(board.getPiece(checkPos) == null) legal.add(new ChessMove(myPosition, checkPos, null));
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }

        //right
        for(int c = myPosition.getColumn() + 1; c <= 8; c++){
            ChessPosition checkPos = new ChessPosition(myPosition.getRow(), c);
            if(board.getPiece(checkPos) == null) legal.add(new ChessMove(myPosition, checkPos, null));
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }
        //left
        for(int c = myPosition.getColumn() - 1; c >= 1; c--){
            ChessPosition checkPos = new ChessPosition(myPosition.getRow(), c);
            if(board.getPiece(checkPos) == null) legal.add(new ChessMove(myPosition, checkPos, null));
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }

        return legal;
    }

    public Collection<ChessMove> diag(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();

        // up right
        for(int r = myPosition.getRow() + 1, c = myPosition.getColumn() + 1; r <= 8 && c <= 8; r++, c++){
            ChessPosition checkPos = new ChessPosition(r, c);
            if(board.getPiece(checkPos) == null) legal.add(new ChessMove(myPosition, checkPos, null));
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }

        // up left
        for(int r = myPosition.getRow() + 1, c = myPosition.getColumn() - 1; r <= 8 && c >= 1; r++, c--){
            ChessPosition checkPos = new ChessPosition(r, c);
            if(board.getPiece(checkPos) == null) legal.add(new ChessMove(myPosition, checkPos, null));
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }

        // down right
        for(int r = myPosition.getRow() - 1, c = myPosition.getColumn() + 1; r >= 1 && c <= 8; r--, c++){
            ChessPosition checkPos = new ChessPosition(r, c);
            if(board.getPiece(checkPos) == null) legal.add(new ChessMove(myPosition, checkPos, null));
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }

        // down bad
        for(int r = myPosition.getRow() - 1, c = myPosition.getColumn() - 1; r >= 1 && c >= 1; r--, c--){
            ChessPosition checkPos = new ChessPosition(r, c);
            if(board.getPiece(checkPos) == null) legal.add(new ChessMove(myPosition, checkPos, null));
            else if(board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            }
            else break;
        }


        return legal;
    }

    public Collection<ChessMove> horse(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        int horseMoves[][] = {{1,2},{2,1},{-1,2},{-2,1},{-1,-2},{-2,-1},{1,-2},{2,-1}};
        for(int move[] : horseMoves){
            if(myPosition.getRow() + move[0] >= 1 && myPosition.getRow() + move[0] <= 8 && myPosition.getColumn() + move[1] >= 1 && myPosition.getColumn() + move[1] <= 8) {
                ChessPosition checkPos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                if(board.getPiece(checkPos) == null || board.getPiece(checkPos).getTeamColor() != pieceColor){
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }

        return legal;
    }

    public Collection<ChessMove> king(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        int kingMoves[][] = {{0,1},{1,0},{1,1},{0,-1},{-1,-1},{-1,0},{1,-1},{-1,1}};
        for(int move[] : kingMoves){
            if(myPosition.getRow() + move[0] >= 1 && myPosition.getRow() + move[0] <= 8 && myPosition.getColumn() + move[1] >= 1 && myPosition.getColumn() + move[1] <= 8) {
                ChessPosition checkPos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                if(board.getPiece(checkPos) == null || board.getPiece(checkPos).getTeamColor() != pieceColor){
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }

        return legal;
    }
    //4.5 + 2.5d = 7
    public  Collection<ChessMove> evilAhhPawns(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legal = new ArrayList<>();
        int d = 1;
        if(pieceColor == ChessGame.TeamColor.BLACK){
            d = -1;
        }

        //eat sides
        for(int c = myPosition.getColumn() - 1; c <= myPosition.getColumn() + 1; c += 2){
            // is it legal
            ChessPosition checkPos = new ChessPosition(myPosition.getRow() + d, c);
            if(c >= 1 && c <= 8 && board.getPiece(checkPos) != null && board.getPiece(checkPos).pieceColor != pieceColor){
                //is it a promotion
                if(checkPos.getRow() == 8 || checkPos.getRow() == 1){
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.BISHOP));
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.QUEEN));
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.ROOK));
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.KNIGHT));
                }
                else{
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }
        //long jump
        if(myPosition.getRow() == (int) (4.5 - (2.5 * d))){
            if (board.getPiece(new ChessPosition(myPosition.getRow() + d, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow() + d + d, myPosition.getColumn())) == null){
                legal.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + d + d, myPosition.getColumn()), null));
            }
        }
        //regular move
        //is it a promotion
        if(board.getPiece(new ChessPosition(myPosition.getRow() + d, myPosition.getColumn())) == null) {
            ChessPosition checkPos = new ChessPosition(myPosition.getRow() + d, myPosition.getColumn());
            if (checkPos.getRow() == 8 || checkPos.getRow() == 1) {
                legal.add(new ChessMove(myPosition, checkPos, PieceType.BISHOP));
                legal.add(new ChessMove(myPosition, checkPos, PieceType.QUEEN));
                legal.add(new ChessMove(myPosition, checkPos, PieceType.ROOK));
                legal.add(new ChessMove(myPosition, checkPos, PieceType.KNIGHT));
                // if not
            } else {
                legal.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + d, myPosition.getColumn()), null));
            }
        }


        return legal;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return Objects.equals(pieceColor, that.pieceColor) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode(){
        return Objects.hash(pieceColor, type);
    }
}
