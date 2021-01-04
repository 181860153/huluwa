import javax.print.DocFlavor;
import java.io.*;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private int portNumber;
    private boolean OK = false;
    private ArrayList<Socket> clientSocket = new ArrayList<>();
    private ServerSocket serverSocket;
    private ArrayList<PrintWriter> out = new ArrayList<>();
    private ArrayList<BufferedReader> in = new ArrayList<>();
    private boolean logging = false;
    File file;

    FileOutputStream fileOutputStream;


    long beginTime = 0;
    public Server(int i) throws IOException {
        portNumber = i;
        serverSocket = new ServerSocket(portNumber);
        if (logging) {
            file = new File("log.txt");
            fileOutputStream = new FileOutputStream(file);
        }
    }

    public void listen(int i) throws IOException {
        if(!OK) return;

        String inputLine;
        while ((inputLine = in.get(i).readLine()) != null) {
            //Map.response(inputLine);
            String finalInputLine = inputLine;
            out.get(0).println((i+1) + " " + inputLine);
            out.get(1).println((i+1) + " " + inputLine);
            //System.out.println((i+1) + " " + inputLine);
            if (logging) {
                if(finalInputLine.equals("Space"))
                    beginTime = System.currentTimeMillis();
                else
                {
                    int t = (int) (System.currentTimeMillis()-beginTime);
                    fileOutputStream.write(Integer.toString(t).getBytes());
                    fileOutputStream.write(' ');
                    fileOutputStream.write(finalInputLine.getBytes());
                    fileOutputStream.write('\n');
                }
            }else if(finalInputLine.equals("Space")){
                finalInputLine = "Random666";
                out.get(0).println((i+1) + " " + finalInputLine);
                out.get(1).println((i+1) + " " + finalInputLine);
            }

        }
    }

    public void setup() {
        try {
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            this.clientSocket.add(clientSocket);
            this.out.add(out);
            this.in.add(in);
            if(this.in.size()==2)
                OK = true;
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            //System.out.println(e.getMessage());
        }
    }

    public void send(String str){
        out.get(0).println(str);
        out.get(1).println(str);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(5000);
        for(int i=0;i<2;i++)
        {
            server.setup();
        }

        for (int i = 0; i < 2; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    server.listen(finalI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        server.send("OK");
    }
}
