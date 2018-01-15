package com.example.idscomercial.servicefragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.idscomercial.servicefragment.network.headers.ConfigRequest;
import com.example.idscomercial.servicefragment.network.headers.HeaderValues;
import com.example.idscomercial.servicefragment.network.jsonrequest.ConsultaGetState;
import com.example.idscomercial.servicefragment.network.tasks.NetworkFragment;
import com.example.idscomercial.servicefragment.network.constants.UrlConstants;
import com.google.gson.Gson;

import java.util.HashMap;

public class PhoneNumberFragment extends Fragment {
    private static final String LOG_TAG = PhoneNumberFragment.class.getSimpleName();

    public static final String TAG = "PhoneNumberFragment";
    private boolean mDownloading = false;

    private NetworkFragment mNetworkFragment;
    private ConfigRequest mConfig;
    private ConsultaGetState gState;

    public static PhoneNumberFragment getInstance(FragmentManager fragmentManager) {
        PhoneNumberFragment phoneFragment = (PhoneNumberFragment) fragmentManager.findFragmentByTag(PhoneNumberFragment.TAG);

        if (phoneFragment == null) {
            phoneFragment = new PhoneNumberFragment();

            fragmentManager.beginTransaction().add(phoneFragment, TAG).addToBackStack(null).commit();
        }

        return phoneFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "create PhoneNumberFragment");
        setRetainInstance(true);

        startGetState();
    }

    private void startGetState() {
        if (!mDownloading) {
            mConfig = new ConfigRequest();

            HashMap <String, String> headers = new HashMap<>();

            headers.put(HeaderValues.CONTENT_TYPE, HeaderValues. APP_JSON);

            mConfig.setHeader(headers);
            mConfig.setUrl(UrlConstants.KEY_GET_STATE, UrlConstants.GET_STATE);

            gState = new ConsultaGetState();
            gState.setPhoneNumber("");

            Gson newJson = new Gson();
            String jsonRequest = newJson.toJson(gState);

            mNetworkFragment = NetworkFragment.getInstance(getChildFragmentManager(), mConfig, jsonRequest, TAG);
            Log.d(LOG_TAG, "create instance of NetworkFragment from: ");

            if (mNetworkFragment != null)
                mDownloading = true;
        }
    }

    public void finishDownload(boolean downloadState){
        mNetworkFragment = (NetworkFragment) getChildFragmentManager().findFragmentByTag(NetworkFragment.TAG);

        if (mNetworkFragment != null) {
            mDownloading = downloadState;
            Log.d(LOG_TAG, "order to destroy NetworkFragment");
            getChildFragmentManager().beginTransaction().remove(mNetworkFragment).commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "destroy PhoneNumberFragment");
    }
}