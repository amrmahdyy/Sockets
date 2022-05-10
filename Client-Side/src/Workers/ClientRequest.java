package Workers;

import java.security.InvalidParameterException;

public class ClientRequest {
    private enum Methods{
        GET,
        POST
    }
    private String methodType;
    private String fileName;
    private String hostName;
    private String portNumber="8080";

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        switch (methodType){
            case "GET":
                this.methodType = Methods.GET.name();
            case "POST":
                this.methodType=Methods.POST.name();
            default:
                throw new InvalidParameterException("This method type is not supported");
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }
}
