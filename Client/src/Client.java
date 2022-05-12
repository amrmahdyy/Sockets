import Workers.ClientCommands;
import Workers.ClientRequest;
import Workers.ClientWorker;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/*
* The Client class provides functionality to let the client connect to any server
* providing sending and receiving message functionality
* */
public class Client {
    public static String clientCommandPath="/Users/test/Desktop/Term 8/Sockets/Client/src/Resources/userCommands.txt";
    private Socket socket;
    private OutputStream out;


//    Establishing connection with a server
      void startConnection(ClientRequest clientRequest) {
        try{
            socket=new Socket(clientRequest.getHostName(),Integer.parseInt(clientRequest.getPortNumber()));
            ClientWorker clientWorker=new ClientWorker(socket,clientRequest);

        }
        catch(IOException ioe){
            System.out.println("Client couldn't establish connection");
        }
    }

    /*
    * This method takes clientRequest string as a parameter and sends the request to the server
    * */
//     void sendRequest(String clientRequest) throws IOException {
//            out.write((clientRequest+"\r\n").getBytes());
//    }
//    void sendFile() throws IOException {
//        out.write("Content-Type: text/html\n".getBytes());
//        out.write("\r\n".getBytes());
//        FileInputStream fileInputStream=new FileInputStream("/Users/test/Desktop/Term 8/Sockets/Client/src/Resources/hello.html");
//        out.write(fileInputStream.readAllBytes());
//        out.flush();
////        out.println("\r\n");
////        out.println("<div>hello</div>\n");
//    }
    public static void main(String[]args){
        ClientCommands clientCommands=new ClientCommands(clientCommandPath);
        ArrayList<ClientRequest> clientRequests=clientCommands.getClientRequests();

        for(ClientRequest clientRequest:clientRequests){
            Client client=new Client();
            client.startConnection(clientRequest);
        }
//        String res=client.sendMessage();
    }
}
