package com.example.examcalendar.ConfigurationActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import yuku.ambilwarna.AmbilWarnaDialog;


public class ConfigurationActivityController extends Activity {

    private Button configAcceptButton, colorPickerExamButton, colorPickerHolidayButton, colorPickerNormalButton;
    //private EditText colorEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);


        configAcceptButton = findViewById(R.id.configAcceptButton);
        colorPickerExamButton = findViewById(R.id.colorPickerExamButton);
        colorPickerNormalButton = findViewById(R.id.colorPickerNormalButton);
        colorPickerHolidayButton = findViewById(R.id.colorPickerHolidayButton);

        //final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int colorExamBg = getColorFromPreferences(MonthDaySquare.EXAM);
        final int colorHolidayBg = getColorFromPreferences(MonthDaySquare.HOLIDAY);
        final int colorNormalBg = getColorFromPreferences(MonthDaySquare.NORMAL);

        colorPickerExamButton.setBackgroundColor(colorExamBg);
        colorPickerHolidayButton.setBackgroundColor(colorHolidayBg);
        colorPickerNormalButton.setBackgroundColor(colorNormalBg);


        //Color chooser buttons
        colorPickerExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker(colorExamBg, MonthDaySquare.EXAM);
            }
        });
        colorPickerHolidayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker(colorHolidayBg, MonthDaySquare.HOLIDAY);
            }
        });
        colorPickerNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker(colorNormalBg, MonthDaySquare.NORMAL);
            }
        });

        //Accept button
        configAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfigurationActivityController.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void openColorPicker(final int defColor, final int type) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            int newColor = defColor;

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationActivityController.this);
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
