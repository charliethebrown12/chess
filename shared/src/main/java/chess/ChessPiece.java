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
    private final PieceType pieceType;

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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }

    //copy method to help in the creation of the board copy method that creates a copy of a ChessPiece object

    public ChessPiece copy() {
        return new ChessPiece(teamColor, pieceType);
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
                if (row + 1 <= 8) {
                    ChessPosition position1 = new ChessPosition(row + 1, column);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    if (pieceThere1 == null) {
                        if (row + 1 == 8) {
                            for (PieceType promotion : PieceType.values()) {
                                if (promotion != PieceType.PAWN && promotion != PieceType.KING) {
                                    moves.add(new ChessMove(myPosition, position1, promotion));
                                }
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, position1, null));
                        }
                    }
                }
                if (row == 2) {
                    ChessPosition position1 = new ChessPosition(row + 2, column);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    ChessPosition position2 = new ChessPosition(row + 1, column);
                    ChessPiece pieceThere2 = board.getPiece(position2);
                    if (pieceThere1 == null && pieceThere2 == null) {
                        moves.add(new ChessMove(myPosition, position1, null));
                    }
                }
                if (row + 1 <= 8 && column + 1 <= 8) {
                    ChessPosition position1 = new ChessPosition(row + 1, column + 1);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    if (pieceThere1 != null && pieceThere1.teamColor != this.teamColor) {
                        if (row + 1 == 8) {
                            for (PieceType promotion : PieceType.values()) {
                                if (promotion != PieceType.PAWN && promotion != PieceType.KING) {
                                    moves.add(new ChessMove(myPosition, position1, promotion));
                                }
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, position1, null));
                        }
                    }
                }
                if (row + 1 <= 8 && column - 1 >= 1) {
                    ChessPosition position1 = new ChessPosition(row + 1, column - 1);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    if (pieceThere1 != null && pieceThere1.teamColor != this.teamColor) {
                        if (row + 1 == 8) {
                            for (PieceType promotion : PieceType.values()) {
                                if (promotion != PieceType.PAWN && promotion != PieceType.KING) {
                                    moves.add(new ChessMove(myPosition, position1, promotion));
                                }
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, position1, null));
                        }
                    }
                }
            }
            if (this.teamColor == ChessGame.TeamColor.BLACK) {
                if (row - 1 >= 1) {
                    ChessPosition position1 = new ChessPosition(row - 1, column);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    if (pieceThere1 == null) {
                        if (row - 1 == 1) {
                            for (PieceType promotion : PieceType.values()) {
                                if (promotion != PieceType.PAWN && promotion != PieceType.KING) {
                                    moves.add(new ChessMove(myPosition, position1, promotion));
                                }
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, position1, null));
                        }
                    }
                }
                if (row == 7) {
                    ChessPosition position1 = new ChessPosition(row - 2, column);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    ChessPosition position2 = new ChessPosition(row - 1, column);
                    ChessPiece pieceThere2 = board.getPiece(position2);
                    if (pieceThere1 == null && pieceThere2 == null) {
                        moves.add(new ChessMove(myPosition, position1, null));
                    }
                }
                if (row - 1 >= 1 && column - 1 >= 1) {
                    ChessPosition position1 = new ChessPosition(row - 1, column - 1);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    if (pieceThere1 != null && pieceThere1.teamColor != this.teamColor) {
                        if (row - 1 == 1) {
                            for (PieceType promotion : PieceType.values()) {
                                if (promotion != PieceType.PAWN && promotion != PieceType.KING) {
                                    moves.add(new ChessMove(myPosition, position1, promotion));
                                }
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, position1, null));
                        }
                    }
                }
                if (row - 1 >= 1 && column + 1 <= 8) {
                    ChessPosition position1 = new ChessPosition(row - 1, column + 1);
                    ChessPiece pieceThere1 = board.getPiece(position1);
                    if (pieceThere1 != null && pieceThere1.teamColor != this.teamColor) {
                        if (row - 1 == 1) {
                            for (PieceType promotion : PieceType.values()) {
                                if (promotion != PieceType.PAWN && promotion != PieceType.KING) {
                                    moves.add(new ChessMove(myPosition, position1, promotion));
                                }
                            }
                        }
                        else {
                            moves.add(new ChessMove(myPosition, position1, null));
                        }
                    }
                }
            }
        }
        if (this.pieceType == PieceType.ROOK) {
            for (int i = 1; column + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row, column + i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; column - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row, column - i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row - i, column);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row + i, column);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
        if (this.pieceType == PieceType.BISHOP) {
            for (int i = 1; row + i <= 8 && column + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row + i, column + i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 1 && column + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row - i, column + i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 1 && column - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row - i, column - i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row + i <= 8 && column - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row + i, column - i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
        if (this.pieceType == PieceType.QUEEN) {
            for (int i = 1; column + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row, column + i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; column - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row, column - i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row - i, column);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row + i, column);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row + i <= 8 && column + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row + i, column + i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 1 && column + i <= 8; i++) {
                ChessPosition newPosition = new ChessPosition(row - i, column + i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 1 && column - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row - i, column - i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
            for (int i = 1; row + i <= 8 && column - i >= 1; i++) {
                ChessPosition newPosition = new ChessPosition(row + i, column - i);
                ChessPiece pieceThere = board.getPiece(newPosition);
                if (pieceThere == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else {
                    if (pieceThere.teamColor != this.teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
        if (this.pieceType == PieceType.KING) {
            if (row + 1 <= 8 && column + 1 <= 8) {
                ChessPosition position1 = new ChessPosition(row + 1, column + 1);
                ChessPiece pieceThere1 = board.getPiece(position1);
                if (pieceThere1 == null || pieceThere1.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position1, null));
                }
            }
            if (row + 1 <= 8) {
                ChessPosition position2 = new ChessPosition(row + 1, column);
                ChessPiece pieceThere2 = board.getPiece(position2);
                if (pieceThere2 == null || pieceThere2.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position2, null));
                }
            }
            if (row + 1 <= 8 && column - 1 >= 1) {
                ChessPosition position3 = new ChessPosition(row + 1, column - 1);
                ChessPiece pieceThere3 = board.getPiece(position3);
                if (pieceThere3 == null || pieceThere3.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position3, null));
                }
            }
            if (column + 1 <= 8) {
                ChessPosition position4 = new ChessPosition(row, column + 1);
                ChessPiece pieceThere4 = board.getPiece(position4);
                if (pieceThere4 == null || pieceThere4.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position4, null));
                }
            }
            if (column - 1 >= 1) {
                ChessPosition position5 = new ChessPosition(row, column - 1);
                ChessPiece pieceThere5 = board.getPiece(position5);
                if (pieceThere5 == null || pieceThere5.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position5, null));
                }
            }
            if (row - 1 >= 1 && column + 1 <= 8) {
                ChessPosition position6 = new ChessPosition(row - 1, column + 1);
                ChessPiece pieceThere6 = board.getPiece(position6);
                if (pieceThere6 == null || pieceThere6.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position6, null));
                }
            }
            if (row - 1 >= 1) {
                ChessPosition position7 = new ChessPosition(row - 1, column);
                ChessPiece pieceThere7 = board.getPiece(position7);
                if (pieceThere7 == null || pieceThere7.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position7, null));
                }
            }
            if (row - 1 >= 1 && column - 1 >= 1) {
                ChessPosition position8 = new ChessPosition(row - 1, column - 1);
                ChessPiece pieceThere8 = board.getPiece(position8);
                if (pieceThere8 == null || pieceThere8.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position8, null));
                }
            }
        }
        if (this.pieceType == PieceType.KNIGHT) {
            if (row + 2 <= 8 && column + 1 <= 8) {
                ChessPosition position1 = new ChessPosition(row + 2, column + 1);
                ChessPiece pieceThere1 = board.getPiece(position1);
                if (pieceThere1 == null || pieceThere1.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position1, null));
                }
            }
            if (row + 2 <= 8 && column - 1 >= 1) {
                ChessPosition position2 = new ChessPosition(row + 2, column - 1);
                ChessPiece pieceThere2 = board.getPiece(position2);
                if (pieceThere2 == null || pieceThere2.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position2, null));
                }
            }
            if (row + 1 <= 8 && column + 2 <= 8) {
                ChessPosition position3 = new ChessPosition(row + 1, column + 2);
                ChessPiece pieceThere3 = board.getPiece(position3);
                if (pieceThere3 == null || pieceThere3.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position3, null));
                }
            }
            if (row - 1 >= 1 && column + 2 <= 8) {
                ChessPosition position4 = new ChessPosition(row - 1, column + 2);
                ChessPiece pieceThere4 = board.getPiece(position4);
                if (pieceThere4 == null || pieceThere4.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position4, null));
                }
            }
            if (row - 2 >= 1 && column - 1 >= 1) {
                ChessPosition position5 = new ChessPosition(row - 2, column - 1);
                ChessPiece pieceThere5 = board.getPiece(position5);
                if (pieceThere5 == null || pieceThere5.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position5, null));
                }
            }
            if (row - 2 >= 1 && column + 1 <= 8) {
                ChessPosition position6 = new ChessPosition(row - 2, column + 1);
                ChessPiece pieceThere6 = board.getPiece(position6);
                if (pieceThere6 == null || pieceThere6.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position6, null));
                }
            }
            if (row + 1 <= 8 && column - 2 >= 1) {
                ChessPosition position7 = new ChessPosition(row + 1, column - 2);
                ChessPiece pieceThere7 = board.getPiece(position7);
                if (pieceThere7 == null || pieceThere7.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position7, null));
                }
            }
            if (row - 1 >= 1 && column - 2 >= 1) {
                ChessPosition position8 = new ChessPosition(row - 1, column - 2);
                ChessPiece pieceThere8 = board.getPiece(position8);
                if (pieceThere8 == null || pieceThere8.teamColor != this.teamColor) {
                    moves.add(new ChessMove(myPosition, position8, null));
                }
            }
        }
        return moves;
    }
}
