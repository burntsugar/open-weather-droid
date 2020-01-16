package com.rach.archexemplar.http;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.rach.archexemplar.utility.Logs;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by rachaelcolley on 31/07/2017.
 */

public class MyHttpClass extends AsyncTask<String, Integer, String> {

    private final String CID = getClass().getSimpleName();
    private String operationStatus = "The " + CID + " core looks beautiful";
    private final static int MIN_SERVICEURL_LENGTH = 10;
    private final int RESPONSE_SUCCESS_CODE = 200;
    private boolean success = true;
    private ProgressBar progressBar;

    public IAsyncTaskResponse delegate = null;

    /**
     * Provides networking for common web service requests without blocking the main UI thread.
     * Call the execute method with the fully formed url (including parameters) of the web service address you wish to hit.
     * Returns a String with the contents of the feed or null if the operation is unsuccessful.
     */
    public MyHttpClass() {
        Logs.logs(CID,"CommonHttpAsyncTask...");

    }

    /**
     * Provides networking for common web service requests without blocking the main UI thread.
     * Call the doInBackGround method with the web service address you wish to hit.
     * Returns a String with the contents of the feed or null if the operation is unsuccessful.
     * @param progressBar ProgressBar. The ProgressBar in the View to receive updates.
     */
    public MyHttpClass(ProgressBar progressBar) {
        Logs.logs(CID,"CommonHttpAsyncTask...");

        if (progressBar != null) {
            this.progressBar = progressBar;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressBar != null) {
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected String doInBackground(String... url) {
        Logs.logs(CID,".doInBackground starting...");

        if (url[0] == null || url[0].length() < MIN_SERVICEURL_LENGTH) {
            success = false;
            operationStatus = "serviceUrl is null or incorrect length";
            Logs.logs(CID, operationStatus);
            return null;
        }

        String serviceUrl = url[0];
        Logs.logs(CID, "serviceURL: " + serviceUrl);

        CommonHttpClient commonHttp = new CommonHttpClient();
        String jsonResponse = "";
        try {
            jsonResponse = commonHttp.getResponseFromService(serviceUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            success = false;
            operationStatus = "MalformedURLException has been thrown when "
                    + "attempting to get a response from the server.";
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
            operationStatus = "IOException has been thrown when attempting "
                    + "to get a response from the server.";
        } finally {
            if (!(commonHttp.getResponseCode() == RESPONSE_SUCCESS_CODE)) {
                success = false;
                operationStatus = "Unsuccessful request";
            }
            Logs.logs(CID,operationStatus);
        }

        Logs.logs(CID, "Success:", Boolean.toString(success), operationStatus);
        return jsonResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressBar != null) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.GONE);
        }
        delegate.processFinish(result);
    }

    public String getSuccessMessage() {
        return operationStatus;
    }

}

