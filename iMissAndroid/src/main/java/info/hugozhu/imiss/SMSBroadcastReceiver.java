package info.hugozhu.imiss;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hugozhu on 6/11/14.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
    final static String TAG = "iMiss";
    private IMissNotification notification = null;

    public SMSBroadcastReceiver(IMissNotification notification) {
        this.notification = notification;
    }


    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
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
                    notification.sendNotification(date, mobile, content);
                }
            }
        }
    }
}
