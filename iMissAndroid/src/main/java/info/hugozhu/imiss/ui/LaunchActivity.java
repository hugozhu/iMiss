package info.hugozhu.imiss.ui;

import android.app.Activity;
import android.content.*;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import com.umeng.analytics.MobclickAgent;
import info.hugozhu.imiss.IMissService;
import info.hugozhu.imiss.R;
import info.hugozhu.imiss.ui.Views.BaseFragment;

/**
 * Created by hugozhu on 3/24/14.
 */
public class LaunchActivity extends ActionBarActivity {
    final static String TAG = "iMiss";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.Theme_iMiss);

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

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent service = new Intent(getApplicationContext(), IMissService.class);
        startService(service);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MobclickAgent.onPageStart("LaunchPage");
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LaunchPage");
        MobclickAgent.onPause(this);       //统计时长
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        SharedPreferences preferences = ApplicationLoader.getMainConfig();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onAboutItemClick(MenuItem item) {
//        Intent intent = new Intent(this, AboutActivity.class);
//        startActivity(intent);

        AboutActivity fragment = new AboutActivity();
        presentFragment(fragment,"about",false);

    }
}
