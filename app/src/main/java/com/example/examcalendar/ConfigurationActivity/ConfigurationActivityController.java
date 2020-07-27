package com.example.examcalendar.ConfigurationActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import yuku.ambilwarna.AmbilWarnaDialog;


public class ConfigurationActivityController extends Activity {

    private Button configAcceptButton;
    private Button changeColorButton;

    //private TextView fontSizeExampleTextView, fontSizeNumberTextView;
    //private Button biggerFontSizeButton, smallerFontSizeButton;
    //private EditText colorEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        configAcceptButton = findViewById(R.id.ConfigAct_ConfigAcceptButton);

        changeColorButton = findViewById(R.id.ConfigAct_ChoosheColorButton);

        /*
        fontSizeNumberTextView = findViewById(R.id.ConfigAct_FontSizeNumberTextView);
        fontSizeExampleTextView = findViewById(R.id.ConfigAct_FontSizeExampleTextView);
        biggerFontSizeButton = findViewById(R.id.ConfigAct_BiggerFontButton);
        smallerFontSizeButton = findViewById(R.id.ConfigAct_SmallerFontButton);
        */

        /*
        //Set the font size in the example TextViews
        int fontSize = preferences.getInt("fontSizeExamTextView", R.integer.examTextSizeMonthGrid);
        fontSizeExampleTextView.setTextSize(fontSize);
        fontSizeExampleTextView.setText("Ejemplo: HOLA");
        fontSizeNumberTextView.setText(fontSize);
        Log.d("CONFIG:", "font size int = " + fontSize);
        */



        changeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfigurationActivityController.this, ConfigColorActivity.class);
                startActivity(i);
            }
        });

        /*
        //Font size buttons
        biggerFontSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fontSizeAux = Integer.parseInt(fontSizeNumberTextView.getText().toString());
                fontSizeAux++;
                fontSizeExampleTextView.setTextSize(fontSizeAux);
                fontSizeNumberTextView.setText(String.valueOf(fontSizeAux));
            }
        });
        smallerFontSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fontSizeAux = Integer.parseInt(fontSizeNumberTextView.getText().toString());
                fontSizeAux--;
                fontSizeExampleTextView.setTextSize(fontSizeAux);
                fontSizeNumberTextView.setText(String.valueOf(fontSizeAux));
            }
        });
         */
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
