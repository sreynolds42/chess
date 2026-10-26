package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard game = new ChessBoard();
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookLeftMoved = false;
    private boolean whiteRookRightMoved = false;
    private boolean blackRookLeftMoved = false;
    private boolean blackRookRightMoved = false;
    private final ChessMove lastMove = null;

    public ChessGame() {
        game.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = game.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> candidateMoves = piece.pieceMoves(game, startPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.KING
                && startPosition.getColumn() == 5) {
            castle(startPosition, candidateMoves);
        }

        Collection<ChessMove> validMoves = new HashSet<>();

        for (ChessMove move : candidateMoves) {
            ChessBoard sim = new ChessBoard(game);

            sim.addPiece(move.getStartPosition(), null);
            if (move.getPromotionPiece() != null) {
                sim.addPiece(move.getEndPosition(),
                        new ChessPiece(teamColor, move.getPromotionPiece()));
            } else {
                sim.addPiece(move.getEndPosition(), piece);
            }

            if (piece.getPieceType() == ChessPiece.PieceType.KING
                    && move.getStartPosition().getColumn() == 5
                    && move.getEndPosition().getColumn() == 7) {
                sim.addPiece(new ChessPosition(move.getStartPosition().getRow(), 6),
                        sim.getPiece(new ChessPosition(move.getStartPosition().getRow(), 8)));
                sim.addPiece(new ChessPosition(move.getStartPosition().getRow(), 8), null);
            }
            if (piece.getPieceType() == ChessPiece.PieceType.KING
                    && move.getStartPosition().getColumn() == 5
                    && move.getEndPosition().getColumn() == 3) {
                sim.addPiece(new ChessPosition(move.getStartPosition().getRow(), 4),
                        sim.getPiece(new ChessPosition(move.getStartPosition().getRow(), 1)));
                sim.addPiece(new ChessPosition(move.getStartPosition().getRow(), 1), null);
            }

            ChessBoard temp = game;
            game = sim;

            if (!isInCheck(teamColor)) {
                validMoves.add(move);
            }
            game = temp;
        }

        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!simMove(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece piece = game.getPiece(move.getStartPosition());

        if (piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == TeamColor.WHITE){
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }

        if (move.getStartPosition().equals(new ChessPosition(1, 1))
                || move.getEndPosition().equals(new ChessPosition(1, 1))) {
            whiteRookLeftMoved = true;
        }
        if (move.getStartPosition().equals(new ChessPosition(1, 8))
                || move.getEndPosition().equals(new ChessPosition(1, 8))) {
            whiteRookRightMoved = true;
        }
        if (move.getStartPosition().equals(new ChessPosition(8, 1))
                || move.getEndPosition().equals(new ChessPosition(8, 1))) {
            blackRookLeftMoved = true;
        }
        if (move.getStartPosition().equals(new ChessPosition(8, 8))
                || move.getEndPosition().equals(new ChessPosition(8, 8))) {
            blackRookRightMoved = true;
        }

        if (move.getPromotionPiece() != null) {
            game.addPiece(move.getEndPosition(),
                    new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            game.addPiece(move.getEndPosition(), piece);
        }
        game.addPiece(move.getStartPosition(), null);

        if (piece.getPieceType() == ChessPiece.PieceType.KING &&
                Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2) {

            int row = move.getStartPosition().getRow();

            if (move.getEndPosition().getColumn() == 7) {
                ChessPiece rook = game.getPiece(new ChessPosition(row, 8));
                game.addPiece(new ChessPosition(row, 6), rook);
                game.addPiece(new ChessPosition(row, 8), null);
            } else if (move.getEndPosition().getColumn() == 3) {
                ChessPiece rook = game.getPiece(new ChessPosition(row, 1));
                game.addPiece(new ChessPosition(row, 4), rook);
                game.addPiece(new ChessPosition(row, 1), null);
            }
        }

        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    private boolean simMove(ChessMove move){
        ChessPiece piece = game.getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            return false;
        }

        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        return valid != null && valid.contains(move);
    }

    private void castle(ChessPosition myPosition, Collection<ChessMove> legal){
        if(isInCheck(game.getPiece(myPosition).getTeamColor())){
            return;
        }

        TeamColor color = game.getPiece(myPosition).getTeamColor();
        int row = (color == TeamColor.WHITE) ? 1 : 8;

        boolean kingMoved = (color == TeamColor.WHITE) ? whiteKingMoved : blackKingMoved;
        boolean rookRightMoved = (color == TeamColor.WHITE) ? whiteRookRightMoved : blackRookRightMoved;
        boolean rookLeftMoved = (color == TeamColor.WHITE) ? whiteRookLeftMoved : blackRookLeftMoved;

        // right castle
        if(!kingMoved && !rookRightMoved){
            if(game.getPiece(new ChessPosition(row, 6)) == null && game.getPiece(new ChessPosition(row, 7)) == null){
                if (castleClear(new ChessPosition(row, 6), color) && castleClear(new ChessPosition(row, 7), color)) {
                    legal.add(new ChessMove(myPosition, new ChessPosition(row, 7), null));
                }
            }
        }
        // left castle
        if(!kingMoved && !rookLeftMoved){
            if(game.getPiece(new ChessPosition(row, 4)) == null && game.getPiece(new ChessPosition(row, 3)) == null && game.getPiece(new ChessPosition(row, 2)) == null){
                if (castleClear(new ChessPosition(row, 4), color) && castleClear(new ChessPosition(row, 3), color)) {
                    legal.add(new ChessMove(myPosition, new ChessPosition(row, 3), null));
                }
            }
        }
    }

    private boolean castleClear(ChessPosition checkPos, TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currentPos = new ChessPosition(r, c);
                ChessPiece piece = game.getPiece(currentPos);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    for (ChessMove move : piece.pieceMoves(game, currentPos)) {
                        if (move.getEndPosition().equals(checkPos)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private ChessPosition kingFind(TeamColor teamColor){
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece p = game.getPiece(pos);
                if(p != null && p.getTeamColor() == teamColor && p.getPieceType() == ChessPiece.PieceType.KING){
                    return pos;
                }
            }
        }
        return null;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSpot = kingFind(teamColor);

        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece p = game.getPiece(pos);
                if(p != null && p.getTeamColor() != teamColor){
                    for(ChessMove move : p.pieceMoves(game, pos)){
                        if(move.getEndPosition().equals(kingSpot)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return noValidMoves(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        return noValidMoves(teamColor);
    }

    private boolean noValidMoves(TeamColor teamColor){
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = game.getPiece(pos);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void setBoard(ChessBoard board) {
        game = board;
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRookLeftMoved = false;
        whiteRookRightMoved = false;
        blackRookLeftMoved = false;
        blackRookRightMoved = false;
    }

    public ChessBoard getBoard() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn
                && whiteKingMoved == chessGame.whiteKingMoved
                && blackKingMoved == chessGame.blackKingMoved
                && whiteRookLeftMoved == chessGame.whiteRookLeftMoved
                && whiteRookRightMoved == chessGame.whiteRookRightMoved
                && blackRookLeftMoved == chessGame.blackRookLeftMoved
                && blackRookRightMoved == chessGame.blackRookRightMoved
                && Objects.equals(game, chessGame.game)
                && Objects.equals(lastMove, chessGame.lastMove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                turn,
                game,
                whiteKingMoved,
                blackKingMoved,
                whiteRookLeftMoved,
                whiteRookRightMoved,
                blackRookLeftMoved,
                blackRookRightMoved,
                lastMove
        );
    }
}