
import java.io.Serializable;
import java.util.Vector;

public class _Message implements Serializable{
    public enum MessageType { DISCOVER, REJECT, TERMINATE};
    public Vector<Integer> graph_path = new Vector<Integer>();
    public MessageType messageType;
    public NodeInstance sender;
    public int id;
    private String _type(){
        switch(messageType) {
            case DISCOVER:
                return "DISCOVER:";
            case REJECT:
                return "REJECT:";
            default:
                return "TERMINATE:";
        }
    }
    public String toString(){
        return  _type() + " FROM " + id;
    }
}
