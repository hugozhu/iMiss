package info.hugozhu.imiss.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import info.hugozhu.imiss.LogMessages;
import info.hugozhu.imiss.R;
import info.hugozhu.imiss.ui.Views.BaseFragment;
import info.hugozhu.imiss.ui.Views.OnSwipeTouchListener;

/**
 * Created by hugozhu on 3/25/14.
 */
public class SettingsActivity extends BaseFragment {
    final static String TAG = "iMiss";
    private ListView listView;
    private ListAdapter listAdapter;

    int rowCount;
    int generalSectionRow;
    int emailEnableRow;
    int emailRow;
    int logSectionRow;
    int logRow;

    TextView logs;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        rowCount = 0;
        generalSectionRow = rowCount++;
        emailEnableRow = rowCount++;
        emailRow       = rowCount++;
        logSectionRow  = rowCount++;
        logRow         = rowCount++;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    for (int i=0;i<10;i++) {
                        LogMessages.getInstance().add("hello");
                        if (logs!=null) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    logs.invalidate();
                                }
                            });
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                    if (position == emailEnableRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                        boolean send = preferences.getBoolean("enable_email", false);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("enable_email", !send);
                        editor.commit();
                        if (listView != null) {
                            Log.e(TAG, "invalidate views");
                            listView.invalidateViews();
                        }
                    }
                    if (position == emailRow) {
                        Log.e(TAG,"-------------- clicked emailRow");
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
            final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (i==emailEnableRow) {
                Log.e(TAG, "get email enable view");
                view = li.inflate(R.layout.settings_row_check_layout, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.settings_row_text);
                View divider = view.findViewById(R.id.settings_row_divider);
                ImageView checkButton = (ImageView) view.findViewById(R.id.settings_row_check_button);
                textView.setText(R.string.setting_email_notification);
                divider.setVisibility(View.VISIBLE);

                boolean enabled = preferences.getBoolean("enable_email", false);
                if (enabled) {
                    checkButton.setImageResource(R.drawable.btn_check_on);
                } else {
                    checkButton.setImageResource(R.drawable.btn_check_off);
                }
            }

            if (i==emailRow) {
                Log.e(TAG, "get email view");
                view = li.inflate(R.layout.settings_row_email_layout, viewGroup, false);
                final TextView textView = (TextView) view.findViewById(R.id.settings_email);
                View divider = view.findViewById(R.id.settings_row_divider);
                String email = preferences.getString("your_email", getResources().getString(R.string.your_email));
                textView.setText(email);
                textView.setSelectAllOnFocus(true);
                textView.setEnabled(preferences.getBoolean("enable_email", false));
                if (textView.isEnabled()) {
                    textView.setTextColor(Color.BLACK);
                } else {
                    textView.setTextColor(Color.GRAY);
                }
                textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Log.e(TAG,"email hasFocus: "+hasFocus);
                    }
                });
                textView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        SharedPreferences.Editor editor = preferences.edit();
                        String tmp = textView.getText().toString();
                        if (tmp.trim().length()==0 || tmp.equals(getResources().getString(R.string.your_email))) {
                            editor.remove("your_email");
                        } else {
                            editor.putString("your_email", tmp);
                        }
                        editor.commit();
                    }
                });
                divider.setVisibility(View.INVISIBLE);
            }


            if (i == logSectionRow) {
                view = li.inflate(R.layout.settings_section_layout, viewGroup, false);
                TextView textView = (TextView)view.findViewById(R.id.settings_section_text);
                textView.setText(getResources().getString(R.string.setting_section_logs));
            }

            if (i == generalSectionRow) {
                view = li.inflate(R.layout.settings_section_layout, viewGroup, false);
                TextView textView = (TextView)view.findViewById(R.id.settings_section_text);
                textView.setText(getResources().getString(R.string.setting_section_general));
            }

            if (i == logRow) {
                view = li.inflate(R.layout.settings_row_logs_layout, viewGroup, false);
                TextView textView = (TextView)view.findViewById(R.id.settings_logs);
                logs = textView;
                StringBuilder tmp = new StringBuilder();
                for (String s: LogMessages.getInstance().getMessages()) {
                    tmp.append(s);
                    tmp.append("\n");
                }
                textView.setText(tmp);
            }
            if (view == null) {
                Log.e(TAG,"view is empty");
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
