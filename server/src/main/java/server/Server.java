package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.get("/auth/:username", (req, res) -> {
            String username = req.params(":username");
            return "Auth token for user " + username + " nothing yet";
        });

        RegisterHandler registerHandler = new RegisterHandler();
        LoginHandler loginHandler = new LoginHandler();

        Spark.post("/user", registerHandler::register);

        Spark.post("/session" , loginHandler::login);

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
