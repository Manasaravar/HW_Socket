import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final int PORT = 8081;
        final String HOST = "127.0.0.1";

        Thread serverThread = new Thread( () -> {
            try {
                new Server(HOST, PORT).run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                new Client(HOST, PORT).run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.start();
        clientThread.start();

        try {
            serverThread.join();
            clientThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
