package info.hugozhu.imiss;

import android.telephony.SmsMessage;

import java.util.Date;

/**
 * Created by hugozhu on 6/17/14.
 */
public interface IMissingHandler {
    public void handleSMS(SmsMessage sms);
}
