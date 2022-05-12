package HTTPServer;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ServerWorker extends Thread {
    String header;
    byte[]headerBytes;
    byte[]resourceBytes;
    byte[]requestData;

    private Socket socket;
    private String httpMethod, requestedPath;
    private String notFoundPath = "/404.html";
    private HashMap<String, String> httpHeaders = new HashMap<String, String>();
    private StringBuilder httpBody = new StringBuilder();

    ServerWorker(Socket socket) {
        this.socket = socket;
    }

    private void completeRequest() {
        if (requestedPath.equals("/"))
            requestedPath = "/index.html";

        if (httpMethod.equals("GET")) {
            Boolean resourceExists = Extensions.resourcesExists(requestedPath);
            GET(resourceExists);
        }

        if (httpMethod.equals("POST")) {
            POST();
        }
    }

    private void GET(Boolean resourceExists) {
        StringBuilder responseBuilder = new StringBuilder();
        FileInputStream fileInputStream;
        String targetURI;

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

            OutputStream outStream = socket.getOutputStream();
            outStream.write(responseBuilder.toString().getBytes());
            outStream.write(fileInputStream.readAllBytes());
            outStream.flush();
           // outStream.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void POST() {
        String contentType = httpHeaders.get("Content-Type");
        String targetExtension = Extensions.getExtensionType(contentType);
        String targetURI = requestedPath + targetExtension;
        StringBuilder responseBuilder = new StringBuilder();

        try {

            File createdFile = new File(SocketServer.resourcesDirectory + targetURI);

            if (createdFile.createNewFile()) {
                Extensions.write(targetURI, httpBody.toString());
                responseBuilder.append("HTTP/1.0 200 OK\r\n");
            } else
                responseBuilder.append("HTTP/1.0 404 Not Found\r\n");

            responseBuilder.append("Date: " + Extensions.getCurrentDate() + "\n");
            responseBuilder.append("Server: macOS\n");
            responseBuilder.append("Connection: Closed\n");
            responseBuilder.append("\r\n");

            OutputStream outStream = socket.getOutputStream();
            outStream.write(responseBuilder.toString().getBytes());
            outStream.flush();
            outStream.close();

        } catch (IOException exception) {
            System.out.println("An error occurred in the upload process.");
            exception.printStackTrace();
        }
    }



    //    this function is used to build headerMap
    void parseHeader(){
//      split header line by line
        String[] splittedHeader=header.split("\\R");
        String contentType=splittedHeader[0].split(": ")[1];
        httpHeaders.put("content-type",contentType);
    }

 /*   This function separate between header bytes and resource bytes
*/
    void bytesPostReader(byte[]dataBytes){
        String dataInString=new String(dataBytes);
//        System.out.println(dataInString);
//         Search for the separator between header and resource data
        int resourceStartIndex=dataInString.indexOf("\n\r");

        headerBytes=new byte[resourceStartIndex];
        for(int i=0;i<headerBytes.length;i++){
            headerBytes[i]=dataBytes[i];
        }
        this.header=new String(headerBytes);

        int padding=3;
        int dataStartIndex=resourceStartIndex+padding;

        resourceBytes=new byte[dataBytes.length-dataStartIndex];
        for(int i=0;i<resourceBytes.length;i++){
            resourceBytes[i]=dataBytes[i+dataStartIndex];
        }
    }

    //  return file extension, for example if I have content type "text/html", this function simply returns "html"
    String getFileExtension(){
        return httpHeaders.get("content-type").split("/")[1];
    }

    @Override
    public void run() {
        super.run();

        try {
//            InputStreamReader inStream = new InputStreamReader(socket.getInputStream());
//            int t;
//            int i=0;
//           StringBuilder clientCompleteRequest=new StringBuilder();
//            while((t=inStream.read())!= -1)
//            {
//                // convert the integer true to character
//                char c = (char)t;
//                clientCompleteRequest.append(c);
//
//            }
//            System.out.println(clientCompleteRequest.toString());
//            requestData=clientCompleteRequest.toString().getBytes();
//           String dataStr=new String(requestData);
//           String clientRequest=dataStr.split("\\R")[0];
//
//             httpMethod=clientRequest.split(" ")[0];
//             requestedPath=clientRequest.split(" ")[1];
//            System.out.println(httpMethod);
//            System.out.println(requestedPath);
//            completeRequest();


            InputStreamReader inStream = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inStream);

            String lineReader = bufferedReader.readLine();
            requestData=bufferedReader.toString().getBytes();

            String[] lineComponents = lineReader.split(" ");
            httpMethod = lineComponents[0];
            requestedPath = lineComponents[1];
            System.out.println(new String(requestData));
            completeRequest();
//            if(httpMethod.equals("GET")){
//
//            }
           // System.out.println(header);
//            File file=new File("/Users/test/Desktop/Term 8/Computer networks/Final-Project/Sockets/Server/src/HTTPServer/Resources/hello.html");
//            FileOutputStream fileOutputStream=new FileOutputStream(file);
//            fileOutputStream.write(resourceBytes);
//            StringBuilder requestBuilder = new StringBuilder();
//
//            String lineReader = bufferedReader.readLine();
//
//            String[] lineComponents = lineReader.split(" ");
//
//            Boolean expectsData = false;
//
//            httpMethod = lineComponents[0];
//            requestedPath = lineComponents[1];

//            if (httpMethod == "POST") {
//                while ((lineReader=bufferedReader.readLine()).equals("\n\r")) {
//                    System.out.println(lineReader);
//                  //  System.out.println(lineReader.toString());
//                    if (lineReader.contains(":")) {
//                        String[] httpHeader = lineReader.split(":");
//                        httpHeaders.put(httpHeader[0], httpHeader[1].trim());
//                    }
//
//                    if (lineReader.isBlank()) {
//                        expectsData = true;
//                    }
//
//                    if (expectsData) {
//                        httpBody.append(lineReader);
//                    }
//
//                    requestBuilder.append(lineReader + "\r\n");
//                    lineReader = bufferedReader.readLine();
//                }
//            }

         //   completeRequest();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
