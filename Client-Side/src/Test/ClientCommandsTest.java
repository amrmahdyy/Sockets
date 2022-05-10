import Workers.ClientCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClientCommandsTest {
    private static String commandsFilePath="/Users/test/Desktop/Term 8/Computer networks/Final-Project/Sockets/Client-Side/src/Resources/userCommands.txt";

    @Test
    public void readCommands(){
        ClientCommands clientCommands=new ClientCommands(commandsFilePath);
//        expecting 2 client requests
        Assertions.assertEquals(2,clientCommands.getClientRequests().size());
    }
}
