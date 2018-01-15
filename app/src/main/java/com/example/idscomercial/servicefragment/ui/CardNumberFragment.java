package com.example.idscomercial.servicefragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.idscomercial.servicefragment.network.constants.UrlConstants;
import com.example.idscomercial.servicefragment.network.headers.ConfigRequest;
import com.example.idscomercial.servicefragment.network.headers.HeaderValues;
import com.example.idscomercial.servicefragment.network.jsonrequest.ConsultaEnrollState;
import com.example.idscomercial.servicefragment.network.tasks.NetworkFragment;
import com.google.gson.Gson;

import java.util.HashMap;

public class CardNumberFragment extends Fragment{
    public static final String TAG = "CardNumberFragment";

    private static final String LOG_TAG = CardNumberFragment.class.getSimpleName();

    private NetworkFragment mNetworkFragment;

    private boolean mDownloading = false;

    private ConfigRequest mConfig;
    private ConsultaEnrollState eState;

    public static CardNumberFragment getInstance(FragmentManager fragmentManager) {
        CardNumberFragment cardFragment = (CardNumberFragment) fragmentManager.findFragmentByTag(CardNumberFragment.TAG);

        if (cardFragment == null) {
            cardFragment = new CardNumberFragment();

            fragmentManager.beginTransaction().add(cardFragment, TAG).addToBackStack(null).commit();
        }
        return cardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "create CardNumberFragment");
        setRetainInstance(true);

        startEnrollState();
    }

    private void startEnrollState() {
        if (!mDownloading) {
            mConfig = new ConfigRequest();

            HashMap<String, String> headers = new HashMap<>();

            headers.put(HeaderValues.CONTENT_TYPE, HeaderValues. APP_JSON);

            mConfig.setHeader(headers);
            mConfig.setUrl(UrlConstants.KEY_ENROLL_STATE, UrlConstants.ENROLL_STATE);

            eState = new ConsultaEnrollState();

            eState.setPhoneNumber("");
            eState.setCardNumber("");

            Gson newJson = new Gson();

            String jsonRequest = newJson.toJson(eState);

            mNetworkFragment = NetworkFragment.getInstance(getChildFragmentManager(), mConfig, jsonRequest, TAG);
            Log.d(LOG_TAG, "create instance of NetworkFragment");

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
//        mCallback = (DownloadCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "destroy CardNumberFragment");
    }
}