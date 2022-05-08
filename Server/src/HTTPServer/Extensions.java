package HTTPServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class Extensions {
    public static boolean resourcesExists(String path) {
        File resource = new File(SocketServer.resourcesDirectory + path);
        System.out.println(resource.toPath().toString());
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

    public static FileInputStream getFileInputStream(String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(SocketServer.resourcesDirectory + path);
        return fileInputStream;
    }
}
