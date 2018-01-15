package com.example.idscomercial.servicefragment.network.tasks;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.idscomercial.servicefragment.network.callbacks.DownloadCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class DownloadJsonTask extends AsyncTask<String, Integer, DownloadJsonTask.Result> {
    private static final String LOG_TAG = DownloadJsonTask.class.getSimpleName();

    private DownloadCallback<String> mCallback;
    private HashMap mHeaderValue;
    private String json;
    private String tag;

    DownloadJsonTask(DownloadCallback<String> callback, HashMap headerValue, String jsonObject, String tag) {
        setCallback(callback);
        setHeaders(headerValue);
        setJson(jsonObject);
        setTag(tag);
    }

    private void setCallback(DownloadCallback<String> callback) {
        this.mCallback = callback;
    }

    private void setHeaders(HashMap headerValue) {
        this.mHeaderValue = headerValue;
    }

    private void setJson(String json){
        this.json = json;
    }

    private void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the download
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    static class Result {
        private String mResultValue;
        private Exception mException;

        public Result(String mResultValue) {
            this.mResultValue = mResultValue;
        }
        public Result(Exception mException) {
            this.mException = mException;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mCallback.onProgressUpdate(values[0],1);
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();

            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {

                mCallback.updateFromDownload(null, tag);

                cancel(true);
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected DownloadJsonTask.Result doInBackground(String... urls) {
        Result result = null;

        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];

            try {
                URL url = new URL(urlString);

                String resultString = downloadUrl(url);

                if (resultString != null)
                    result = new Result(resultString);
                else
                    throw new IOException("No response received.");

            } catch(Exception e) {
                result = new Result(e);
            }
        }

        if (urls != null) {
            Log.d(LOG_TAG, "do in background url: " + urls[0]);
        }

        return result;
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    @Override
    protected void onPostExecute(Result result) {
        if (result != null && mCallback != null) {
            if (result.mException != null)
                mCallback.updateFromDownload(result.mException.getMessage(),tag);
            else if (result.mResultValue != null)
                mCallback.updateFromDownload(result.mResultValue, tag);

            mCallback.finishDownloading(tag);
        }
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);

            for (Object o : mHeaderValue.entrySet()) {
                HashMap.Entry e = (HashMap.Entry) o;
                String key = (String) e.getKey();
                String value = (String) e.getValue();

                connection.setRequestProperty(key, value);
            }

            OutputStream oStream = connection.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(oStream, "UTF-8"));

            Log.d(LOG_TAG, "JSON: " + json);

            writer.write(json);
            writer.flush();
            writer.close();

            oStream.close();

            connection.connect();

            publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);

            if (stream != null) {
                result = readStream(stream);
                publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS, 0);
            }
        } finally {
            if (stream != null)
                stream.close();

            if (connection != null)
                connection.disconnect();
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(stream,"utf-8"));
        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        br.close();

        publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS, 0);

        return sb.toString();
    }
}