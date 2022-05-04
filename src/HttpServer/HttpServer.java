package HttpServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HttpServer  implements  Runnable{
//    place the absolute path for the resources folder
    private static String parentResource="/Users/test/Desktop/Term 8/Computer networks/Final-Project/Sockets/src/resources";
    private  Socket socket;
    private OutputStream clientOutput;
    private String methodType="";
    private String path="";
    public HttpServer(Socket s){
        socket=s;
    }
    /*
     * function that takes resource path as a name and matches with the path name to return the correct content type
     * if file type is not supported, text/plain is returned
     * */
    private String getContentType(String path)  {
        if(path.endsWith(".html") || path.endsWith(".htm"))
            return "text/html";
        if(path.endsWith(".json"))
            return "application/json";
        if(path.endsWith(".jpeg"))
            return "image/jpeg";
        if(path.endsWith(".jpg"))
            return "image/jpg";
        if(path.endsWith(".xml"))
            return "text/xml";
        if(path.endsWith(".mp4"))
            return "video/mp4";
        if(path.endsWith(".mpeg"))
            return "audio/mpeg";
        if(path.endsWith(".png"))
            return "image/png";
        if(path.endsWith(".ico"))
            return "image/x-icon";
        return "text/plain";
    }
    //    getting last modified date to embed it in header response and use it in caching functionality
    private Date getLastModifiedDate(){
        if(parentResource==null || path==null)return null;
        File file=new File(parentResource+path);
        return new Date(file.lastModified());
    }
    /*
     * Handle evey get request from client and respond with the correct header format and write to socket the resource found
     * */
    private void GET_Handler(String path) throws IOException{
        String reqFound="";
        String contentLength="";
        String date;
        String server="";
        String contentType="";
        String connectionStatus="";
        String lastModified="";
        // if path is '/' convert it to 'index.html'
        if(path.equals("/"))
            path="/index.html";
        System.out.println(path);
        FileInputStream fileInputStream=new FileInputStream(parentResource+path);
        // resource is found, send success message
        reqFound="HTTP/1.0 200 OK\r\n";
        // write server information with mentioning the current version
        server="Server: Java HTTP Server from AMR MAHDY : 1.0\n";
        // setting the date
        date="Date: "+new Date().toString()+"\n";
        // Getting last modified date
        lastModified="Last-Modified: "+getLastModifiedDate().toString()+"\n";
        // evaluating content length
        contentLength="Content-Length: "+String.valueOf(fileInputStream.getChannel().size())+"\n";
        contentType="Content-Type: "+getContentType(path)+"\n";
        connectionStatus="Connection: Closed\n";
        String header=reqFound+server+date+lastModified+contentType+contentLength+connectionStatus;
        clientOutput.write(header.getBytes());
        // separating header and body with a blank line is a ***MUST***
        clientOutput.write("\r\n".getBytes());
        // writing the file
        clientOutput.write(fileInputStream.readAllBytes());
        // clearing the buffer from any remaining Bytes
        clientOutput.flush();
        // closing the connection
        clientOutput.close();
    }
    private void fileNotFound() throws IOException{
        FileInputStream fileInputStream=new FileInputStream(parentResource+"/404.html");
        String reqNotFound="";
        String date="";
        String server="";
        String contentLength="";
        String connectionStatus="";
        String contentType="";
        String lastModified="";
        reqNotFound="HTTP/1.0 404 Not Found\r\n";
        // write server information with mentioning the current version
        server="Server: Java HTTP Server from AMR MAHDY : 1.0\n";
        // setting the date
        date="Date: "+new Date().toString()+"\n";
        // Getting last modified date
        lastModified="Last-Modified: "+getLastModifiedDate().toString()+"\n";
        // evaluating content length
        contentLength="Content-Length: "+String.valueOf(fileInputStream.getChannel().size())+"\n";
        contentType="Content-Type: "+getContentType("404.html")+"\n";
        connectionStatus="Connection: Closed\n";
        String header=reqNotFound+server+date+lastModified+contentType+contentLength+connectionStatus;
        clientOutput.write(header.getBytes());
        clientOutput.write("\n".getBytes());
        clientOutput.write(fileInputStream.readAllBytes());
        clientOutput.flush();
        clientOutput.close();
    }
    public static void main(String[]args){
        try(ServerSocket serverSocket=new ServerSocket(8080)){
            System.out.println("Server is listening on port 8080");
            while(true){
                // handling new incoming requests
                HttpServer httpServer=new HttpServer(serverSocket.accept());
                Thread thread=new Thread(httpServer);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * Thread worker
     * */
    @Override
    public void run() {
        InputStreamReader isr= null;
        try {
            isr = new InputStreamReader(socket.getInputStream());
            BufferedReader br=new BufferedReader(isr);
            StringBuilder request=new StringBuilder();
            String line;
            // read line by line
            try {
                line = br.readLine();
                while (!line.isBlank()) {
                    request.append(line + "\r\n");
                    line = br.readLine();
                }
                String command = request.toString().split("\n")[0];
                this.methodType = command.split(" ")[0];
                this.path = command.split(" ")[1];
                // for debugging
                System.out.println("METHOD TYPE " + this.methodType);
                System.out.println("Path " + this.path);


                clientOutput = socket.getOutputStream();
                // handling GET requests
                if (this.methodType .equals("GET")) {
                    try {
                        GET_Handler(path);
                    } catch (IOException ioe) {
                        fileNotFound();
                    }
                    System.out.println("Got new message: " + socket.toString());
                }
            }
            catch(IOException ioe){

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
