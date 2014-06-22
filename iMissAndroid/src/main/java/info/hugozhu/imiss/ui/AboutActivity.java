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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView==null) {
            fragmentView = inflater.inflate(R.layout.about_layout, container, false);
        } else {
            ViewGroup parent = (ViewGroup)fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }
}
