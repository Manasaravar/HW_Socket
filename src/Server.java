
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Server {
    private String host;
    private int port;
    private PrintWriter out;
    private Scanner in;

    private boolean isRunning = true;
    private int countQuotes = 0;


    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

//    public PrintWriter getOut() {
//        return out;
//    }
//
//    public Scanner getIn() {
//        return in;
//    }
//
//    public boolean isRunning() {
//        return isRunning;
//    }

    public void run() throws IOException {

        try (ServerSocket server = serverInit();
             Socket client = clientInit(server))
        {

            //send(out, "server> You connect to Server_Admin...");

            Thread outThread = new ServerOutThread(out);
            outThread.start();
            try {
                logInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            logWriter();
            String message;
            while (true) {
                message = in.nextLine();
                if (message.equals("break")) {
                    break;
                }
                out.println(quotes());
                countQuotes++;
                if (countQuotes == 3) {
                    out.println("Connection server is broken....");
                    server.close();
                }
            }
        } catch (IOException e) {
            System.out.println("***SERVER*** инициализация не прошла");
            throw new RuntimeException(e);
        } finally {
            in.close();
            out.close();
        }
    }
    public void logInfo() throws Exception{
        //open buffered reader for reading data from client
        boolean access = false;
      //  input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        do {
            String username = in.nextLine();
            System.out.println("username: " + username);
            String password = in.nextLine();
            System.out.println("password: " + password);

            if (username.equals("User") && password.equals("Password")) {
                out.println("Access granted");
                access = true;
            } else {
                out.println("Login Failed");
            }
        } while (!access);
        out.flush();
    }
    private ServerSocket serverInit() throws IOException {
        System.out.println("***SERVER*** starting...");
        return new ServerSocket(port, 10, InetAddress.getByName(host));
    }

    private Socket clientInit(ServerSocket server) {
        Socket client;
        try {
            client = server.accept(); // ожидание входящих подключений
            out = outInit(client);
            in = inInit(client);
            return client;
        } catch (IOException e) {
            System.out.println("клиент не подключился");
            throw new RuntimeException(e);
        }
    }

    private Scanner inInit(Socket client) {
        try {
            return new Scanner(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            System.out.print("соединение разорвано");
            throw new RuntimeException(e);
        }
    }

    private PrintWriter outInit(Socket client) {
        try {
            return new PrintWriter(
                    new OutputStreamWriter(
                            client.getOutputStream()
                    ), true
            );
        } catch (IOException e) {
            System.out.print("соединение разорвано");
            throw new RuntimeException(e);
        }
    }

    public void send(PrintWriter out, String message) {
        System.out.printf("***SERVER*** send message: %s\n\n", message);
        out.println(message);
    }

//    public String receive(Scanner in) {
//        return in.nextLine();
//    }

    public void logWriter() {
        InetAddress address = null;
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        try {
            FileWriter logWrite = new FileWriter("logWriter.txt");
            logWrite.write("Имя клиента: " + address.toString() + " ; Время подключения: " + currentTime.toString());
            logWrite.close();
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }
    public String quotes () throws FileNotFoundException {
        List<String> quotesString = new ArrayList<>();
        File file = new File("C:\\Users\\User\\IdeaProjects\\HW_5\\src\\text.txt");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            quotesString.add(scanner.nextLine());
        }
        scanner.close();
        return quotesString.get(((int)(Math.random() * (quotesString.size() - 1))));
    }
}

class ServerOutThread extends Thread {
    private final PrintWriter out;

    public ServerOutThread(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void run() {
        String message;
        Scanner console = new Scanner(System.in);
        while (!(message = console.nextLine()).equals("break")) {
            out.println(message);
        }
        out.println(message);
    }
}






