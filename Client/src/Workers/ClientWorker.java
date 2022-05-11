package Workers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public ClientWorker(byte[] dataBytes,String clientRequest){
        this.dataBytes=dataBytes;
        this.clientRequest=clientRequest;

        bytesReader();
        parseHeader();

        saveResource();
    }

/*
* This function separate between header bytes and resource bytes
* */
     void bytesReader(){
         String dataInString=new String(dataBytes);
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
            File file=new File("/Users/test/Desktop/Term 8/Computer networks/Final-Project/Sockets/Client/src/Resources/"+fileName+"."+extension);
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
        String contentType=splittedHeader[5].split(": ")[1];;
        String connection=splittedHeader[6].split(": ")[1];;

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
