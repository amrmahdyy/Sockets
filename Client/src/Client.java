import Workers.ClientCommands;
import Workers.ClientRequest;
import Workers.ClientWorker;

import java.io.*;
import java.net.*;
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
    public static String clientCommandPath="/Users/test/Desktop/Term 8/Computer networks/Final-Project/Sockets/Client/src/Resources/userCommands.txt";
    private Socket socket;
    private PrintWriter out;


//    Establishing connection with a server
      void startConnection(ClientRequest clientRequest) {
        try{
            socket=new Socket(clientRequest.getHostName(),Integer.parseInt(clientRequest.getPortNumber()));
            out=new PrintWriter(socket.getOutputStream(),true);

            sendRequest(clientRequest.getClientRequest());
            InputStream inputStream=socket.getInputStream();
            byte[]data=inputStream.readAllBytes();

            ClientWorker clientWorker=new ClientWorker(data,clientRequest.getClientRequest());
            socket.close();
            out.close();

        }
        catch(IOException ioe){
            System.out.println("Client couldn't establish connection");
        }
    }

    /*
    * This method takes clientRequest string as a parameter and sends the request to the server
    * */
     void sendRequest(String clientRequest){
            out.println(clientRequest);
            out.println("\n");
    }
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
