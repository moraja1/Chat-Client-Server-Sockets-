package application;

import data.util.Server;

public class Application {
    
    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server();
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.run();
    }
}
