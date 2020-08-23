package com.example.examcalendar.HelpClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import com.example.examcalendar.R;

/**
 * Class made to do things every activity do
 */


public class CommonActivityThings {

    /**
     * Method to paint the background of the param activity
     */
    public static void paintBackground(Activity act){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        final ViewGroup rootLayout = (ViewGroup) ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);

        int bgColor = preferences.getInt("bgColorActivity", Color.WHITE);

        //get if darkMode is active
        boolean dmActive = preferences.getBoolean("DarkModeActive", false);
        if(dmActive){
            bgColor = act.getApplicationContext().getResources().getColor(R.color.PDarkActivityBg);
        }


        rootLayout.setBackgroundColor(bgColor);
        //TODO pintar tambien los botones y eso
    }
}
