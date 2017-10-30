package ru.popov.bodya.boundedlocalservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * @author Popov Bogdan
 */

public class AsyncBoundLocalService extends Service {

    private static final String TAG = AsyncBoundLocalService.class.getSimpleName();
    private static final int MAGIC_NUMBER = 5;

    private final AsyncServiceBinder mBinder = new AsyncServiceBinder();

    private WeakReference<OperationListener> mCallbackWeakReference;

    public interface OperationListener {
        void onOperationDone(int i);
    }

    public class AsyncServiceBinder extends Binder {
        public AsyncBoundLocalService getService() {
            return AsyncBoundLocalService.this;
        }
    }

    public void doLongAsyncOperation(final int i, final OperationListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = longOperation(i);
                postResultOnMainThread(result);
            }
        }).start();
    }

    public void setListener(OperationListener listener) {
        mCallbackWeakReference = new WeakReference<>(listener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void postResultOnMainThread(final int result) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OperationListener loaderCallback = mCallbackWeakReference.get();
                if (loaderCallback != null) {
                    loaderCallback.onOperationDone(result);
                }
            }
        });
    }

    private int longOperation(int i) {
        Log.e(TAG, "longOperation() method");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return MAGIC_NUMBER * i;
    }
}
