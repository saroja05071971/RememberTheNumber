package com.wonderwoman.rememberthenumber;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class OptionRestoreHandlerThread extends HandlerThread {
    private static final String TAG = CustomHandlerThread.class.getSimpleName();
    public Handler mHandle;

    public OptionRestoreHandlerThread(String name) {
        super(name);
    }
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandle = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"Thread Id is "+Thread.currentThread().getId());
            }
        };
    }
}
