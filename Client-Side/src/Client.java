import java.io.*;
import java.net.*;
/*
* The Client class provides functionality to let the client connect to any server
* providing sending and receiving message functionality
* */
public class Client {
    private int port;
    private String ip;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    Client(String ip,int port){
        this.port=port;
    }
//    Establishing connection with a server
    private void startConnection() {
        try{
            socket=new Socket(ip,port);
            out=new PrintWriter(socket.getOutputStream(),true);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(IOException ioe){
            System.out.println("Client couldn't establish connection");
        }
    }
    /*
    * This method takes message string as a parameter and returns
    * */
    private String sendMessage(String message){
        try{
            String res="";
            out.println(message);
            out.println("\n");
            res=in.readLine();
            return res;
        }
        catch(IOException ioe){
            System.out.println("Client couldn't send a message to the server");
        }
        return null;
    }
    public static void main(String[]args){
        Client client=new Client("127.0.0.1",8080);
        client.startConnection();
        String res=client.sendMessage("Hello From Client");
    }
}
