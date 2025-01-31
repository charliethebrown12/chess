package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private final ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        setBoard(board);
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
            return piece.pieceMoves(board, startPosition);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves != null && validMoves.contains(move)) {
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
        /*
        if (teamTurn == TeamColor.WHITE) {
            ChessPosition kingLocation = findKing(teamColor);
            Collection<ChessPiece> otherTeam = getPiecesByColor(TeamColor.WHITE);
            for (ChessPiece piece : otherTeam) {
                Collection<ChessMove> validMoves = validMoves(new ChessPosition(kingLocation.getRow(), kingLocation.getColumn()));

                for (ChessMove move : validMoves) {
                    if (move.getEndPosition().equals(kingLocation)) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (teamTurn == TeamColor.BLACK) {
            ChessPosition kingLocation = findKing(teamColor);
            Collection<ChessPiece> otherTeam = getPiecesByColor(TeamColor.BLACK);
            for (ChessPiece piece : otherTeam) {
                Collection<ChessMove> validMoves = validMoves(new ChessPosition(piece.));

                for (ChessMove move : validMoves) {
                    if (move.getEndPosition().equals(kingLocation)) {
                        return true;
                    }
                }
            }
            return false;
        }

         */
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
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
/*
    private ChessPosition findKing(TeamColor teamColor) {
        return null;
    }

    private Collection<ChessPiece> getPiecesByColor(TeamColor color) {
        return null;
    }

    private Collection<ChessMove> getEnemyMoves(TeamColor teamColor) {
        Collection<ChessPiece> opponentPieces = getPiecesByColor(teamColor);
        Collection<ChessMove> moves = new ArrayList<>();
        for (ChessPiece piece : opponentPieces) {
            Collection<ChessMove> validMoves = validMoves();
        }
    }

 */
}
