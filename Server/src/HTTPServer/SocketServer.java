package HTTPServer;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    public static String resourcesDirectory = "/Users/Youssef/Desktop/Opensource/Sockets/Server/src/HTTPServer/Resources";
    public static Integer ACTIVE_WORKERS = 0;
    public static Integer PORT = 8080;

    public static void main(String[] args) throws Exception {
        try {

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Listening on port " + PORT + "...");

            while (true) {
                ACTIVE_WORKERS = ACTIVE_WORKERS + 1;
                Socket clientSocket = serverSocket.accept();
                ServerWorker serverWorker = new ServerWorker(clientSocket);
                serverWorker.start();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
