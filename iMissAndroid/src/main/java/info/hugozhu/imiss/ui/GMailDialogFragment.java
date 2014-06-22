package info.hugozhu.imiss.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import info.hugozhu.imiss.R;

/**
 * Created by hugozhu on 6/20/14.
 */
public class GMailDialogFragment extends DialogFragment {
    String username;
    String password;
    Runnable callback;

    public GMailDialogFragment() {

    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.your_gmail));
        builder.setIcon(R.drawable.ic_launcher);
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.gmail_account_dialog, null);
        builder.setView(view);
        final EditText t_username = (EditText)view.findViewById(R.id.gmail_username);
        final EditText t_password = (EditText)view.findViewById(R.id.gmail_password);

        t_username.setText(preferences.getString("gmail_username", ""));
        t_password.setText(preferences.getString("gmail_password",""));

        builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                username = t_username.getText().toString().trim();
                password = t_password.getText().toString().trim();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("gmail_username", username);
                editor.putString("gmail_password", password);
                editor.commit();
                if (callback!=null)
                    callback.run();
            }
        });

        builder.setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }
}
