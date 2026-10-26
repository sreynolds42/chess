package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (getPieceType() == PieceType.ROOK){
            return cross(board, myPosition);
        }
        if (type == PieceType.KNIGHT){
            return horse(board, myPosition);
        }
        if (type == PieceType.BISHOP){
            return diag(board, myPosition);
        }
        if (type == PieceType.KING){
            return king(board, myPosition);
        }
        if (type == PieceType.QUEEN){
            Collection<ChessMove> moves = new ArrayList<>();
            moves.addAll(cross(board, myPosition));
            moves.addAll(diag(board, myPosition));
            return moves;
        }
        if (type == PieceType.PAWN){
            return evilAhhPawns(board, myPosition);
        }
        return cross(board, myPosition);
    }

    private void addRayMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> legal, int rowDir, int colDir) {
        int r = myPosition.getRow() + rowDir;
        int c = myPosition.getColumn() + colDir;
        while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
            ChessPosition checkPos = new ChessPosition(r, c);
            if (board.getPiece(checkPos) == null) {
                legal.add(new ChessMove(myPosition, checkPos, null));
            } else if (board.getPiece(checkPos).getTeamColor() != getTeamColor()){
                legal.add(new ChessMove(myPosition, checkPos, null));
                break;
            } else {
                break;
            }
            r += rowDir;
            c += colDir;
        }
    }

    public Collection<ChessMove> cross(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        addRayMoves(board, myPosition, legal, 1, 0);
        addRayMoves(board, myPosition, legal, -1, 0);
        addRayMoves(board, myPosition, legal, 0, 1);
        addRayMoves(board, myPosition, legal, 0, -1);
        return legal;
    }

    public Collection<ChessMove> diag(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        addRayMoves(board, myPosition, legal, 1, 1);
        addRayMoves(board, myPosition, legal, 1, -1);
        addRayMoves(board, myPosition, legal, -1, 1);
        addRayMoves(board, myPosition, legal, -1, -1);
        return legal;
    }

    private void addOffsetMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> legal, int[][] moves) {
        for (int[] move : moves){
            if (myPosition.getRow() + move[0] >= 1 && myPosition.getRow() + move[0] <= 8
                    && myPosition.getColumn() + move[1] >= 1 && myPosition.getColumn() + move[1] <= 8) {
                ChessPosition checkPos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                if (board.getPiece(checkPos) == null || board.getPiece(checkPos).getTeamColor() != pieceColor){
                    legal.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }
    }

    public Collection<ChessMove> horse(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        int[][] horseMoves = {{1,2},{2,1},{-1,2},{-2,1},{-1,-2},{-2,-1},{1,-2},{2,-1}};
        addOffsetMoves(board, myPosition, legal, horseMoves);
        return legal;
    }

    public Collection<ChessMove> king(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legal = new ArrayList<>();
        int[][] kingMoves = {{0,1},{1,0},{1,1},{0,-1},{-1,-1},{-1,0},{1,-1},{-1,1}};
        addOffsetMoves(board, myPosition, legal, kingMoves);
        return legal;
    }

    private void addPawnMove(ChessPosition myPosition, ChessPosition checkPos, Collection<ChessMove> legal) {
        if (checkPos.getRow() == 8 || checkPos.getRow() == 1) {
            legal.add(new ChessMove(myPosition, checkPos, PieceType.BISHOP));
            legal.add(new ChessMove(myPosition, checkPos, PieceType.QUEEN));
            legal.add(new ChessMove(myPosition, checkPos, PieceType.ROOK));
            legal.add(new ChessMove(myPosition, checkPos, PieceType.KNIGHT));
        } else {
            legal.add(new ChessMove(myPosition, checkPos, null));
        }
    }

    public Collection<ChessMove> evilAhhPawns(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legal = new ArrayList<>();
        int d = 1;
        if (pieceColor == ChessGame.TeamColor.BLACK){
            d = -1;
        }

        for (int c = myPosition.getColumn() - 1; c <= myPosition.getColumn() + 1; c += 2){
            ChessPosition checkPos = new ChessPosition(myPosition.getRow() + d, c);
            if (c >= 1 && c <= 8 && board.getPiece(checkPos) != null && board.getPiece(checkPos).pieceColor != pieceColor){
                addPawnMove(myPosition, checkPos, legal);
            }
        }

        if (myPosition.getRow() == (int) (4.5 - (2.5 * d))){
            if (board.getPiece(new ChessPosition(myPosition.getRow() + d, myPosition.getColumn())) == null
                    && board.getPiece(new ChessPosition(myPosition.getRow() + d + d, myPosition.getColumn())) == null){
                legal.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + d + d, myPosition.getColumn()), null));
            }
        }

        if (board.getPiece(new ChessPosition(myPosition.getRow() + d, myPosition.getColumn())) == null) {
            ChessPosition checkPos = new ChessPosition(myPosition.getRow() + d, myPosition.getColumn());
            addPawnMove(myPosition, checkPos, legal);
        }

        return legal;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
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