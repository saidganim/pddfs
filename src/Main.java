



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class Main {

    /*
        (1111) 1: 2, 3, 6, 7
        (1112) 2: 1
        (1113) 3: 1, 9, 8, 7, 4
        (1114) 4: 3, 5
        (1115) 5: 4, 9
        (1116) 6: 1
        (1117) 7: 3, 1
        (1118) 8: 3
        (1119) 9: 3, 5

     */

    public static Integer counter = 0;
    public static Long start;
    private static int NODENUM = 40;
    private static int PROBABILITY = 100; // percentage

//    private static Config[] config_generator(){
//        Config[] configs = new Config[NODENUM];
//        for(int i = 0 ; i < NODENUM; ++i)
//            configs[i] = new Config();
//        configs[0].initiator = true;
//
//        // Generating links for nodes
//
//        NodeInstance[] nodeInstances = new NodeInstance[NODENUM];
//        int port = 1111;
//        for(int i = 0; i < NODENUM; ++i){
//            nodeInstances[i] = new NodeInstance(new Pair<String, Integer>("127.0.0.1", port++),i + 1);
//        }
//        Random rand = new Random();
//        for(int i = 0; i < NODENUM; ++i){
//            configs[i].links = new ArrayList<NodeInstance>();
//            for(int j = 0; j <= i; ++j){
//
//                if(j == i || rand.nextInt(100) > PROBABILITY)
//                    continue;
//                configs[i].links.add(nodeInstances[j]);
//                configs[j].links.add(nodeInstances[i]);
//            }
//        }
//
//        for(int i = 0; i < NODENUM; ++i)
//                System.out.println(" CONFIG " +  i + " is sent " + configs[i].links);
//        return configs;
//    }



    static int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }


    private static Config[] config_generator(){
        // 10.141.0.49, 10.141.0.50, 10.141.0.51

        ArrayList<String> das4_network = new ArrayList<String>(){{
//            add("node002/10.141.0.2");
//            add("node003/10.141.0.3");
//            add("node004/10.141.0.4");
//            add("node005/10.141.0.5");
//            add("node006/10.141.0.6");
//            add("node007/10.141.0.7");
//            add("node008/10.141.0.8");
            add("node010/10.141.0.10");
            add("node012/10.141.0.12");
            add("node013/10.141.0.13");
            add("node014/10.141.0.14");
            add("node015/10.141.0.15");
            add("node016/10.141.0.16");
            add("node017/10.141.0.17");
            add("node018/10.141.0.18");
            add("node019/10.141.0.19");
            add("node020/10.141.0.20");
            add("node021/10.141.0.21");
            add("node022/10.141.0.22");
            add("node023/10.141.0.23");
//            add("node026/10.141.0.26");
//            add("node027/10.141.0.27");
            add("node030/10.141.0.30");
            add("node031/10.141.0.31");
            add("node040/10.141.0.40");
            add("node041/10.141.0.41");
            add("node042/10.141.0.42");
            add("node043/10.141.0.43");
            add("node044/10.141.0.44");
            add("node046/10.141.0.46");
            add("node047/10.141.0.47");
            add("node048/10.141.0.48");
            add("node049/10.141.0.49");
            add("node050/10.141.0.50");
            add("node051/10.141.0.51");
            add("node059/10.141.0.59");
            add("node060/10.141.0.60");
            add("node061/10.141.0.61");
            add("node062/10.141.0.62");
//            add("node065/10.141.0.65");
        }};
        InetAddress ip = null;
        String hostname;

        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
//            System.out.println("Your current Hostname : " + hostname);

        } catch (UnknownHostException e) {

            e.printStackTrace();
        }

        Config[] result = new Config[1];
        result[0] = new Config();


        result[0].initiator = das4_network.indexOf(ip.toString()) == 0? true : false;
        result[0].id = das4_network.indexOf(ip.toString()) + 1;
        result[0].links = new ArrayList<NodeInstance>();
        int index = das4_network.indexOf(ip.toString()) + 1;

        int level = log(index, 2);
        int parentid = (int) (Math.pow(2, level - 1) + Math.floor((index - Math.pow(2, level)) / 2)) - 1;
        int childid = (int) (Math.pow(2, level + 1) + 2 * (index - Math.pow(2, level))) - 1;

        // link to a parent
        if(!result[0].initiator)
            result[0].links. add(new NodeInstance(new Pair<String, Integer>(das4_network.get(parentid).substring(8), 1111),das4_network.indexOf(das4_network.get(parentid)) + 1));

        // links to children
        if(childid < das4_network.size())
            result[0].links. add(new NodeInstance(new Pair<String, Integer>(das4_network.get(childid).substring(8), 1111),das4_network.indexOf(das4_network.get(childid)) + 1));
        if(childid + 1 < das4_network.size())
            result[0].links. add(new NodeInstance(new Pair<String, Integer>(das4_network.get(childid + 1).substring(8), 1111),das4_network.indexOf(das4_network.get(childid + 1)) + 1));

//        for(String das4_node : das4_network){
//            if(ip.toString().substring(8).equals(das4_node.substring(8)))
//                continue;
//            result[0].links. add(new NodeInstance(new Pair<String, Integer>(das4_node.substring(8), 1111),das4_network.indexOf(das4_node) + 1));
//        }

        return result;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        Config[] configs = config_generator();
        //============================================================= First configuration ========================================================
//        configs[0].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1112),2));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1114),4));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1115),5));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1116),6));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1117),7));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1118),8));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1119),9));
//        }};
//        configs[1].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[2].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[3].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[4].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[5].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[6].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[7].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[8].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};

//        configs[0].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1112),2));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1116),6));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1117),7));
//        }};
//        configs[1].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[2].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1119),9));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1118),8));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1117),7));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1114),4));
//        }};
//        configs[3].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1115),5));
//        }};
//        configs[4].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1114),4));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1119),9));
//        }};
//        configs[5].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[6].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//        }};
//        configs[7].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//        }};
//        configs[8].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1115),5));
//        }};


        // =========================================================================
//// CLIQUE
//
//        configs[0].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1112),2));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1114),4));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1115),5));
//        }};
//
//        configs[1].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1114),4));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1115),5));
//        }};
//
//        configs[2].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1112),2));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1114),4));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1115),5));
//        }};
//
//        configs[3].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1112),2));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1115),5));
//        }};
//
//        configs[4].links = new ArrayList<NodeInstance>(){{
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1111),1));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1112),2));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1113),3));
//            add(new NodeInstance(new Pair<String, Integer>("127.0.0.1", 1114),4));
//        }};

        ArrayList<Runnable> fs = new ArrayList<Runnable>();

        for(int i = 0; i < configs.length; ++i){
            final Config conf = configs[i];
            System.out.println("STARTED NODE " + conf + " children : " + conf.links);
            fs.add(() -> {
                Node node = new Node(conf);
            });
        }
        start = System.currentTimeMillis();
        Thread[] threads = new Thread[configs.length];

        for(Runnable curr : fs){
            threads[fs.indexOf(curr)] = new Thread(curr);
            threads[fs.indexOf(curr)].start();
        }

        for(Runnable curr : fs){
            threads[fs.indexOf(curr)].join();
        }

    }
}
