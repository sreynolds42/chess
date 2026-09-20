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

    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
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
    public ChessGame.TeamColor getTeamColor() { return color;}

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
    private Collection<ChessMove> checkCross(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        //going right
        int col = myPosition.getColumn() + 1;
        while (col <= 8){
            ChessPosition checkPosition = new ChessPosition(myPosition.getRow(), col);
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            col++;
        }
        //going left
        col = myPosition.getColumn() - 1;
        while (col >= 1){
            ChessPosition checkPosition = new ChessPosition(myPosition.getRow(), col);
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            col--;
        }
        //going down
        int row = myPosition.getRow() + 1;
        while (row <= 8){
            ChessPosition checkPosition = new ChessPosition(row, myPosition.getColumn());
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            row++;
        }
        //going up
        row = myPosition.getRow() - 1;
        while (row >= 1){
            ChessPosition checkPosition = new ChessPosition(row, myPosition.getColumn());
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            row--;
        }

        return legalMoves;

    }

    private Collection<ChessMove> checkDiagonal(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        //going upright
        int col = myPosition.getColumn() + 1;
        int row = myPosition.getRow() + 1;
        while (col <= 8 && row <=8){
            ChessPosition checkPosition = new ChessPosition(row, col);
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            col++;
            row++;
        }
        //going downright
        col = myPosition.getColumn() + 1;
        row = myPosition.getRow() - 1;
        while (col <= 8 && row >= 1){
            ChessPosition checkPosition = new ChessPosition(row, col);
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            col++;
            row--;
        }
        //going upleft
        col = myPosition.getColumn() - 1;
        row = myPosition.getRow() + 1;
        while (col >= 1 && row <= 8){
            ChessPosition checkPosition = new ChessPosition(row, col);
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            col--;
            row++;
        }
        //going downleft
        col = myPosition.getColumn() - 1;
        row = myPosition.getRow() - 1;
        while (col >= 1 && row >= 1){
            ChessPosition checkPosition = new ChessPosition(row, col);
            if(board.getPiece(checkPosition) == null){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
            }
            else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()){
                legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                break;
            }
            else{
                break;
            }
            col--;
            row--;
        }

        return legalMoves;
    }

    private Collection<ChessMove> checkHorse(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int horseEpicMoves[][] = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1},{1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        for(int[] horseJump : horseEpicMoves) {
            int row = myPosition.getRow() + horseJump[0];
            int col = myPosition.getColumn() + horseJump[1];

            if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition checkPosition = new ChessPosition(row, col);
                if (board.getPiece(checkPosition) == null) {
                    legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                } else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()) {
                    legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                }
            }
        }
        return legalMoves;
    }

    private Collection<ChessMove> checkKing(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int kingHops[][] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for(int[] kingHop : kingHops) {
            int row = myPosition.getRow() + kingHop[0];
            int col = myPosition.getColumn() + kingHop[1];

            if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition checkPosition = new ChessPosition(row, col);
                if (board.getPiece(checkPosition) == null) {
                    legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                } else if (board.getPiece(checkPosition).getTeamColor() != this.getTeamColor()) {
                    legalMoves.add(new ChessMove(myPosition, checkPosition, null));
                }
            }
        }
        return legalMoves;
    }

    private Collection<ChessMove> checkPawn(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int d = 1;
        if(getTeamColor() == ChessGame.TeamColor.BLACK){
            d = -1;
        }

        int nextRow = myPosition.getRow() + d;
        ChessPosition step = new ChessPosition(nextRow, myPosition.getColumn());

        double openDouble = (4.5 - (2.5 * d));
        int open = (int) openDouble;

        if(board.getPiece(step) == null){
            addPawnMoves(legalMoves, myPosition, step, nextRow);

            if(myPosition.getRow() == open){
                ChessPosition hop = new ChessPosition(myPosition.getRow() + (2 * d), myPosition.getColumn());
                if (board.getPiece(hop) == null) {
                    legalMoves.add(new ChessMove(myPosition, hop, null));
                }
            }
        }

    // diag left
        int eatLeftCol = myPosition.getColumn() - 1;
        if (eatLeftCol >= 1) {
            ChessPosition eatLeft = new ChessPosition(nextRow, eatLeftCol);
            ChessPiece target = board.getPiece(eatLeft);
            if (target != null && target.getTeamColor() != getTeamColor()) {
                addPawnMoves(legalMoves, myPosition, eatLeft, nextRow);
            }
        }

    // diag right
        int eatRightCol = myPosition.getColumn() + 1;
        if (eatRightCol <= 8) {
            ChessPosition eatRight = new ChessPosition(nextRow, eatRightCol);
            ChessPiece target = board.getPiece(eatRight);
            if (target != null && target.getTeamColor() != getTeamColor()) {
                addPawnMoves(legalMoves, myPosition, eatRight, nextRow);
            }
        }

        return legalMoves;
    }

    private void addPawnMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int checkPosition) {
        if (checkPosition == 8 || checkPosition == 1) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(type == PieceType.KING){
            //oob check
        }
        if(type == PieceType.QUEEN){
            //oob check
        }
        if(type == PieceType.BISHOP){
            //oob check
        }
        if(type == PieceType.ROOK){
            //oob check
        }
        if(type == PieceType.KNIGHT){
            //oob check
        }
        if(type == PieceType.PAWN){
            //oob check
        }
        throw new RuntimeException("Catastrophic Failure");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return this.color == that.color && this.type == that.type;
    }

    @Override
    public int hashCode(){
        return Objects.hash(color, type);
    }
}
