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
    private final ChessGame.TeamColor teamColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return getTeamColor() == that.getTeamColor() && getPieceType() == that.getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamColor(), getPieceType());
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        if (this.pieceType == PieceType.PAWN) {
            if (this.teamColor == ChessGame.TeamColor.WHITE) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, column), this.pieceType));
            }
            if (row == 2 && this.teamColor == ChessGame.TeamColor.WHITE) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), this.pieceType));
            }
            if (this.teamColor == ChessGame.TeamColor.BLACK) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, column), this.pieceType));
            }
            if (row == 7 && this.teamColor == ChessGame.TeamColor.BLACK) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row - 2, column), this.pieceType));
            }
        }
        if (this.pieceType == PieceType.ROOK) {
            if (this.teamColor == ChessGame.TeamColor.WHITE) {
                for (int i = 1; column + i <= 8; i++) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column + i), this.pieceType));
                }
                for (int i = 1; row + i <= 8; i++) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + i, column), this.pieceType));
                }
            if (this.teamColor == ChessGame.TeamColor.BLACK) {
                for (int i = 1; column - i >= 1; i++) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, column - i), this.pieceType));
                }
                for (int i = 1; row - i >= 1; i++) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row - i, column), this.pieceType));
                }
            }
            }
        }
        return moves;
    }
}
