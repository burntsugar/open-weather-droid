package com.rach.archexemplar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rach.archexemplar.utility.Logs;

/**
 * Created by rachaelcolley on 20/04/2017.
 */
public class ExService extends Service {

    private final String CID = getClass().getSimpleName();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.logs(CID+" SERVICE STARTING");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
