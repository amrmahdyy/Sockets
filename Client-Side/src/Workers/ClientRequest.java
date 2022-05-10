package Workers;

import java.security.InvalidParameterException;
import java.util.Locale;

public class ClientRequest {
    private enum Methods{
        GET,
        POST
    }
    private String methodType;
    private String fileName;
    private String hostName;
    private String portNumber="8080";


    public ClientRequest(String methodType,String fileName,String hostName){
        this.methodType=methodType;
        this.fileName=fileName;
        this.hostName=hostName;
    }
    public ClientRequest(String methodType,String fileName,String hostName,String portNumber){
        setMethodType(methodType);
        this.fileName=fileName;
        this.hostName=hostName;
        this.portNumber=portNumber;
    }
    public String getMethodType() {
        return methodType;
    }
    private void setMethodType(String methodType) {
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



    public String getHostName() {
        return hostName;
    }


    public String getPortNumber() {
        return portNumber;
    }

    /*
    * This function return the correct client request that will be sent
    * */
    public String getClientRequest(){
        return this.methodType+" "+this.fileName+" "+this.hostName+" "+this.portNumber;
    }
}
