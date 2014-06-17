package info.hugozhu.imiss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

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
                }
            }
        }
    }
}
