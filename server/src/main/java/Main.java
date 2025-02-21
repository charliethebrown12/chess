import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        Server server = new Server();
        int port = server.run(4567);
        System.out.println("Server running on port " + port);
    }
}