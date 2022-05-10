package Workers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientCommands {
    private ArrayList<ClientRequest> clientRequests;
    private String clientFilePath="";

    public ClientCommands(String clientFilePath){
       this.clientFilePath=clientFilePath;
       readCommands();
    }
    /*
    * this is the function that is responsible for reading commands from the file and create a new clientRequest object
    * for every command and add it to clientRequests arrayList
    * */
    public void readCommands(){
        try{
            File clientCommandsFile=new File(clientFilePath);
            Scanner reader=new Scanner(clientCommandsFile);
            String clientCommand="";
            String[] clientCommandsSeperated;
            ClientRequest clientRequest=null;
            while (reader.hasNextLine()){
                clientCommand=reader.nextLine();
                clientCommandsSeperated=clientCommand.split(" ");
//                The command format length must be 3 or 4, otherwise throw error
                switch (clientCommandsSeperated.length){
                    case 3:
                      clientRequest=new ClientRequest(clientCommandsSeperated[0],clientCommandsSeperated[1],clientCommandsSeperated[2]);
                    case 4:
                        clientRequest=new ClientRequest(clientCommandsSeperated[0],clientCommandsSeperated[1],clientCommandsSeperated[2],clientCommandsSeperated[3]);
                    default:
                        throw new IOException("Invalid command length, command must have a range of 3 to 4 elements");
                }
            }
            clientRequests.add(clientRequest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Can't read client commands file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ClientRequest> getClientRequests() {
        return clientRequests;
    }


}
