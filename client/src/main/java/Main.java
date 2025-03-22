import chess.*;
import client.PreLoginRepl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        String serverAddress = "http://localhost:4567";
        new PreLoginRepl(serverAddress).run();
    }
}