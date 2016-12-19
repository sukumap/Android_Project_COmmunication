package mooc.vandy.weatherapp.utils;

/**
 * Created by PRAVEENKUMAR on 6/3/2015.
 */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class GenericServiceConnection <AIDLInterface extends
        android.os.IInterface>
        implements ServiceConnection {

    private static final String STUB = "Stub";
    private static final String AS_INTERFACE = "asInterface";
    private static final Class<?>[] AI_PARAMS = {IBinder.class};

    private AIDLInterface mInterface;

    private final Class<?> mStub;

    private final Method mAsInterface;

    public AIDLInterface getInterface() {
        return mInterface;
    }

    public GenericServiceConnection(final Class<AIDLInterface> aidl) {
        Class<?> stub = null;
        Method method = null;
        for (final Class<?> c : aidl.getDeclaredClasses()) {
            if (c.getSimpleName().equals(STUB)) {
                try {
                    stub = c;
                    method = stub.getMethod(AS_INTERFACE,
                            AI_PARAMS);
                    break;
                } catch (final NoSuchMethodException e) { // Should not be possible
                    e.printStackTrace();
                }
            }
        }
        mStub = stub;
        mAsInterface = method;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServiceConnected(ComponentName name,
                                   IBinder service) {
        Log.d("GenericServiceConnectio", "Connected to ComponentName " + name);
        try {
            mInterface = (AIDLInterface)mAsInterface.invoke(mStub,
                    new Object[]{service});
        } catch (IllegalArgumentException e) { // Should not be possible
            e.printStackTrace();
        } catch (IllegalAccessException e) { // Should not be possible
            e.printStackTrace();
        } catch (InvocationTargetException e) { // Should not be possible
            e.printStackTrace();
        }
    }

    public void onServiceDisconnected(ComponentName name) {
        mInterface = null;
    }
}
