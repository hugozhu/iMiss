package info.hugozhu.imiss;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import info.hugozhu.imiss.ui.ApplicationLoader;
import info.hugozhu.imiss.util.GMailSender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hugozhu on 6/11/14.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
    final static String TAG = "iMiss";
    private IMissingHandler handler;

    public void register(IMissingHandler notification) {
        this.handler = notification;
    }

    public void unregister() {
        this.handler = null;
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras(); //---get the SMS message passed in---

            if (bundle!=null) {
                Object[] pduses = (Object[]) bundle.get("pdus");
                for (Object pdus : pduses) {
                    byte[] pdusmessage = (byte[]) pdus;
                    SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
                    String mobile = sms.getOriginatingAddress();//发送短信的手机号码
                    String content = sms.getMessageBody(); //短信内容
                    Date date = new Date(sms.getTimestampMillis());
                    LogMessages.getInstance().add(format.format(date)+" "+ mobile+" "+content);
                    if ( handler!=null ) {
                        handler.handleSMS(sms);
                    }

                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    if (preferences.getBoolean("enable_sms", false)) {
                        SmsManager smsManager = SmsManager.getDefault();
                        String targetPhone = preferences.getString("your_phone","13750866695");
                        smsManager.sendTextMessage(targetPhone, null, format.format(date)+" "+ mobile+" "+content, null, null);
                    }

                    if (preferences.getBoolean("enable_email", false)) {
                        new AsyncTask<String, Void, Boolean>(){
                            @Override
                            protected Boolean doInBackground(String... params) {
                                try {
                                    Log.d(TAG, "forwarding via email");
                                    new GMailSender("hugozhu@gmail.com","").sendMail(params[0], params[1],"hugozhu@gmail.com",params[2]);
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
                                Log.e(TAG, "Email result:" + result);
                            }
                        }.execute("You got a new message", format.format(date) + " " + mobile + " " + content, preferences.getString("your_email",""));
                    }
                }
            }
        }
    }
}
