import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private String hostName;
    private int portNumber;

    private Socket echoSocket;
    private PrintWriter out;
    private BufferedReader stdIn;
    private BufferedReader in;

    public Client(String hostname, int portNumber) {
        this.hostName = "localhost";
        this.portNumber = portNumber;
    }

    public void setup() {
        try {
            echoSocket = new Socket(hostName, portNumber);
            out =
                    new PrintWriter(echoSocket.getOutputStream(), true);
            in =
                    new BufferedReader(
                            new InputStreamReader(echoSocket.getInputStream()));
            stdIn =
                    new BufferedReader(
                            new InputStreamReader(System.in));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }

    public void send(String str) {
        out.println(str);
    }

    public void listen() throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            Map.response(inputLine);
            //System.out.println(portNumber + " " + inputLine);
        }
    }

    public void close() throws IOException {
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client("localhost", 5000);
        client.setup();

        new Thread(()->{
            String output;
            while (true) {
                try {
                    if ((output = client.in.readLine()) == null) break;
                    //System.out.println("echo: " + output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(()->{
            String userInput = null;
            while (true) {
                try {
                    if ((userInput = client.stdIn.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.send(userInput);
            }
        }).start();
    }
}
