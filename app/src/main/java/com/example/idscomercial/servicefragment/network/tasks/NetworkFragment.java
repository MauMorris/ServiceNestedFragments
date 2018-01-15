package com.example.idscomercial.servicefragment.network.tasks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.idscomercial.servicefragment.network.callbacks.DownloadCallback;
import com.example.idscomercial.servicefragment.network.headers.ConfigRequest;

import java.util.HashMap;

public class NetworkFragment extends Fragment {
    public static final String LOG_TAG = NetworkFragment.class.getSimpleName();

    public static final String TAG = "NetworkFragment";

    private DownloadCallback mCallback;
    private DownloadJsonTask mDownloadJsonTask;

    private static String mTag;
    private static String mKeyUrlString;

    private static final String mKeyHeader = "headers";
    private static final String jsonKey = "JSON";

    public static NetworkFragment getInstance(FragmentManager fragmentManager, ConfigRequest mConfigRequest, String json, String tag) {
        NetworkFragment networkFragment = (NetworkFragment) fragmentManager.findFragmentByTag(NetworkFragment.TAG);

        if (networkFragment == null) {
            networkFragment = new NetworkFragment();

            mKeyUrlString = mConfigRequest.getKeyUrl();
            mTag = tag;
            String urlValue = mConfigRequest.getKeyUrlValue();
            HashMap<String, String> headerValue = mConfigRequest.getHeader();

            Bundle args = new Bundle();

            args.putString(mKeyUrlString, urlValue);

            args.putSerializable(mKeyHeader, headerValue);
            args.putString(jsonKey, json);

            networkFragment.setArguments(args);

            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }

        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "create networkFragment");

        String url = getArguments().getString(mKeyUrlString);
        HashMap<String, String> header = (HashMap<String, String>) getArguments().getSerializable(mKeyHeader);
        String json = getArguments().getString(jsonKey);
        // Retain this Fragment across configuration changes in the host Activity.
        setRetainInstance(true);

        boolean dataNull = dataNull(url, header, json);
        if (!dataNull)
            startDownload(url, header, json);
    }

    /**
     * Start non-blocking execution of DownloadJsonTask.
     */
    public void startDownload(String mUrl, HashMap<String, String> mHeaderValue, String json) {
        Log.d(LOG_TAG, "start download from networkFragment");

        cancelDownload();
        //TODO: hacer que acepte array para considerar varios headers
        mDownloadJsonTask = new DownloadJsonTask(mCallback, mHeaderValue, json, mTag);
        mDownloadJsonTask.execute(mUrl);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadJsonTask execution.
     */
    public void cancelDownload() {
        if (mDownloadJsonTask != null) {
            mDownloadJsonTask.cancel(true);
            mDownloadJsonTask = null;
        }
    }

    public boolean dataNull(String url, HashMap<String, String> headerValue, String json) {
        return !(url != null && headerValue != null && json != null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        if (context instanceof DownloadCallback)
            mCallback = (DownloadCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        Log.d(LOG_TAG, "destroying networkFragment");

        super.onDestroy();
    }
}