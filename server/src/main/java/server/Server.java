package server;

import dataaccess.DatabaseManager;
import spark.*;
import static spark.Spark.webSocket;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        webSocket("/ws", ServerWebSocketHandler.class);
        System.out.println("WebSocket handler registered at /ws");

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        DatabaseManager.createDatabase();


        RegisterHandler registerHandler = new RegisterHandler();
        LoginHandler loginHandler = new LoginHandler();
        LogoutHandler logoutHandler = new LogoutHandler();
        CreateGameHandler createGameHandler = new CreateGameHandler();
        GetGamesHandler getGamesHandler = new GetGamesHandler();
        JoinGameHandler joinGameHandler = new JoinGameHandler();
        DeleteAllHandler deleteAllHandler = new DeleteAllHandler();

        Spark.post("/user", registerHandler::register);

        Spark.post("/session" , loginHandler::login);

        Spark.delete("/session" , logoutHandler::logout);

        Spark.post("/game", createGameHandler::createGame);

        Spark.get("/game", getGamesHandler::getGames);

        Spark.put("/game", joinGameHandler::joinGame);

        Spark.delete("/db" , deleteAllHandler::deleteAll);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
