package com.newcircle.yamba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusActivity extends Activity implements View.OnClickListener, TextWatcher {
    private static final String TAG = StatusActivity.class.getName();
    private static final int    WARNING_STATUS_COUNT = 10;
    private static final int    ERROR_STATUS_COUNT = 0;

    private EditText            mStatus;
    private Button              mSubmit;
    private TextView            mEditStatus;
    private int                 mDfltColor;
    private PostTask            mCurPost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);
        mStatus = (EditText)findViewById(R.id.status);
        mStatus.addTextChangedListener(this);

        mSubmit = (Button)findViewById(R.id.btn_submit);
        mSubmit.setOnClickListener(this);

        mEditStatus = (TextView)findViewById(R.id.edit_status);
        mDfltColor = mEditStatus.getCurrentTextColor();
        mEditStatus.setText(Integer.toString(getResources().getInteger(R.integer.MAX_STATUS_COUNT)));
        Log.d(TAG, getString(R.string.app_name));
    }


    @Override
    public void onStop() {
        if (mCurPost != null) {
            mCurPost.cancel(true);
        }

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String status = mStatus.getText().toString();
        if ((status != null) && (status.length() > 0)) {
            mCurPost = new PostTask();
            mCurPost.execute(status);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //  Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //  Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        //  Determine how much is left to go
        int remaining = getResources().getInteger(R.integer.MAX_STATUS_COUNT) - s.length();

        if (remaining <= ERROR_STATUS_COUNT) {
            mEditStatus.setTextColor(Color.RED);
        } else if (remaining <= WARNING_STATUS_COUNT) {
            mEditStatus.setTextColor(Color.YELLOW);
        } else {
            mEditStatus.setTextColor(mDfltColor);
        }

        mEditStatus.setText(Integer.toString(remaining));
    }

    private final class PostTask extends AsyncTask<String, Void, String> {
        ProgressDialog   mProgDlg;

        @Override
        protected String doInBackground(String... params) {
            String status = params[0];
            String result;

            YambaClient client = new YambaClient("student", "password");
            try {
                client.postStatus(status);
            } catch (YambaClientException e) {
                e.printStackTrace();
                result = "Failed to post status";
                return result;
            }

            result = "Updated status: " + status;
            return result;
        }

        @Override
        protected void onPreExecute() {
            mProgDlg = ProgressDialog.show(StatusActivity.this, "Please wait", "Posting your status...", true);
            mProgDlg.setCancelable(true);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
            mProgDlg.dismiss();
            mStatus.setText(null);

            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();


        }

        @Override
        protected void onCancelled() {
            mProgDlg.dismiss();
            StatusActivity.this.mCurPost = null;
        }
    }
}
