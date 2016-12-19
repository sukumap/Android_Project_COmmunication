package mooc.vandy.weatherapp.cache;

import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import mooc.vandy.weatherapp.aidl.WeatherData;

/**
 * Created by PRAVEENKUMAR oWn 6/5/2015.
 */
public class TimeOutCacheImpl<K,V> implements TimeOutCache<K,V>
{
    private int mDefaultTimeout;
    private final int  CACHE_BUFFER = 10;
    private final String TAG = this.getClass().getSimpleName();
    private ConcurrentHashMap<K, WeatherData> mResults = new ConcurrentHashMap<>();
    private ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(1);
    /*class CacheValues {
        public V mValue;
        public ScheduledFuture<?> mFuture;

        public CacheValues(V value, ScheduledFuture<?> future){
            mValue = value;
            mFuture= future;
        }
    }*/

    public TimeOutCacheImpl(int timeout)
    {
        mDefaultTimeout = timeout;
    }

    private void putImpl(final K key,
                         V value,
                         int timeout)
    {
        Runnable cacheRemoverRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Running runnable to remove key from cache");
                remove(key);
            }
        };

        ScheduledFuture scheduledCacheRemovalHandler = mScheduledExecutorService.schedule(cacheRemoverRunnable,CACHE_BUFFER, TimeUnit.SECONDS);
        Log.i(TAG,"Scheduled the cache removal task");
        mResults.put(key,(WeatherData)value);

    }

    @Override
    public V get(K key) {
        Log.i(TAG, "Key received is  " +key);
        if (true == mResults.containsKey(key)) {
            return (V) mResults.get(key);
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        putImpl(key,value,mDefaultTimeout);
    }

    @Override
    public void put(K key, V value, int timeout) {
        putImpl(key,value,timeout);
    }

    @Override
    public void remove(K key) {
        Log.i(TAG,"Removing key ");
        mResults.remove(key);
    }

    @Override
    public int size() {
        return mResults.size();
    }


}
