package com.example.idscomercial.servicefragment.network.constructor;

import android.net.NetworkInfo;

public interface DownloadView {
    NetworkInfo networkCheck();
    void showDialog();
    void progressUpdateFromFragment(int progressCode, String LOG_TAG);
}