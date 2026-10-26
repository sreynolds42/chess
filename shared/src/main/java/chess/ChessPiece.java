package chess;

import java.util.ArrayList;
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

    private void addLineMoves(ChessBoard board, ChessPosition myPosition,
                              Collection<ChessMove> legal, int rowStep, int colStep) {
        int r = myPosition.getRow() + rowStep;
        int c = myPosition.getColumn() + colStep;
        while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPosition checkPos = new ChessPosition(r, c);
            ChessPiece target = board.getPiece(checkPos);
            if (target == null) {
                legal.add(new ChessMove(myPosition, checkPos, null));
            } else {
                if (target.getTeamColor() != getTeamColor()) {
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
                break;
            }
            r += rowStep;
            c += colStep;
        }
    }

    public Collection<ChessMove> cross(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        addLineMoves(board, myPosition, legal, 1, 0);  // up
        addLineMoves(board, myPosition, legal, -1, 0); // down
        addLineMoves(board, myPosition, legal, 0, 1);  // right
        addLineMoves(board, myPosition, legal, 0, -1); // left
        return legal;
    }

    public Collection<ChessMove> diag(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        addLineMoves(board, myPosition, legal, 1, 1);   // up right
        addLineMoves(board, myPosition, legal, 1, -1);  // up left
        addLineMoves(board, myPosition, legal, -1, 1);  // down right
        addLineMoves(board, myPosition, legal, -1, -1); // down left
        return legal;
    }

    public Collection<ChessMove> horse(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        int[][] horseMoves = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {-1, -2}, {-2, -1}, {1, -2}, {2, -1}};
        for(int[] move : horseMoves){
            int targetRow = myPosition.getRow() + move[0];
            int targetCol = myPosition.getColumn() + move[1];
            if(targetRow >= 1 && targetRow <= 8 && targetCol >= 1 && targetCol <= 8) {
                ChessPosition checkPos = new ChessPosition(targetRow, targetCol);
                ChessPiece p = board.getPiece(checkPos);
                if(p == null || p.getTeamColor() != pieceColor){
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }
        return legal;
    }

    public Collection<ChessMove> king(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        int[][] kingMoves = {{0, 1}, {1, 0}, {1, 1}, {0, -1}, {-1, -1}, {-1, 0}, {1, -1}, {-1, 1}};
        for(int[] move : kingMoves){
            int targetRow = myPosition.getRow() + move[0];
            int targetCol = myPosition.getColumn() + move[1];
            if(targetRow >= 1 && targetRow <= 8 && targetCol >= 1 && targetCol <= 8) {
                ChessPosition checkPos = new ChessPosition(targetRow, targetCol);
                ChessPiece p = board.getPiece(checkPos);
                if(p == null || p.getTeamColor() != pieceColor){
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }
        return legal;
    }

    public Collection<ChessMove> evilAhhPawns(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legal = new ArrayList<>();
        int d = 1;
        if(pieceColor == ChessGame.TeamColor.BLACK){
            d = -1;
        }

        for(int c = myPosition.getColumn() - 1; c <= myPosition.getColumn() + 1; c += 2){
            ChessPosition checkPos = new ChessPosition(myPosition.getRow() + d, c);
            ChessPiece target = board.getPiece(checkPos);
            if(c >= 1 && c <= 8 && target != null && target.pieceColor != pieceColor){
                if(checkPos.getRow() == 8 || checkPos.getRow() == 1){
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.BISHOP));
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.QUEEN));
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.ROOK));
                    legal.add(new ChessMove(myPosition, checkPos, PieceType.KNIGHT));
                } else {
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }

        if(myPosition.getRow() == (int) (4.5 - (2.5 * d))){
            ChessPosition midPos = new ChessPosition(myPosition.getRow() + d, myPosition.getColumn());
            ChessPosition endPos = new ChessPosition(myPosition.getRow() + d + d, myPosition.getColumn());
            if (board.getPiece(midPos) == null && board.getPiece(endPos) == null){
                legal.add(new ChessMove(myPosition, endPos, null));
            }
        }

        ChessPosition checkPos = new ChessPosition(myPosition.getRow() + d, myPosition.getColumn());
        if(board.getPiece(checkPos) == null) {
            if (checkPos.getRow() == 8 || checkPos.getRow() == 1) {
                legal.add(new ChessMove(myPosition, checkPos, PieceType.BISHOP));
                legal.add(new ChessMove(myPosition, checkPos, PieceType.QUEEN));
                legal.add(new ChessMove(myPosition, checkPos, PieceType.ROOK));
                legal.add(new ChessMove(myPosition, checkPos, PieceType.KNIGHT));
            } else {
                legal.add(new ChessMove(myPosition, checkPos, null));
            }
        }

        return legal;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return Objects.equals(pieceColor, that.pieceColor) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode(){
        return Objects.hash(pieceColor, type);
    }
}