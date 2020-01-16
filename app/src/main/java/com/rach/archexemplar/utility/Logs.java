package com.rach.archexemplar.utility;

import android.util.Log;

/**
 * Created by rachaelcolley on 19/04/2017.
 */
public class Logs {

    public static void logs(String logText) {
        Log.d(" >>>", logText);
    }

    public static void logs(int logText) {
        logs(Integer.toString(logText));
    }

    public static void logs(String logText, int logIntText) {
        logs(logText + " " + Integer.toString(logIntText));
    }

    public static void logs(String logText1, String logText2, int logIntText) {
        logs(logText1 + " " + logText2 + " " + Integer.toString(logIntText));
    }

    public static void logs(String... logText) {
        String text = "";
        for (String s : logText) {
            text += " " + s;
        }
        logs(text);
    }

    public static void logs(int... logText) {
        String text = "";
        for (int i : logText) {
            text += "" + Integer.toString(i);
        }
        logs(text);
    }


}
