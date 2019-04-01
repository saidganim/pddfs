public class Pair<F, S> extends java.util.AbstractMap.SimpleImmutableEntry<F, S> {

    public Pair(F f, S s ) {
        super( f, s );
    }

    public String toString() {
        return "["+getKey()+","+getValue()+"]";
    }

}