package info.hugozhu.imiss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by hugozhu on 6/20/14.
 */
public class BootBroadcastReceiver  extends BroadcastReceiver {
    final static String TAG = "iMiss";
    static boolean started = false;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "iMiss started via:"+intent.getAction());
        Intent service = new Intent(context, SMSService.class);
        context.startService(service);

        if (!started) {
            started = true;
//            Toast.makeText(context, "iMiss started", Toast.LENGTH_SHORT).show();
        }
    }
}