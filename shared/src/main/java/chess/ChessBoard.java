package chess;

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

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //White Team initial positions
        addPiece(new ChessPosition(0, 0), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(1, 0), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(2, 0), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(3, 0), new ChessPiece(WHITE, QUEEN));
        addPiece(new ChessPosition(4, 0), new ChessPiece(WHITE, KING));
        addPiece(new ChessPosition(5, 0), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(6, 0), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(7, 0), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(0, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(1, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(2, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(3, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(4, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(5, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(6, 1), new ChessPiece(WHITE, PAWN));
        addPiece(new ChessPosition(7, 1), new ChessPiece(WHITE, PAWN));
        //Black Team initial positions
        addPiece(new ChessPosition(0, 7), new ChessPiece(BLACK, ROOK));
        addPiece(new ChessPosition(1, 7), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(2, 7), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(3, 7), new ChessPiece(BLACK, QUEEN));
        addPiece(new ChessPosition(4, 7), new ChessPiece(BLACK, KING));
        addPiece(new ChessPosition(5, 7), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(6, 7), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(7, 7), new ChessPiece(BLACK, ROOK));
        addPiece(new ChessPosition(0, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(1, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(2, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(3, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(4, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(5, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(6, 6), new ChessPiece(BLACK, PAWN));
        addPiece(new ChessPosition(7, 6), new ChessPiece(BLACK, PAWN));


    }
}
