

public class NodeInstance {
    public Pair<String, Integer> addr;
    public int id;

    public NodeInstance(){}
    public NodeInstance(Pair<String, Integer> pair, int id){
        this.addr = pair;
        this.id = id;
    }

    public boolean equals(NodeInstance second){
        boolean not_equals = (this.id != second.id);
        if(not_equals)
            return false;
        return true;
    }

    public String toString(){
        return "Object NodeInstance{id:" + id + ";addr:" + addr + "}";
    }
}


