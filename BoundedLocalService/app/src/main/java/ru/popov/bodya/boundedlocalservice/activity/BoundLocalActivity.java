package ru.popov.bodya.boundedlocalservice.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.popov.bodya.boundedlocalservice.service.AsyncBoundLocalService;
import ru.popov.bodya.boundedlocalservice.service.BoundLocalService;
import ru.popov.bodya.boundedlocalservice.R;

public class BoundLocalActivity extends AppCompatActivity implements AsyncBoundLocalService.OperationListener {

    private static final int MAGIC_NUMBER = 9;

    private LocalServiceConnection mLocalServiceConnection = new LocalServiceConnection();
    private AsyncLocalServiceConnection mAsyncLocalServiceConnection = new AsyncLocalServiceConnection();

    private BoundLocalService mBoundLocalService;
    private AsyncBoundLocalService mAsyncBoundLocalService;
    private boolean mIsBound;
    private boolean mIsAsyncBound;

    private TextView mTextView;

    @Override
    public void onOperationDone(int i) {
        mTextView.setText(getString(R.string.async_operation_result, i));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService(new Intent(BoundLocalActivity.this, BoundLocalService.class), mLocalServiceConnection, Service.BIND_AUTO_CREATE);
        bindService(new Intent(BoundLocalActivity.this, AsyncBoundLocalService.class), mAsyncLocalServiceConnection, Service.BIND_AUTO_CREATE);
        mIsBound = true;
        mIsAsyncBound = true;
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            try {
                unbindService(mLocalServiceConnection);
                mIsBound = false;
            } catch (IllegalArgumentException e) {
                // Нет подключенной службы
            }
        }
        if (mIsAsyncBound) {
            try {
                mAsyncBoundLocalService.setListener(null);
                unbindService(mAsyncLocalServiceConnection);
                mIsAsyncBound = false;
            } catch (IllegalArgumentException e) {
                // Нет подключенной службы
            }
        }
    }

    private void initViews() {
        Button firstMethodButton = (Button) findViewById(R.id.first_method_button);
        Button secondMethodButton = (Button) findViewById(R.id.second_method_button);
        Button thirdMethodButton = (Button) findViewById(R.id.third_method_button);
        mTextView = (TextView) findViewById(R.id.text_view);

        firstMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoundLocalService != null) {
                    mBoundLocalService.publishedMethod1();
                }
            }
        });

        secondMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoundLocalService != null) {
                    mBoundLocalService.publishedMethod2();
                }
            }
        });

        thirdMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText(getString(R.string.third_method));
                if (mAsyncBoundLocalService != null) {
                    mAsyncBoundLocalService.doLongAsyncOperation(MAGIC_NUMBER, BoundLocalActivity.this);
                }
            }
        });
    }

    private class LocalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mBoundLocalService = ((BoundLocalService.ServiceBinder) iBinder).getService();
            mBoundLocalService.publishedMethod1();
            mBoundLocalService.publishedMethod2();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundLocalService = null;
        }
    }

    private class AsyncLocalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mAsyncBoundLocalService = ((AsyncBoundLocalService.AsyncServiceBinder) iBinder).getService();
            mAsyncBoundLocalService.setListener(BoundLocalActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAsyncBoundLocalService = null;
        }
    }
}
