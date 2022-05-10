package Workers;

public class ClientRequest {
    private enum Methods{
        GET,
        POST
    }
    private String methodType;
    private String fileName;
    private String hostName;
    private String portNumber="8080";

    @Override
    public boolean equals(Object obj){
        if(!this.getClass().equals(obj.getClass()))
            return false;
        ClientRequest otherClientRequest=(ClientRequest) obj;
        return this.methodType.equals(otherClientRequest.methodType) && this.hostName.equals(otherClientRequest.hostName)
                && this.portNumber.equals(otherClientRequest.portNumber) && this.hostName.equals(otherClientRequest.hostName);
    }

    public ClientRequest(String methodType,String fileName,String hostName){
        setMethodType(methodType.toUpperCase());
        this.fileName=fileName;
        this.hostName=hostName;
    }
    public ClientRequest(String methodType,String fileName,String hostName,String portNumber){
        setMethodType(methodType.toUpperCase());
        this.fileName=fileName;
        this.hostName=hostName;
        this.portNumber=portNumber;
    }
    public String getMethodType() {
        return methodType;
    }
    private void setMethodType(String methodType) throws IllegalArgumentException {
        switch (methodType){
            case "GET":
                this.methodType = Methods.GET.name();
                break;
            case "POST":
                this.methodType=Methods.POST.name();
                break;
            default:
                throw new IllegalArgumentException("This method type is not supported");
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
