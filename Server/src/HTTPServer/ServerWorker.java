package HTTPServer;

import java.io.*;
import java.net.Socket;

public class ServerWorker extends Thread {
    private Socket socket;
    private String httpMethod, requestedPath;
    private String notFoundPath = "/404.html";

    ServerWorker(Socket socket) {
        this.socket = socket;
    }

    private void completeRequest() {
        if (requestedPath.equals("/"))
            requestedPath = "/index.html";

        if (httpMethod.equals("GET")) {
            Boolean resourceExists = Extensions.resourcesExists(requestedPath);
            System.out.println(resourceExists);
            GET(resourceExists);
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


    @Override
    public void run() {
        super.run();

        try {
            InputStreamReader inStream = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inStream);
            String lineReader = bufferedReader.readLine();
            String[] lineComponents = lineReader.split(" ");

            httpMethod = lineComponents[0];
            requestedPath = lineComponents[1];

            completeRequest();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
