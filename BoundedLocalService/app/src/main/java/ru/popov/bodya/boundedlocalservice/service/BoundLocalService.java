package ru.popov.bodya.boundedlocalservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BoundLocalService extends Service {

    private static final String TAG = BoundLocalService.class.getSimpleName();

    private final ServiceBinder mBinder = new ServiceBinder();

    public class ServiceBinder extends Binder {
        public BoundLocalService getService() {
            return BoundLocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void publishedMethod1() {
        Log.e(TAG, "publishedMethod1");
    }

    public void publishedMethod2() {
        Log.e(TAG, "publishedMethod2");
    }

}
