package com.example.idscomercial.servicefragment;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.idscomercial.servicefragment.network.callbacks.DownloadCallback;
import com.example.idscomercial.servicefragment.network.impl.DownloadImpl;
import com.example.idscomercial.servicefragment.network.jsonResponse.ResponseEnrollState;
import com.example.idscomercial.servicefragment.network.jsonResponse.ResponseGetState;
import com.example.idscomercial.servicefragment.ui.CardNumberFragment;
import com.example.idscomercial.servicefragment.ui.CvvNipFragment;
import com.example.idscomercial.servicefragment.ui.PhoneNumberFragment;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements DownloadCallback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ResponseGetState rState;
    private ResponseEnrollState eState;

    private DownloadImpl mPresenter;

    private PhoneNumberFragment pNumberFragment;
    private CardNumberFragment cardFragment;
    private CvvNipFragment cvvFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new DownloadImpl(this, getSupportFragmentManager());

        pNumberFragment = PhoneNumberFragment.getInstance(getSupportFragmentManager());
    }

    @Override
    public void updateFromDownload(Object result, String fragmentTag) {
        if (result == null) {
            Log.d(LOG_TAG, "error de red, no disponible");
            mPresenter.showDialog();
        } else
            //TODO: Falta la validacion donde result pertenece a la clase de Strings para pasar el valor
            Log.d(LOG_TAG, "result response: " + result.toString());

        if (result != null) {
            Gson gson;
            switch (fragmentTag) {
                case PhoneNumberFragment.TAG:
                    gson = new Gson();
                    rState = gson.fromJson(result.toString(), ResponseGetState.class);
                    Log.d(LOG_TAG, "class response get State: " + gson.toJson(rState));
                    break;
                case CardNumberFragment.TAG:
                    gson = new Gson();
                    eState = gson.fromJson(result.toString(), ResponseEnrollState.class);
                    Log.d(LOG_TAG, "class response enroll state: " + gson.toJson(eState));
                    break;
                case CvvNipFragment.TAG:
                    gson = new Gson();
                    break;
            }
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        return mPresenter.networkCheck();
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        mPresenter.progressUpdateFromFragment(progressCode, LOG_TAG);
    }

    @Override
    public void finishDownloading(String fragmentTag) {
        switch (fragmentTag) {
            case PhoneNumberFragment.TAG:
                pNumberFragment = PhoneNumberFragment.getInstance(getSupportFragmentManager());
                pNumberFragment.finishDownload(false);

            cardFragment = CardNumberFragment.getInstance(getSupportFragmentManager());
                break;
            case CardNumberFragment.TAG:
                cardFragment = CardNumberFragment.getInstance(getSupportFragmentManager());
                cardFragment.finishDownload(false);

//                cvvFragment = CvvNipFragment.getInstance(getSupportFragmentManager());
                break;
            case CvvNipFragment.TAG:
                cvvFragment = CvvNipFragment.getInstance(getSupportFragmentManager());
                cvvFragment.finishDownload(false);
                break;
        }
    }
}