package info.hugozhu.imiss.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import info.hugozhu.imiss.R;
import info.hugozhu.imiss.ui.Views.BaseFragment;

/**
 * Created by hugozhu on 6/22/14.
 */
public class AboutActivity extends BaseFragment  {
    public final static String TAG = "iMiss";

    public View fragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.about_layout);
        Log.e(TAG,"AboutActivity onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG,"AboutActivity onCreateView");
        if (fragmentView==null) {
            fragmentView = inflater.inflate(R.layout.about_layout, container, false);
        }
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"AboutActivity onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"AboutActivity onResume");
    }
}
