import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    final String HOST;
    final int PORT;
    Scanner in;
    PrintWriter out;

    public Client(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    public void run() throws IOException {
        try (Socket client = new Socket(HOST, PORT)) {
            in = new Scanner(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            //out.println("hello server");

            new ClientOutThread(out).start();
            String serverMessage;
            enterLogin();
            while (true) {
                serverMessage = in.nextLine();
                if (serverMessage.equals("break")) {
                    break;
                }
                System.out.println(serverMessage);
            }
        } finally {
            in.close();
            out.close();
        }
    }
    public void enterLogin () {
        boolean access = false;
        do {
            //prompt for user name
            String username = JOptionPane.showInputDialog(null, "Enter User Name:");

            //send username to server
            out.println(username);

            //prompt for password
            String password = JOptionPane.showInputDialog(null, "Enter Password");

            //send password to server
            out.println(password);
            out.flush();

            //read response from server
            String response;
            response = in.nextLine();
            //display response
            JOptionPane.showMessageDialog(null, response);
            if (response.equals("Access granted")) {
                access = true;
            }
        } while (!access);

    }
}

class ClientOutThread extends Thread {
    private final PrintWriter out;
    private final Scanner console;

    public ClientOutThread(PrintWriter out) {
        this.out = out;
        this.console = new Scanner(System.in);
    }

    @Override
    public void run() {
        String message;

        while (true) {
            message = console.nextLine();
            if (message.equals("break")) {
                out.println("break");
                break;
            } else {
                out.println("Client_Korniushin> " + message);
            }
        }
    }
}