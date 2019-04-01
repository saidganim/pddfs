
import java.util.ArrayList;

public class Config {
    private static int counter=0;
    int port;
    int id;
    boolean initiator = false;
    ArrayList<NodeInstance> links;

    public Config(){
        id = ++counter;
        port = 1110 + id;
    }
}
