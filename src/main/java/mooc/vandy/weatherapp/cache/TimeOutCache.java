package mooc.vandy.weatherapp.cache;

/**
 * Created by PRAVEENKUMAR on 6/5/2015.
 */
public interface TimeOutCache<K,V> {
    // get function for getting the object
    V get(K key);

    // put function
    void put(K key, V value);

    void put(K key, V value, int timeout);

    //remove function
    void remove (K key);

    //size of the object
    int size();
}
