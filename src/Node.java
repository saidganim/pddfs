
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Node {

    private static NodeInstance SENTINEL_NODE = new NodeInstance();
    private NetworkManager networkManager = null;
    private boolean initiator;
    private ArrayList<NodeInstance> links;
    private CopyOnWriteArrayList<NodeInstance> toNotify;
    private HashSet<NodeInstance> toTerminate = new HashSet<NodeInstance>();
    private HashSet<NodeInstance> children = new HashSet<NodeInstance>();
    private NodeInstance father;
    private Vector<Integer> fatherVec;
    public int _id;
    private boolean deactivated = false;

    public Node(Config config){
        this.networkManager = new NetworkManager(config.port);
        this.initiator = config.initiator;
        this.links = config.links;
        _id = config.id;
        this.toNotify = new CopyOnWriteArrayList<NodeInstance>(links);
        this.networkManager.setNodesList(this.links);
        this.networkManager.id = _id;
        this.networkManager.init(this);
        try {
            Runnable f = () -> {
                try {
                    this.networkManager.run();
                } catch (IOException e) {
                }
            };
            new Thread(f).start(); // starting listener in separate thread;
            if(config.initiator) {
                fatherVec = new Vector<Integer>();
                Thread.sleep(5000);
                System.out.println("INITIATOR STARTS ACTIVITY :)");
                synchronized (children){
                    moveOn(new Vector<Integer>());
                    if(children.size() == 0) deactivate();
                }
            } else {}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public NodeInstance getLink(NodeInstance child){
        for(NodeInstance cur : links){
            if(child.equals(cur))
                return cur;
        }
        return null;
    }



    synchronized private void moveOn(Vector<Integer> vectr){
        _Message mess = new _Message();
        mess.messageType =  _Message.MessageType.DISCOVER;
        if(father != null)
            toNotify.remove(father);
        for(NodeInstance node : toNotify){
            children.add(node);
            toTerminate.add(node);
            Vector<Integer> newVec = (Vector<Integer>) vectr.clone();
            newVec.add(node.id);
            mess.graph_path = newVec;
            networkManager.sendMessage(node, mess);
        }
    }

    int vectorCompare(Vector<Integer> a, Vector<Integer> b){
        for(int i = 0; i < Math.min(a.size(), b.size()); ++i){
            if(a.get(i) < b.get(i))
                return -1;
            else if(a.get(i) > b.get(i))
                return 1;
        }
        return 0;
    }

    private void deactivate(){
        if(toTerminate.size() != 0)
            return;
        if(!initiator){
//            System.out.println("=========================== NON-INITIATOR FINISHED THE TASK : " + _id + "; children " + children);
            _Message mess = new _Message();
            mess.messageType = _Message.MessageType.TERMINATE;
            networkManager.sendMessage(father, mess);
            System.out.println("NON-INITIATOR FINISHED THE TASK : " + Main.counter + " Messages are sent; Time : " +  (System.currentTimeMillis() - Main.start));
            return;
        }
//        System.out.println("INITIATOR FINISHED THE TASK : " + Main.counter + " Messages are sent; children: " + children + "; Time : " +  (System.currentTimeMillis() - Main.start));
        System.out.println("INITIATOR FINISHED THE TASK : " + Main.counter + " Messages are sent; Time : " +  (System.currentTimeMillis() - Main.start));
        System.exit(0);
    }

    public _Message handleMessage(_Message mess){
        NodeInstance sender = getLink(mess.sender);
        NodeInstance sendTo = null;
        synchronized (children){
            switch(mess.messageType){
                case REJECT:
                    children.remove(sender);
                    toTerminate.remove(sender);

                    if(toTerminate.size() == 0)
                        deactivate();
                    break;
                case DISCOVER:
                    if((father != null && father.equals(sender)))
                        return null;
                    _Message mess2 = new _Message();
                    mess2.messageType = _Message.MessageType.DISCOVER;
                    if(father == null && !initiator){
                        // first DISCOVER message
                        father = sender;
                        fatherVec = mess.graph_path;
                        moveOn(mess.graph_path); // passing DISCOVER further
                        if(children.size() == 0)
                            deactivate();
                    } else {
                        if(vectorCompare(fatherVec, mess.graph_path) == 1){
                            // new candidate to be the parent link
                            sendTo = father;
                            children.add(father);
                            toTerminate.add(father);
                            fatherVec = mess.graph_path;
                            children.remove(sender);
                            toTerminate.remove(sender);
                            father = sender;
                        } else if (vectorCompare(fatherVec, mess.graph_path) == 0){
                            // we have to reject it only if sender has better parent then current link
                            sendTo = sender;
                            if(sender.id > mess.graph_path.get(fatherVec.size())){
                                toTerminate.remove(sender);
                                children.remove(sender);
                                mess2.messageType = _Message.MessageType.REJECT;
                            }

                        } else {
                            sendTo = sender;
                        }
//                        if(sendTo != null){
                            Vector<Integer> newVec = (Vector<Integer>) fatherVec.clone();
                            newVec.add(sendTo.id);
                            mess2.graph_path = newVec;
                            networkManager.sendMessage(sendTo, mess2);
//                        }
                    }
                    break;
                case TERMINATE:
                    toTerminate.remove(sender);
                    deactivate();
                    break;
            }
        }


        return null;
    }

}
