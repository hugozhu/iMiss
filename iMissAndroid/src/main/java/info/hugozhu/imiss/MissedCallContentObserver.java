package info.hugozhu.imiss;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import info.hugozhu.imiss.ui.ApplicationLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hugozhu on 6/20/14.
 */
public class MissedCallContentObserver extends ContentObserver {
    final static String TAG = "iMiss";
    private Context ctx;
    long lastCall = 0;
    private IMissNotification notification = null;

    public MissedCallContentObserver(Context context, Handler handler, IMissNotification notification) {
        super(handler);
        ctx = context;
        final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        lastCall = preferences.getLong("last_call",0);
        this.notification = notification;
    }

    public void onChange(boolean selfChange) {
        Cursor csr = ctx.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] {CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE, CallLog.Calls.NEW, CallLog.Calls.DATE}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        if (csr != null) {
            if (csr.moveToFirst()) {
                int type = csr.getInt(csr.getColumnIndex(CallLog.Calls.TYPE));
                switch (type) {
                    case CallLog.Calls.MISSED_TYPE:
                        Log.v(TAG, "missed type");
                        if (csr.getInt(csr.getColumnIndex(CallLog.Calls.NEW)) == 1) {
                            String mobile = csr.getString(csr.getColumnIndex(CallLog.Calls.NUMBER));
                            String log = "You have a missed call from: "+mobile;
                            long timestamp = csr.getLong(csr.getColumnIndexOrThrow(CallLog.Calls.DATE));
                            if (timestamp > lastCall) {
                                lastCall = timestamp;
                                LogMessages.getInstance().add(log);
                                Log.e(TAG, log);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putLong("last_call", lastCall);
                                editor.commit();
                                notification.sendNotification(new Date(lastCall), mobile, null);
                            }
                        }
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        Log.e(TAG, "incoming type");
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        Log.e(TAG, "outgoing type");
                        break;
                }
            }
            // release resource
            csr.close();
        }
    }
}
