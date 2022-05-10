import Workers.ClientRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClientRequestTest {

//    Testing creating a clientRequest with unsupported method type
    @Test
    public void useWrongMethodType(){
        Throwable throwable=Assertions.assertThrows(IllegalArgumentException.class,()->
               new ClientRequest("PUT","index.html","8081")
                );
        Assertions.assertEquals("This method type is not supported",throwable.getMessage());
    }
}
