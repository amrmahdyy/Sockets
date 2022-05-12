package HTTPServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Extensions {
    public static boolean resourcesExists(String path) {
        File resource = new File(SocketServer.resourcesDirectory + path);
        return resource.exists();
    }

    public static Date getLastModifiedDate(String path) {
        File resource = new File(SocketServer.resourcesDirectory + path);
        return new Date(resource.lastModified());
    }

    public static String getCurrentDate() {
        Date date = new Date();
        return date.toString();
    }

    public static String getContentType(String path)  {
        if (path.endsWith(".html") || path.endsWith(".htm"))
            return "text/html";
        if (path.endsWith(".json"))
            return "application/json";
        if (path.endsWith(".jpeg"))
            return "image/jpeg";
        if (path.endsWith(".jpg"))
            return "image/jpg";
        if (path.endsWith(".xml"))
            return "text/xml";
        if (path.endsWith(".mp4"))
            return "video/mp4";
        if (path.endsWith(".mpeg"))
            return "audio/mpeg";
        if (path.endsWith(".png"))
            return "image/png";
        if (path.endsWith(".ico"))
            return "image/x-icon";
        return "text/plain";
    }

    public static String getExtensionType(String contentType) {
        if (contentType.equals("text/html"))
            return ".html";
        if (contentType.equals("application/json"))
            return ".json";
        if (contentType.equals("image/jpeg"))
            return ".jpeg";
        if (contentType.equals("image/jpg"))
            return ".jpg";
        if (contentType.equals("text/xml"))
            return ".xml";
        if (contentType.equals("video/mp4"))
            return ".mp4";
        if (contentType.equals("audio/mpeg"))
            return ".mpeg";
        if (contentType.equals("image/png"))
            return ".png";
        if (contentType.equals("image/x-icon"))
            return ".ico";

        return ".txt";
    }

    public static void write(String path, String dataWritten) throws IOException {
        FileWriter myWriter = new FileWriter(SocketServer.resourcesDirectory + path);
        myWriter.write(dataWritten);
        myWriter.close();
    }

    public static FileInputStream getFileInputStream(String path)  {
        try{
            FileInputStream fileInputStream = new FileInputStream(SocketServer.resourcesDirectory + path);
            return fileInputStream;
        }
        catch (IOException ioException){
            System.out.println("Failed to find file");
            ioException.printStackTrace();
        }
        return null;
    }
}
