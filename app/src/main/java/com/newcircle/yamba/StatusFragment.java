package com.newcircle.yamba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class StatusFragment extends Fragment implements  View.OnClickListener, TextWatcher {
    public static final String TAG = StatusFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final int    WARNING_STATUS_COUNT = 10;
    private static final int    ERROR_STATUS_COUNT = 0;

    private EditText            mStatus;
    private Button              mSubmit;
    private TextView            mEditStatus;
    private int                 mDfltColor;
    private PostTask            mCurPost = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStop() {
        if (mCurPost != null) {
            mCurPost.cancel(true);
        }

        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        mStatus = (EditText)root.findViewById(R.id.status);
        mStatus.addTextChangedListener(this);

        mSubmit = (Button)root.findViewById(R.id.btn_submit);
        mSubmit.setOnClickListener(this);

        mEditStatus = (TextView)root.findViewById(R.id.edit_status);
        mDfltColor = mEditStatus.getCurrentTextColor();
        mEditStatus.setText(Integer.toString(getResources().getInteger(R.integer.MAX_STATUS_COUNT)));
        Log.d(TAG, getString(R.string.app_name));
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String result);
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
        ProgressDialog mProgDlg;

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
            mProgDlg = ProgressDialog.show(getActivity(), "Please wait", "Posting your status...", true);
            mProgDlg.setCancelable(true);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
            mProgDlg.dismiss();
            mStatus.setText(null);
            StatusFragment.this.mListener.onFragmentInteraction(result);
        }

        @Override
        protected void onCancelled() {
            mProgDlg.dismiss();
            StatusFragment.this.mCurPost = null;
        }
    }
}
