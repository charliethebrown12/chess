package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    public ChessBoard copy() {
        ChessBoard copy = new ChessBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = this.getPiece(position);
                if (piece != null) {
                    copy.addPiece(position, piece.copy());
                }
            }
        }
        return copy;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //White Team initial positions
        addPiece(new ChessPosition(1, 1), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(WHITE, QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(WHITE, KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(2, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 2), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 3), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 4), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 5), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 6), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 7), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 8), new ChessPiece(WHITE, PAWN));
        //Black Team initial positions
        addPiece(new ChessPosition(8, 1), new ChessPiece(BLACK, ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(BLACK, QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(BLACK, KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(BLACK, ROOK));
        addPiece(new ChessPosition(7, 1), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 2), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 3), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 4), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 5), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 7), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 8), new ChessPiece(BLACK, PAWN));


    }

    public void makeMove(ChessMove move) {
        ChessPiece piece = getPiece(move.getStartPosition());
        if (piece == null) {
            return;
        }

        addPiece(move.getEndPosition(), piece);
        addPiece(move.getStartPosition(), null);

        if (move.getPromotionPiece() != null) {
            addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
    }
}
