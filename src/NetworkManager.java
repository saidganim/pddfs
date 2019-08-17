
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkManager {
    private int port;
    private List<NodeInstance> nodesList = null;
    private Node nodeInstance = null;
    private ServerSocket ssocket = null;
    private Queue<Message> messageBuffer = new LinkedBlockingQueue<Message>();
    private NetworkManager(){
        port = new Random().nextInt(65535);
    }
    private Thread handler = null;
    public int id;

    public NetworkManager(int port){
        this.port = port;
    }

    public void setNodesList(List<NodeInstance> nl){
        nodesList = nl;
    }

    public void sendMessage(NodeInstance addr, _Message mess){
        synchronized (Main.counter){
            ++Main.counter;
        }
        mess.id = this.id;
        Socket socket = null;
        System.out.println("SENDS " + mess.toString() + " TO " + addr.id);
        try {
            socket= new Socket(addr.addr.getKey(), addr.addr.getValue());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            os.writeObject(mess);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void init(Node node){
        try {
            ssocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.out.println( this.port + " Server Socket IOException " + e);
            //System.exit(1);
        }
        this.nodeInstance = node;

        Runnable worker = () -> {
            while(true){
                Message nextMess;
                synchronized (this.messageBuffer){
                    if(messageBuffer.size() == 0)
                        try {
                            messageBuffer.wait();
                        } catch (InterruptedException e) {}
                    nextMess = messageBuffer.remove();
//                    messageBuffer.remove();
//                    try {
//                        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
//                        os.writeObject(_mess);
//                    } catch (IOException e) {
//                        //System.out.println("Can't respond to message");
//                    }
                }
                Socket socket = nextMess.sender;
                nextMess.data.sender = new NodeInstance();
                nextMess.data.sender.addr = new Pair<String, Integer>(socket.getInetAddress().getHostAddress(), socket.getPort());
                nextMess.data.sender.id =  nextMess.data.id;
                this.nodeInstance.handleMessage(nextMess.data);
            }

        };
        this.handler = new Thread(worker);
        this.handler.start();
    }

    public void run() throws IOException {
        ObjectInputStream is;
        while(true){
            Socket socket = ssocket.accept();
            try {
                is = new ObjectInputStream(socket.getInputStream());
                _Message _mess = (_Message)is.readObject();
                Message newMessage = new Message();
                newMessage.data = _mess;
                newMessage.sender = socket;
                synchronized (messageBuffer){
                    System.out.println("NODE-" + this.nodeInstance._id + " ARRIVED " + _mess.toString());
                    messageBuffer.add(newMessage);
                    messageBuffer.notify();
                }
                socket.close();

            } catch (ClassNotFoundException e) {
                //System.out.println("Can't read Message");
            }
        }
    }


}
