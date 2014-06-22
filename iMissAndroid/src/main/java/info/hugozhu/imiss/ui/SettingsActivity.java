package info.hugozhu.imiss.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import info.hugozhu.imiss.IMissObserver;
import info.hugozhu.imiss.LogMessages;
import info.hugozhu.imiss.R;
import info.hugozhu.imiss.ui.Views.BaseFragment;
import info.hugozhu.imiss.ui.Views.OnSwipeTouchListener;

import java.util.List;

/**
 * Created by hugozhu on 3/25/14.
 */
public class SettingsActivity extends BaseFragment implements IMissObserver {
    final static String TAG = "iMiss";
    private ListView listView;
    private ListAdapter listAdapter;

    int rowCount;
    int generalSectionRow;
    int emailEnableRow;
    int emailRow;
    int gmailRow;
    int smsEnableRow;
    int smsRow;
    int logSectionRow;
    int logRow;

    TextView logs;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        rowCount = 0;
        generalSectionRow = rowCount++;
        emailEnableRow = rowCount++;
        emailRow       = rowCount++;
        gmailRow       = rowCount++;
        smsEnableRow   = rowCount++;
        smsRow         = rowCount++;
        logSectionRow  = rowCount++;
        logRow         = rowCount++;
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        LogMessages.getInstance().unregister();
        Log.e(TAG, "unregister iMissingHandler");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.e(TAG, "register iMissingHandler");
        LogMessages.getInstance().register(this);
    }

    @Override
    public void onMissing() {
        if (logs!=null) {
            List<String> messages = LogMessages.getInstance().getMessages();
            logs.append(messages.get(messages.size()-1));
            logs.append("\n");
            logs.postInvalidate();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.settings_layout, container, false);
            listAdapter = new ListAdapter(parentActivity);
            listView = (ListView)fragmentView.findViewById(R.id.listView);
            Log.e(LaunchActivity.TAG, listView + "");
            listView.setAdapter(listAdapter);
            final SharedPreferences preferences = ApplicationLoader.getMainConfig();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == emailEnableRow) {
                        boolean send = preferences.getBoolean("enable_email", false);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("enable_email", !send);
                        editor.commit();
                        if (listView != null) {
                            Log.e(TAG,"refresh views");
                            listView.invalidateViews();
                        }
                    }

                    if (position == smsEnableRow) {
                        boolean send = preferences.getBoolean("enable_sms", false);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("enable_sms", !send);
                        editor.commit();
                        if (listView != null) {
                            listView.invalidateViews();
                        }
                    }

                    if (position == gmailRow) {
                        boolean enable_email = preferences.getBoolean("enable_email", false);
                        if (enable_email) {
                            GMailDialogFragment dialog = new GMailDialogFragment(new Runnable() {
                                @Override
                                public void run() {
                                    if (listView != null) {
                                        listView.invalidateViews();
                                    }
                                }
                            });
                            dialog.show(getFragmentManager(), "gmail_dialog");
                        }
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
            final SharedPreferences preferences = ApplicationLoader.getMainConfig();
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (i==emailEnableRow) {
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

            if (i==smsEnableRow) {
                view = li.inflate(R.layout.settings_row_check_layout, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.settings_row_text);
                View divider = view.findViewById(R.id.settings_row_divider);
                ImageView checkButton = (ImageView) view.findViewById(R.id.settings_row_check_button);
                textView.setText(R.string.setting_sms_notification);
                divider.setVisibility(View.VISIBLE);

                boolean enabled = preferences.getBoolean("enable_sms", false);
                if (enabled) {
                    checkButton.setImageResource(R.drawable.btn_check_on);
                } else {
                    checkButton.setImageResource(R.drawable.btn_check_off);
                }
            }

            if (i==emailRow) {
                boolean enabled = preferences.getBoolean("enable_email", false);
                if (enabled) {
                    view = li.inflate(R.layout.settings_row_email_layout, viewGroup, false);
                    final TextView textView = (TextView) view.findViewById(R.id.settings_email);
                    View divider = view.findViewById(R.id.settings_row_divider);
                    String email = preferences.getString("your_email", "");
                    textView.setText(email);
                    textView.setSelectAllOnFocus(true);
                    textView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    textView.setEnabled(preferences.getBoolean("enable_email", false));
                    if (textView.isEnabled()) {
                        textView.setTextColor(Color.BLUE);
                    } else {
                        textView.setTextColor(Color.GRAY);
                    }
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
                            if (tmp.trim().length() == 0 || tmp.equals(getResources().getString(R.string.your_email))) {
                                editor.remove("your_email");
                            } else {
                                editor.putString("your_email", tmp);
                            }
                            editor.commit();
                        }
                    });
                    divider.setVisibility(View.INVISIBLE);
                } else {
                    view = null;
                }
            }

            if (i==gmailRow) {
                boolean enable_email = preferences.getBoolean("enable_email", false);
                if (enable_email) {
                    view = li.inflate(R.layout.settings_row_gmail_layout, viewGroup, false);
                    final TextView textView = (TextView) view.findViewById(R.id.settings_gmail);
                    textView.setText(preferences.getString("gmail_username", getResources().getString(R.string.txt_set)));
                } else {
                    view = null;
                }

            }

            if (i==smsRow) {
                boolean enabled = preferences.getBoolean("enable_sms", false);
                if (enabled) {
                    view = li.inflate(R.layout.settings_row_email_layout, viewGroup, false);
                    final TextView textView = (TextView) view.findViewById(R.id.settings_email);
                    View divider = view.findViewById(R.id.settings_row_divider);
                    String phone = preferences.getString("your_phone", "");
                    textView.setText(phone);
                    textView.setHint(getResources().getString(R.string.your_phone_number));
                    textView.setSelectAllOnFocus(true);
                    textView.setEnabled(preferences.getBoolean("enable_sms", false));
                    if (textView.isEnabled()) {
                        textView.setTextColor(Color.BLUE);
                    } else {
                        textView.setTextColor(Color.GRAY);
                    }
                    textView.setInputType(InputType.TYPE_CLASS_PHONE);
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
                            if (tmp.trim().length() == 0 || tmp.equals(getResources().getString(R.string.your_email))) {
                                editor.remove("your_phone");
                            } else {
                                editor.putString("your_phone", tmp);
                            }
                            editor.commit();
                        }
                    });
                    divider.setVisibility(View.INVISIBLE);
                } else {
                    view = null;
                }
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
                view = new View(getActivity());
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
