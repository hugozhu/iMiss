package info.hugozhu.imiss;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import com.umeng.analytics.MobclickAgent;
import info.hugozhu.imiss.ui.ApplicationLoader;
import info.hugozhu.imiss.util.GMailSender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hugozhu on 6/20/14.
 */
public class IMissService extends Service implements IMissNotification {
    final static String TAG = "iMiss";
    boolean started = false;
    SimpleDateFormat format = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "IMissService onCreate");
        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        smsFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(new SMSBroadcastReceiver(this), smsFilter);
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, false,
                new MissedCallContentObserver(this, new Handler(), this));
        format = new SimpleDateFormat(getResources().getString(R.string.fmt_phone_time));
        LogMessages.getInstance().add("Started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int r =  super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "IMissService started: "+intent+" "+r);

        if (!started) {
            started = true;
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "IMissService onDestroy");
        //restart service
        started = false;
        Intent service = new Intent(getApplicationContext(), IMissService.class);
        startService(service);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendNotification(Date date, String mobile, String content) {
        String subject = getResources().getString(R.string.notification_email_subject);
        String body    = getResources().getString(R.string.notification_email_body);
        final SharedPreferences preferences = ApplicationLoader.getMainConfig();
        if (preferences.getBoolean("enable_sms", false)) {
            SmsManager smsManager = SmsManager.getDefault();
            String targetPhone = preferences.getString("your_phone","");
            String tmp = String.format(body, format.format(date), mobile, content);
            if (tmp.length()>70) {
                tmp = tmp.substring(0,66)+"...";
            }
            smsManager.sendTextMessage(targetPhone, null,
                    tmp, null, null);
            Log.e(TAG, "SMS result....");
        }

        if (preferences.getBoolean("enable_email", false)) {
            new AsyncTask<String, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        Log.e(TAG, "forwarding via email");
                        new GMailSender(preferences.getString("gmail_username",""),
                                preferences.getString("gmail_password",""))
                                .sendMail(params[0], params[1], preferences.getString("gmail_username", ""), params[2]);
                        return true;
                    } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        Log.e(TAG, "failed to send email:" + sw.toString());
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    LogMessages.getInstance().add("Email result:"+result);
                    Log.e(TAG, "Email result:" + result);
                }
            }.execute(String.format(subject, getResources().getString(content==null?R.string.txt_phonecall:R.string.txt_sms) , mobile),
                    String.format(body, format.format(date), mobile, content),
                    preferences.getString("your_email", ""));
        }

        MobclickAgent.onEvent(this,"send notification");
    }
}
