package com.example.idscomercial.servicefragment.network.impl;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.idscomercial.servicefragment.network.networkdialogs.CancelDialog;
import com.example.idscomercial.servicefragment.network.constructor.DownloadView;
import com.example.idscomercial.servicefragment.network.callbacks.DownloadCallback;

public class DownloadImpl implements DownloadView{

    private Activity mActivity;
    private FragmentManager fManager;

    public DownloadImpl(Activity mActivity, FragmentManager fManager) {
        this.mActivity = mActivity;
        this.fManager = fManager;
    }

    @Override
    public NetworkInfo networkCheck() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo;
    }

    @Override
    public void showDialog() {
        CancelDialog errorDialog = new CancelDialog();

        errorDialog.setCancelable(false);
        errorDialog.show(fManager, "");
    }

    @Override
    public void progressUpdateFromFragment(int progressCode, String LOG_TAG) {
        switch (progressCode) {
            // You can add UI behavior for progress updates here.
            case DownloadCallback.Progress.ERROR:
                Log.d(LOG_TAG, "error");
                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:
                Log.d(LOG_TAG, "connection success");
                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:
                Log.d(LOG_TAG, "get input stream success");
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Log.d(LOG_TAG, "process input stream progress");
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:
                Log.d(LOG_TAG, "process input stream success");
                break;
        }
    }
}