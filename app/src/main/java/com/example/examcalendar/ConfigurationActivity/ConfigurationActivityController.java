package com.example.examcalendar.ConfigurationActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import yuku.ambilwarna.AmbilWarnaDialog;


public class ConfigurationActivityController extends Activity {

    private Button configAcceptButton;
    private Button changeColorButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        configAcceptButton = findViewById(R.id.ConfigAct_ConfigAcceptButton);
        changeColorButton = findViewById(R.id.ConfigAct_ChoosheColorButton);

        //Paint bg Color
        CommonActivityThings.paintBackground(this);

        changeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfigurationActivityController.this, ConfigColorActivity.class);
                startActivity(i);
            }
        });

        //Accept button
        configAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SharedPreferences.Editor editor = preferences.edit();
                //int newFontSize = Integer.parseInt(fontSizeNumberTextView.getText().toString());
                //editor.putInt("fontSizeExamTextView", newFontSize); //Override the font size

                Intent i = new Intent(ConfigurationActivityController.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
    }

}
