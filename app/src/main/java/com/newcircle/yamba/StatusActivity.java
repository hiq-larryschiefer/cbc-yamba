package com.newcircle.yamba;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class StatusActivity extends Activity implements StatusFragment.OnFragmentInteractionListener {
    private static final String TAG = StatusActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);

        if (savedInstanceState == null) {
            StatusFragment frag = StatusFragment.newInstance(null, null);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            ft.add(R.id.frag_container, frag, frag.getClass().getName());
            ft.commit();
        }
    }


    @Override
    public void onStop() {
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
    public void onFragmentInteraction(String result) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
