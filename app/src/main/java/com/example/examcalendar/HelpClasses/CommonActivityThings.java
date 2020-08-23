package com.example.examcalendar.HelpClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.examcalendar.R;

/**
 * Class made to do things every activity do
 */


public class CommonActivityThings {

    /**
     * Method to paint the background of the param activity
     */
    public static void paintBackground(Activity act) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        //get if darkMode is active
        boolean dmActive = preferences.getBoolean("DarkModeActive", false);

        ViewGroup rootLayout = (ViewGroup) ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        int bgColor = preferences.getInt("bgColorActivity", Color.WHITE);
        if (dmActive) {
            bgColor = act.getApplicationContext().getResources().getColor(R.color.PDarkActivityBg);
        }
        rootLayout.setBackgroundColor(bgColor);

        paintTextViews(rootLayout, dmActive, act);
    }

    /**
     * Recursive method that gets a view, and search for TextViews on its childs to
     * change the text color
     *
     * @param parent
     */
    public static void paintTextViews(View parent, boolean dmActive, Activity act) {
        if (parent instanceof ViewGroup) {
            int childs = ((ViewGroup) parent).getChildCount();
            for (int i = 0; i < childs; i++) {
                paintTextViews(((ViewGroup) parent).getChildAt(i), dmActive, act);
            }
        }

        //Change color of textViews
        if (parent instanceof TextView) {

            //Change color of button background since button extends textView
            //Only on basic buttons
            if ((parent instanceof Button) && !(parent instanceof CompoundButton)) {
                if (dmActive) {
                    //((Button) parent).setBackgroundColor(act.getResources().getColor(R.color.PDarkButtonBg));
                    ((Button) parent).getBackground().setColorFilter(act.getResources().getColor(R.color.PDarkButtonBg), PorterDuff.Mode.MULTIPLY);
                }
            } else { //it's a text view
                if (dmActive) {
                    ((TextView) parent).setTextColor(Color.WHITE);
                } else {
                    ((TextView) parent).setTextColor(Color.BLACK);
                }
            }
        }

    }
}


