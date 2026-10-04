package chess;

import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard game = new ChessBoard();

    public ChessGame() {

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

        return game.getPiece(startPosition).pieceMoves(game, startPosition);
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
}
