package info.hugozhu.imiss.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import info.hugozhu.imiss.R;
import info.hugozhu.imiss.ui.Views.BaseFragment;
import info.hugozhu.imiss.ui.Views.OnSwipeTouchListener;

/**
 * Created by hugozhu on 3/25/14.
 */
public class SettingsActivity extends BaseFragment {
    private ListView listView;
    private ListAdapter listAdapter;

    int rowCount;
    int emailRow;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        rowCount = 0;
        emailRow = rowCount++;
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.settings_layout, container, false);
            listAdapter = new ListAdapter(parentActivity);
            listView = (ListView)fragmentView.findViewById(R.id.listView);
            Log.e(LaunchActivity.TAG, listView + "");
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    boolean send = preferences.getBoolean("enable_email", false);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("enable_email", !send);
                    editor.commit();
                    if (listView != null) {
                        listView.invalidateViews();
                    }
                }
            });
            listView.setOnTouchListener(new OnSwipeTouchListener() {
                public void onSwipeRight() {
                    finishFragment(true);
                }
            });
        } else {
            ViewGroup parent = (ViewGroup)fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }

    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.settings_row_check_layout, viewGroup, false);
            TextView textView = (TextView)view.findViewById(R.id.settings_row_text);
            View divider = view.findViewById(R.id.settings_row_divider);
            ImageView checkButton = (ImageView)view.findViewById(R.id.settings_row_check_button);
            textView.setText(R.string.setting_email_notification);
            divider.setVisibility(View.VISIBLE);

            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            boolean enabled = preferences.getBoolean("enable_email", false);
            if (enabled) {
                checkButton.setImageResource(R.drawable.btn_check_on);
            } else {
                checkButton.setImageResource(R.drawable.btn_check_off);
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
