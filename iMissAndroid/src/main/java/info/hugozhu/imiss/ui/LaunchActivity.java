package info.hugozhu.imiss.ui;

import android.app.Activity;
import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewGroup;
import info.hugozhu.imiss.R;
import info.hugozhu.imiss.SMSBroadcastReceiver;
import info.hugozhu.imiss.ui.Views.BaseFragment;

/**
 * Created by hugozhu on 3/24/14.
 */
public class LaunchActivity extends ActionBarActivity {
    final static String TAG = "iMiss";

    private ContentObserver newMmsContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            int mNewSmsCount = getNewSmsCount() + getNewMmsCount();
            Log.e(TAG,"missing msg:"+ mNewSmsCount+" missing calls:"+readMissCall());
        }
    };

    private void registerObserver() {
        unregisterObserver();
//        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true,
//                newMmsContentObserver);
        getContentResolver().registerContentObserver(Telephony.MmsSms.CONTENT_URI, true,
                newMmsContentObserver);
    }

    private synchronized void unregisterObserver() {
        try {
            if (newMmsContentObserver != null) {
                getContentResolver().unregisterContentObserver(newMmsContentObserver);
            }
        } catch (Exception e) {
            Log.e(TAG, "unregisterObserver fail");
        }
    }

    private int getNewSmsCount() {
        int result = 0;
        Cursor csr = getContentResolver().query(Uri.parse("content://sms"), null,
                "type = 1 and read = 0", null, null);
        if (csr != null) {
            result = csr.getCount();
            csr.close();
        }
        return result;
    }

    private int getNewMmsCount() {
        int result = 0;
        Cursor csr = getContentResolver().query(Uri.parse("content://mms/inbox"),
                null, "read = 0", null, null);
        if (csr != null) {
            result = csr.getCount();
            csr.close();
        }
        return result;
    }

    private int readMissCall() {
        int result = 0;
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] {
                CallLog.Calls.TYPE
        }, " type=? and new=?", new String[] {
                CallLog.Calls.MISSED_TYPE + "", "1"
        }, "date desc");

        if (cursor != null) {
            result = cursor.getCount();
            cursor.close();
        }

        return result;
    }


    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && "com.android.phone.NotificationMgr.MissedCall_intent".equals(action)) {
                int mMissCallCount = intent.getExtras().getInt("MissedCallNumber");
                Log.e(TAG, "missing calls:" + mMissCallCount);
            }
        }
    };

    SMSBroadcastReceiver smsReceiver = new SMSBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.Theme_iMiss);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.phone.NotificationMgr.MissedCall_intent");
        registerReceiver(receiver, filter);
        registerObserver();

        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        getWindow().setFormat(PixelFormat.RGB_565);
//        getSupportActionBar().setLogo(R.drawable.ab_icon_fixed2);

        for (BaseFragment fragment : ApplicationLoader.fragmentsStack) {
            if (fragment.fragmentView != null) {
                ViewGroup parent = (ViewGroup)fragment.fragmentView.getParent();
                if (parent != null) {
                    parent.removeView(fragment.fragmentView);
                }
                fragment.fragmentView = null;
            }
            fragment.parentActivity = this;
        }

        setContentView(R.layout.application_layout);

        SettingsActivity fragment = new SettingsActivity();
        fragment.onFragmentCreate();
        presentFragment(fragment,"settings",false);

        Log.e(TAG,"------register sms listener------");
        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        smsFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, smsFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterObserver();
        unregisterReceiver(smsReceiver);
    }


    public void removeFromStack(BaseFragment fragment) {
        ApplicationLoader.fragmentsStack.remove(fragment);
        fragment.onFragmentDestroy();
    }

    public void finishFragment(boolean bySwipe) {
        if (ApplicationLoader.fragmentsStack.size() < 2) {
            for (BaseFragment fragment : ApplicationLoader.fragmentsStack) {
                fragment.onFragmentDestroy();
            }
            ApplicationLoader.fragmentsStack.clear();
            SettingsActivity fragment = new SettingsActivity();
            fragment.onFragmentCreate();
            ApplicationLoader.fragmentsStack.add(fragment);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, "settings").commitAllowingStateLoss();
            return;
        }
        BaseFragment fragment = ApplicationLoader.fragmentsStack.get(ApplicationLoader.fragmentsStack.size() - 1);
        fragment.onFragmentDestroy();
        BaseFragment prev = ApplicationLoader.fragmentsStack.get(ApplicationLoader.fragmentsStack.size() - 2);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTrans = fm.beginTransaction();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean animations = preferences.getBoolean("view_animations", true);
        if (animations) {
            if (bySwipe) {
                fTrans.setCustomAnimations(R.anim.no_anim_show, R.anim.slide_right_away);
            } else {
                fTrans.setCustomAnimations(R.anim.no_anim_show, R.anim.scale_out);
            }
        }
        fTrans.replace(R.id.container, prev, prev.getTag());
        fTrans.commitAllowingStateLoss();
        ApplicationLoader.fragmentsStack.remove(ApplicationLoader.fragmentsStack.size() - 1);
    }

    public void presentFragment(BaseFragment fragment, String tag, boolean bySwipe) {
        presentFragment(fragment, tag, false, bySwipe);
    }

    public void presentFragment(BaseFragment fragment, String tag, boolean removeLast, boolean bySwipe) {
//        if (getCurrentFocus() != null) {
//            Utilities.hideKeyboard(getCurrentFocus());
//        }
        if (!fragment.onFragmentCreate()) {
            return;
        }
        BaseFragment current = null;
        if (!ApplicationLoader.fragmentsStack.isEmpty()) {
            current = ApplicationLoader.fragmentsStack.get(ApplicationLoader.fragmentsStack.size() - 1);
        }
        if (current != null) {
            current.willBeHidden();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTrans = fm.beginTransaction();
        if (removeLast && current != null) {
            ApplicationLoader.fragmentsStack.remove(ApplicationLoader.fragmentsStack.size() - 1);
            current.onFragmentDestroy();
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean animations = preferences.getBoolean("view_animations", true);
        if (animations) {
            if (bySwipe) {
                fTrans.setCustomAnimations(R.anim.slide_left, R.anim.no_anim);
            } else {
                fTrans.setCustomAnimations(R.anim.scale_in, R.anim.no_anim);
            }
        }
        try {
            fTrans.replace(R.id.container, fragment, tag);
            fTrans.commitAllowingStateLoss();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        ApplicationLoader.fragmentsStack.add(fragment);
    }

    @Override
    public void onBackPressed() {
        if (ApplicationLoader.fragmentsStack.size() == 1) {
            ApplicationLoader.fragmentsStack.get(0).onFragmentDestroy();
            ApplicationLoader.fragmentsStack.clear();
            finish();
            return;
        }
        if (!ApplicationLoader.fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = ApplicationLoader.fragmentsStack.get(ApplicationLoader.fragmentsStack.size() - 1);
            if (lastFragment.onBackPressed()) {
                finishFragment(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
        } catch (Exception e) {
        }
    }
}
