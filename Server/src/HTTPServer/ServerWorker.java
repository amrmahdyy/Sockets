package HTTPServer;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ServerWorker extends Thread {
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
            outStream.close();

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

    @Override
    public void run() {
        super.run();

        try {
            InputStreamReader inStream = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inStream);
            StringBuilder requestBuilder = new StringBuilder();

            String lineReader = bufferedReader.readLine();
            String[] lineComponents = lineReader.split(" ");

            Boolean expectsData = false;

            httpMethod = lineComponents[0];
            requestedPath = lineComponents[1];

            if (httpMethod == "POST") {

                while (lineReader != null) {
                    if (lineReader.contains(":")) {
                        String[] httpHeader = lineReader.split(":");
                        httpHeaders.put(httpHeader[0], httpHeader[1].trim());
                    }

                    if (lineReader.isBlank()) {
                        expectsData = true;
                    }

                    if (expectsData) {
                        httpBody.append(lineReader);
                    }

                    requestBuilder.append(lineReader + "\r\n");
                    lineReader = bufferedReader.readLine();
                }
            }

            completeRequest();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
