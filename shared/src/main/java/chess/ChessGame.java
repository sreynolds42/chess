package chess;

import java.util.ArrayList;
import java.util.Collection;
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
    private ChessMove lastMove = null;

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
        // work on check
        ChessPiece piece = game.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : piece.pieceMoves(game, startPosition)) {
            ChessBoard sim = new ChessBoard(game);

            if (move.getPromotionPiece() != null) {
                sim.addPiece(move.getEndPosition(), new ChessPiece(teamColor, move.getPromotionPiece()));
            } else {
                sim.addPiece(move.getEndPosition(), piece);
            }
            sim.addPiece(move.getStartPosition(), null);

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

        // check if the king is moving
        if (piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == TeamColor.WHITE){
                whiteKingMoved = true;
            }
            else{
                blackKingMoved = true;
            }
        }

        // left white rook
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
            if(move.getStartPosition().equals(new ChessPosition(1, 1))) {
                whiteRookLeftMoved = true;
            }
            else if(move.getStartPosition().equals(new ChessPosition(1, 8))) {
                whiteRookRightMoved = true;
            }
            else if(move.getStartPosition().equals(new ChessPosition(8, 1))) {
                blackRookLeftMoved = true;
            }
            else if(move.getStartPosition().equals(new ChessPosition(8, 8))) {
                blackRookRightMoved = true;
            }
        }

        if (move.getPromotionPiece() != null) {
            game.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            game.addPiece(move.getEndPosition(), piece);
        }
        game.addPiece(move.getStartPosition(), null);

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
        if (valid == null || !valid.contains(move)) {
            return false;
        }

        ChessBoard sim = new ChessBoard(game);
        sim.addPiece(move.getEndPosition(), sim.getPiece(move.getStartPosition()));
        sim.addPiece(move.getStartPosition(), null);

        return !isInCheck(piece.getTeamColor());

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */


    private ChessPosition kingFind(TeamColor teamColor){
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                if(game.getPiece(new ChessPosition(r, c)) != null && game.getPiece(new ChessPosition(r, c)).getTeamColor() == teamColor && game.getPiece(new ChessPosition(r, c)).getPieceType() == ChessPiece.PieceType.KING){
                    return new ChessPosition(r, c);
                }
            }
        }
        return null;
    }

    public boolean isInCheck(TeamColor teamColor) {
        // finding the king
        ChessPosition kingSpot = kingFind(teamColor);

        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                if(game.getPiece(new ChessPosition(r, c)) != null && game.getPiece(new ChessPosition(r, c)).getTeamColor() != teamColor){
                    for(ChessMove move : game.getPiece(new ChessPosition(r, c)).pieceMoves(game, new ChessPosition(r, c))){
                        if(move.getEndPosition().equals(kingSpot)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return noValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
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

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game = board;
        /*
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++) {
                if (board.getPiece(new ChessPosition(r,c)) != null){
                    game.addPiece(new ChessPosition(r,c), board.getPiece(new ChessPosition(r,c)));
                }
                else{
                    game.addPiece(new ChessPosition(r,c), null);
                }
            }
        }
         */
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
