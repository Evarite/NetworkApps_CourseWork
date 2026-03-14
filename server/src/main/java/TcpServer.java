import java.io.*;
import java.net.*;
import org.apache.log4j.*;

public class TcpServer {
    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = true;
    Logger logger = Logger.getLogger(TcpServer.class);

    public TcpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        logger.info("Сервер запушчаны на порце " + port);
        while(running) {
            Socket client = serverSocket.accept();
            new Thread(new ClientHandler(client, logger)).start();
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    private record ClientHandler(Socket socket, Logger logger) implements Runnable {
        @Override
            public void run() {
                try (socket;
                     BufferedReader in = new BufferedReader
                             (new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String line;
                    while ((line = in.readLine()) != null) {
                        logger.info("Атрымана: " + line);
                    }
                } catch (IOException e) {
                    System.err.println("Памылка апрацоўкі кліента: " + e.getMessage());
                }
            }
        }
}


