package Workers;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientWorker {
    HashMap<String,String> headerMap;
    String header;
    byte[]dataBytes;
    byte[]headerBytes;
    byte[]resourceBytes;
    String clientRequest;
    ClientRequest clientRequestObj;
    OutputStream out;
    Socket socket;
    String resourcePath="/Users/test/Desktop/Term 8/Sockets/Client/src/Resources/";

    public ClientWorker(Socket socket,ClientRequest clientRequest) throws IOException {
        this.socket=socket;
        out=socket.getOutputStream();

        this.clientRequest=clientRequest.getClientRequest();
        this.clientRequestObj=clientRequest;

        System.out.println(this.clientRequest);


        if(clientRequestObj.getMethodType().equals("GET")){
            out.write(this.clientRequest.getBytes());
            out.flush();
            InputStream inputStream=socket.getInputStream();
            this.dataBytes=inputStream.readAllBytes();
            bytesHeaderReader();
            parseHeader();
            saveResource();
        }
//        If method type is POST, send file on the outputStream out
        else if(clientRequestObj.getMethodType().equals("POST"))
           POST();
        out.flush();
        out.close();
    }

    void POST() throws IOException {
        String targetFile=this.clientRequest.split("/")[1].split(" ")[0];
//        // System.out.println(targetFile);
        String extension=targetFile.split("[.]")[1];
        StringBuilder requestBuilder=new StringBuilder();
        requestBuilder.append("Content-Type: text/"+extension+"\n"+"\r\n");
////        System.out.println(requestBuilder.toString().length());
        FileInputStream fileInputStream=sendFile(targetFile);
//
//        System.out.println(fileInputStream.readAllBytes().length);
       // requestBuilder.append(fileInputStream.readAllBytes());
       // System.out.println("");
       // System.out.println(requestBuilder.toString().length());
        out.write((this.clientRequest+"\n").getBytes());
        out.flush();
        out.write(requestBuilder.toString().getBytes());
        out.write(fileInputStream.readAllBytes());
        out.flush();
//        out.write(requestBuilder.toString().getBytes());
//        out.flush();


        //out.write(fileInputStream.readAllBytes());
        //out.write(fileInputStream.readAllBytes());
//        out.write("\r\n".getBytes());

    }
//    for debugging
    public String getFileContent( FileInputStream fis ) throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader r = new InputStreamReader(fis, "UTF-8");  //or whatever encoding
        int ch = r.read();
        while(ch >= 0) {
            sb.append(ch);
            ch = r.read();
        }
        return sb.toString();
    }

    /*
     * This method takes clientRequest string as a parameter and sends the request to the server
     * */
    void sendRequest(String clientRequest) throws IOException {
        out.write((clientRequest+"\r\n").getBytes());
    }

//    This function uploads the file on the outputStream
    FileInputStream sendFile(String targetFile) {
        try{
            FileInputStream fileInputStream=new FileInputStream(resourcePath+targetFile);
          return fileInputStream;
        }
        catch(IOException ioException){
            System.out.println("Can't send the file to server");
        }
        return null;
    }
/*
* This function separate between header bytes and resource bytes
* */
     void bytesHeaderReader(){
         String dataInString=new String(dataBytes);
         System.out.println(dataInString);
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

/*
* This function return the requested resource name for example if the client requested "GET /image.jpeg 127.0.0.1 8080", the function
* returns "image"
 * */
     String getFileName(){
         Pattern pattern= Pattern.compile("\\/(.*?)\\.");
         Matcher matcher=pattern.matcher(clientRequest);
         if(matcher.find())
             return matcher.group(1);
         return getFileTimestamp();
     }

/*
*     Save the resource in Resources directory
* */
    void saveResource(){
        try{
            String extension=getFileExtension();
            String fileName=getFileName();
            if(!headerMap.get("statusMessage").equals("OK"))
                fileName="404";
            File file=new File(resourcePath+fileName+"."+extension);
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(resourceBytes);
        }
        catch(FileNotFoundException fnfe){
            System.out.println("Error in file path");
        } catch (IOException e) {
            System.out.println("Can't save resource");
        }

    }

/*
*     This function return the timestamp as a string after parsing it on the correct format
*/
    String getFileTimestamp(){
        String fileTimestamp="";
//      Convert date string to date object
        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        try{
            Date result =  df.parse(headerMap.get("date"));
            fileTimestamp=String.valueOf(result.getTime());
        }
        catch(ParseException pe){
            System.out.println("Can't parse file date");
        }
        return fileTimestamp;
    }

//  return file extension, for example if I have content type "text/html", this function simply returns "html"
    String getFileExtension(){
        return headerMap.get("content-type").split("/")[1];
    }

    //    this function is used to build headerMap
     void parseHeader(){
        headerMap=new HashMap<>();
//      split header line by line
        String[] splittedHeader=header.split("\\R");
        String httpVersion=splittedHeader[0].split("/")[1].split(" ")[0];
        String statusCode=splittedHeader[0].split("/")[1].split(" ")[1];
        String statusMessage=splittedHeader[0].split("/")[1].split(" ")[2];
        String date=splittedHeader[1].split(": ")[1];
        String server=splittedHeader[2].split(": ")[1];
        String lastModified=splittedHeader[3].split(": ")[1];
        String contentLength=splittedHeader[4].split(": ")[1];
        String contentType=splittedHeader[5].split(": ")[1];
        String connection=splittedHeader[6].split(": ")[1];



        headerMap.put("httpVersion",httpVersion);
        headerMap.put("statusCode",statusCode);
        headerMap.put("statusMessage",statusMessage);
        headerMap.put("date",date);
        headerMap.put("server",server);
        headerMap.put("last-modified",lastModified);
        headerMap.put("content-length",contentLength);
        headerMap.put("content-type",contentType);
        headerMap.put("connection",connection);
    }

}
