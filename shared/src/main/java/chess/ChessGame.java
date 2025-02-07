package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
    }

    public ChessGame(ChessGame other) {
        this.teamTurn = other.teamTurn;
        this.board = other.board.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getBoard());
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        else {
            TeamColor teamColor = piece.getTeamColor();
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
            Collection<ChessMove> validMoves = new ArrayList<>();
            for (ChessMove move : moves) {
                ChessGame boardCopy = new ChessGame(this);
                boardCopy.getBoard().makeMove(move);;
                if (!boardCopy.isInCheck(teamColor)) {
                    validMoves.add(move);
                }
            }
            return validMoves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at this position: " + move.getStartPosition());
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Piece is not in team color: " + piece.getTeamColor());
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves != null && validMoves.contains(move)) {
            board.makeMove(move);
            if (teamTurn == TeamColor.BLACK) {
                teamTurn = TeamColor.WHITE;
            } else {
                teamTurn = TeamColor.BLACK;
            }
        } else {
            throw new InvalidMoveException("Invalid move: " + move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
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
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }
        boolean check = isInCheck(teamColor);
        if (check) {
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = validMoves(position);
                        if (!moves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else {return false;}
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }
        boolean check = isInCheck(teamColor);
        if (!check) {
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = validMoves(position);
                        if (!moves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else {return false;}
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row ++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

}
