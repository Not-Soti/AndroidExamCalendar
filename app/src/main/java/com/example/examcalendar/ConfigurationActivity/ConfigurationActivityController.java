package com.example.examcalendar.ConfigurationActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import yuku.ambilwarna.AmbilWarnaDialog;


public class ConfigurationActivityController extends Activity {

    private Button configAcceptButton, colorPickerExamButton, colorPickerHolidayButton, colorPickerNormalButton;

    //private TextView fontSizeExampleTextView, fontSizeNumberTextView;
    //private Button biggerFontSizeButton, smallerFontSizeButton;
    //private EditText colorEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        configAcceptButton = findViewById(R.id.ConfigAct_ConfigAcceptButton);
        colorPickerExamButton = findViewById(R.id.ConfigAct_ColorPickerExamButton);
        colorPickerNormalButton = findViewById(R.id.ConfigAct_ColorPickerNormalButton);
        colorPickerHolidayButton = findViewById(R.id.ConfigAct_ColorPickerHolidayButton);

        /*
        fontSizeNumberTextView = findViewById(R.id.ConfigAct_FontSizeNumberTextView);
        fontSizeExampleTextView = findViewById(R.id.ConfigAct_FontSizeExampleTextView);
        biggerFontSizeButton = findViewById(R.id.ConfigAct_BiggerFontButton);
        smallerFontSizeButton = findViewById(R.id.ConfigAct_SmallerFontButton);
        */

        final int colorExamBg = getColorFromPreferences(MonthDaySquare.EXAM);
        final int colorHolidayBg = getColorFromPreferences(MonthDaySquare.HOLIDAY);
        final int colorNormalBg = getColorFromPreferences(MonthDaySquare.NORMAL);

        colorPickerExamButton.setBackgroundColor(colorExamBg);
        colorPickerHolidayButton.setBackgroundColor(colorHolidayBg);
        colorPickerNormalButton.setBackgroundColor(colorNormalBg);


        /*
        //Set the font size in the example TextViews
        int fontSize = preferences.getInt("fontSizeExamTextView", R.integer.examTextSizeMonthGrid);
        fontSizeExampleTextView.setTextSize(fontSize);
        fontSizeExampleTextView.setText("Ejemplo: HOLA");
        fontSizeNumberTextView.setText(fontSize);
        Log.d("CONFIG:", "font size int = " + fontSize);
        */

        //Color chooser buttons
        colorPickerExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker(colorExamBg, MonthDaySquare.EXAM, preferences);
            }
        });
        colorPickerHolidayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker(colorHolidayBg, MonthDaySquare.HOLIDAY, preferences);
            }
        });
        colorPickerNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker(colorNormalBg, MonthDaySquare.NORMAL, preferences);
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

    private void openColorPicker(final int defColor, final int type, final SharedPreferences preferences) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            int newColor = defColor;

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                //final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationActivityController.this);
                SharedPreferences.Editor editor = preferences.edit();
                newColor = color;
                switch (type) {
                    case MonthDaySquare.EXAM:
                        editor.putInt("bgColorExamDay", newColor);
                        editor.apply();
                        colorPickerExamButton.setBackgroundColor(getColorFromPreferences(MonthDaySquare.EXAM)); //Repaint the button
                        break;
                    case MonthDaySquare.HOLIDAY:
                        editor.putInt("bgColorHolidayDay", newColor);
                        editor.apply();
                        colorPickerHolidayButton.setBackgroundColor(getColorFromPreferences(MonthDaySquare.HOLIDAY));
                        break;
                    case MonthDaySquare.NORMAL:
                        editor.putInt("bgColorNormalDay", newColor);
                        editor.apply();
                        colorPickerNormalButton.setBackgroundColor(getColorFromPreferences(MonthDaySquare.NORMAL));
                        break;
                }
                //editor.apply();
            }
        });
        colorPicker.show();
    }

    /**
     * Method to obtain a color for the day grid from the preferences
     *
     * @param type type of the day: Normal, exam or holiday
     */
    private int getColorFromPreferences(int type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int color = 0;
        switch (type) {
            case MonthDaySquare.NORMAL:
                color = preferences.getInt("bgColorNormalDay", getResources().getColor(R.color.NormalBg));
                break;
            case MonthDaySquare.EXAM:
                color = preferences.getInt("bgColorExamDay", getResources().getColor(R.color.ExamBg));
                break;
            case MonthDaySquare.HOLIDAY:
                color = preferences.getInt("bgColorHolidayDay", getResources().getColor(R.color.HolidayBg));
                break;
        }
        return color;
    }
}
