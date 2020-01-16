package com.rach.archexemplar.http;

import android.widget.ProgressBar;

import com.rach.archexemplar.utility.Logs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rachaelcolley on 19/04/2017.
 */
public class CommonHttpClient {

    private final String CID = getClass().getSimpleName();
    private String operationStatus = "The " + CID + " core looks beautiful";
    private int responseCode = -1;
    private int contentLength = -1;
    private String contentEncoding = "none";
    private final static int MIN_SERVICEURL_LENGTH = 10;

    public String getResponseFromService(String serviceUrl) throws MalformedURLException, IOException {
        Logs.logs(CID,".getResponseFromService...");
        return getResponseFromService(serviceUrl,null);
    }

    public String getResponseFromService(String serviceUrl, ProgressBar progressBar) throws MalformedURLException, IOException {
        Logs.logs(CID,".getResponseFromService...");

        if (serviceUrl == null || serviceUrl.length() < MIN_SERVICEURL_LENGTH) {
            operationStatus = "ServiceUrl was empty or too short";
            Logs.logs(CID, operationStatus);
            return null;
        }

        URL url = null;
        URLConnection urlConnection = null;
        InputStream in = null;
        StringBuilder jsonResponse = null;

        try {
            url = new URL(serviceUrl);
            urlConnection = url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            operationStatus += "\n Opened connection to url";
        } catch (MalformedURLException e) {
            operationStatus = "MalformedURLException has been thrown when attempting to parse provided serviceUrl";
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            operationStatus = "IOException has been thrown when attempting to open urlConnection";
            e.printStackTrace();
            throw e;
        } finally {
            Logs.logs(CID,operationStatus);
        }

        /**
         * TODO close url connection.
         */

        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        jsonResponse = new StringBuilder();
        String bufferString = "";

        try {
            while ((bufferString = buffer.readLine()) != null) {
                jsonResponse.append(bufferString);
            }
            operationStatus += "\nFinished reading the buffer";
        } catch (IOException e) {
            operationStatus = "IOException has been thrown when attempting read input stream";
            e.printStackTrace();
            throw e;
        } finally {
            in.close();
            Logs.logs(CID, operationStatus);
        }

        HttpURLConnection htuc = (HttpURLConnection) urlConnection;
        responseCode = htuc.getResponseCode();
        Logs.logs(CID, "responseCode", responseCode);

        if (urlConnection != null) {
            contentLength = urlConnection.getContentLength();
            contentEncoding = urlConnection.getContentEncoding();
            Logs.logs(CID, "urlConnection contentLength", getContentLength());
            Logs.logs(CID, "urlConnection contentEncoding",getContentEncoding());
        }

        Logs.logs(CID, "response", jsonResponse.toString());
        return jsonResponse.toString();
    }

    /**
     * @return the responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @return the contentLength
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * @return the contentEncoding
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * @return the debugAdvice
     */
    public String getOperationStatus() {
        return operationStatus;
    }


}

