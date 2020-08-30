package com.example.examcalendar.ConfigurationActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import yuku.ambilwarna.AmbilWarnaDialog;


public class ConfigurationActivityController extends Activity {

    private Button configAcceptButton;
    private Button changeColorButton;
    private Switch monthGridColumnsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int nColumnsMonthGrid = preferences.getInt("nColumsMonthGrid", getResources().getInteger(R.integer.columnsOnDayGrid));

        configAcceptButton = findViewById(R.id.ConfigAct_ConfigAcceptButton);
        changeColorButton = findViewById(R.id.ConfigAct_ChoosheColorButton);
        monthGridColumnsSwitch = findViewById(R.id.ConfigAct_MonthGridColumnSwitch);

        if(nColumnsMonthGrid == 7){
            monthGridColumnsSwitch.setChecked(true);
        }else {
            monthGridColumnsSwitch.setChecked(false);
        }

        //Paint bg Color
        CommonActivityThings.paintBackground(this);

        changeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfigurationActivityController.this, ConfigColorActivity.class);
                startActivity(i);
            }
        });

        monthGridColumnsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences.Editor editor = preferences.edit();
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putInt("nColumsMonthGrid",7);
                }else{
                    editor.putInt("nColumsMonthGrid",5);
                }
                editor.apply();
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
