package info.hugozhu.imiss;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;
import info.hugozhu.imiss.ui.ApplicationLoader;

import java.util.Date;

/**
 * Created by hugozhu on 6/20/14.
 */
public class SMSService extends Service {
    final static String TAG = "iMiss";
    boolean started = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int r =  super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "SMSService started: "+intent+" "+r);

        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        smsFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(ApplicationLoader.Instance.getSMSBroadcastReceiver(), smsFilter);

        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, false,
                new MissedCallContentObserver(this,new Handler()));

        if (!started) {
            started = true;
            LogMessages.getInstance().add(new Date()+": Started");
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "SMSService onDestroy");
        Intent service = new Intent(getApplicationContext(), SMSService.class);
        startService(service);

        //unregisterReceiver(ApplicationLoader.Instance.getSMSBroadcastReceiver());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
