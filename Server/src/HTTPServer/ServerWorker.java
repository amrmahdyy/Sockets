package HTTPServer;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ServerWorker extends Thread {
    byte[] dataBytes;
    byte[] resourceBytes;
    private Socket socket;
    String clientRequest="";
    private String httpMethod, requestedPath;
    private String notFoundPath = "/404.html";
    private HashMap<String, String> httpHeaders = new HashMap<String, String>();
    private StringBuilder httpBody = new StringBuilder();

    ServerWorker(Socket socket) {
        this.socket = socket;
    }


    private void GET(Boolean resourceExists) {
        StringBuilder responseBuilder = new StringBuilder();
        FileInputStream fileInputStream;
        String targetURI;

        if (requestedPath.equals("/"))
            requestedPath = "/index.html";

        if (resourceExists) {
            targetURI = requestedPath;
            responseBuilder.append("HTTP/1.0 200 OK\r\n");

        } else {
            targetURI = notFoundPath;
            responseBuilder.append("HTTP/1.0 404 Not Found\r\n");
        }

        try {
            fileInputStream = Extensions.getFileInputStream(targetURI);

            String contentType = Extensions.getContentType(targetURI);
            Long contentLength = fileInputStream.getChannel().size();

            responseBuilder.append("Date: " + Extensions.getCurrentDate() + "\n");
            responseBuilder.append("Server: macOS\n");
            responseBuilder.append("Last-Modified: " + Extensions.getLastModifiedDate(targetURI) + "\n");
            responseBuilder.append("Content-Length: " + contentLength.toString() + "\n");
            responseBuilder.append("Content-Type: " + contentType + "\n");
            responseBuilder.append("Connection: Closed\n");
            responseBuilder.append("\r\n");
            OutputStream outputStream = (socket.getOutputStream());
            System.out.println(socket.isConnected());
            responseBuilder.append(fileInputStream);


            outputStream.write(responseBuilder.toString().getBytes());
            outputStream.flush();

            fileInputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


//    This function is used by POST function where it saves the resource

    void saveResource(byte[]resourceBytes){
        try{
            File file=new File(SocketServer.resourcesDirectory+requestedPath);
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            fileOutputStream.write(resourceBytes);
        }
        catch(IOException ioException){
            System.out.println("Can't save resource on Server");
            ioException.printStackTrace();
        }

    }


//    This function is responsible for copying the resource bytes from the main byte array by passing the start of resourceStartIndex
    void processPOST(int resourceStartIndex){
        int padding=3;
        int dataStartIndex=resourceStartIndex+padding;

         resourceBytes=new byte[dataBytes.length-dataStartIndex];
        for(int i=0;i<resourceBytes.length;i++){
            resourceBytes[i]=dataBytes[i+dataStartIndex];
        }
    }

    void POST(){
        saveResource(resourceBytes);
    }

    void processSocketStream(){
        try{
            InputStream is=socket.getInputStream();
            dataBytes=is.readAllBytes();

            String dataInString=new String(dataBytes);
            System.out.println(dataInString);
//            Search for the end of the header
            int resourceStartIndex=dataInString.indexOf("\n\r");

//            get Client request from the first line
             clientRequest=dataInString.split("\\R")[0];
//             Get the requested path from client
            requestedPath=clientRequest.split(" ")[1];
            String clientMethodType=clientRequest.split(" ")[0];

            if(clientMethodType.equals("GET")){
                Boolean isResourceExists=Extensions.resourcesExists(requestedPath);
                GET(isResourceExists);
            }

            else if(clientMethodType.equals("POST")) {
                processPOST(resourceStartIndex);
                POST();
            }
            else throw new RuntimeException("Unsupported request method type");
        }
        catch(IOException ioException){
            System.out.println("Error in reading socket stream");

        }

    }

    @Override
    public void run() {
        super.run();
        try{
            processSocketStream();
        }
        catch(Exception exception){
           exception.printStackTrace();
        }
        }
    }

