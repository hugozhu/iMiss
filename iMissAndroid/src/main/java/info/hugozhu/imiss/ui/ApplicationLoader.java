package info.hugozhu.imiss.ui;

import android.app.Application;
import android.content.Context;
import info.hugozhu.imiss.SMSBroadcastReceiver;
import info.hugozhu.imiss.ui.Views.BaseFragment;

import java.util.ArrayList;
import java.util.Locale;


public class ApplicationLoader extends Application {
    public static Context applicationContext;
    private Locale currentLocale;
    public static ApplicationLoader Instance = null;

    public static ArrayList<BaseFragment> fragmentsStack = new ArrayList<BaseFragment>();
    private static volatile boolean applicationInited = false;

    private SMSBroadcastReceiver smsReceiver = null;


    public void onCreate() {
        super.onCreate();
        currentLocale = Locale.getDefault();
        Instance = this;

        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");

        applicationContext = getApplicationContext();
        smsReceiver = new SMSBroadcastReceiver(this.getResources());
    }

    public SMSBroadcastReceiver getSMSBroadcastReceiver() {
        return smsReceiver;
    }

    public static void postInitApplication() {
        if (applicationInited) {
            return;
        }

        applicationInited = true;
    }

}