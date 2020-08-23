package com.example.examcalendar.ConfigurationActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ConfigColorActivity extends Activity {
    private Button configAcceptButton, colorPickerExamButton, colorPickerHolidayButton, colorPickerNormalButton;
    private ImageButton palette1ImageButton, palette2ImageButton;
    private TextView customizeColorsTextView;
    private Switch darkModeSwitch;
    private int prevColorNormal, prevColorExam, prevColorHoliday, prevColorActivity; //used to save colors setted when the activity starts
    private final static int PALETTE_NUMBER_1 = 1, PALETTE_NUMBER_2 = 2, PALETTE_DARK = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_color);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        configAcceptButton = findViewById(R.id.ColorAct_ConfigAcceptButton);
        colorPickerExamButton = findViewById(R.id.ColorAct_ColorPickerExamButton);
        colorPickerNormalButton = findViewById(R.id.ColorAct_ColorPickerNormalButton);
        colorPickerHolidayButton = findViewById(R.id.ColorAct_ColorPickerHolidayButton);

        palette1ImageButton = findViewById(R.id.ColorAct_Palette1ImageButton);
        palette2ImageButton = findViewById(R.id.ColorAct_Palette2ImageButton);

        customizeColorsTextView = findViewById(R.id.ColorAct_CustomizeColorTextView);

        darkModeSwitch = findViewById(R.id.ColorAct_DarkModeSwitch);


        //Setting color from preferences
        final int colorExamBg = getColorFromPreferences(MonthDaySquare.EXAM);
        final int colorHolidayBg = getColorFromPreferences(MonthDaySquare.HOLIDAY);
        final int colorNormalBg = getColorFromPreferences(MonthDaySquare.NORMAL);
        final int colorActivityBg = getColorFromPreferences(10);

        prevColorExam = colorExamBg;
        prevColorHoliday = colorHolidayBg;
        prevColorNormal = colorNormalBg;
        prevColorActivity = colorActivityBg;

        colorPickerExamButton.setBackgroundColor(colorExamBg);
        colorPickerHolidayButton.setBackgroundColor(colorHolidayBg);
        colorPickerNormalButton.setBackgroundColor(colorNormalBg);

        boolean darkModeActivated = preferences.getBoolean("DarkModeActive", false);
        darkModeSwitch.setChecked(darkModeActivated);


        //Palette chooser buttons
        palette1ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapDarkMode(false);
                darkModeSwitch.setChecked(false);
                setColorFromPalette(1);
            }
        });
        palette2ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapDarkMode(false);
                darkModeSwitch.setChecked(false);
                setColorFromPalette(2);
            }
        });

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

        activateColorPicketButtons(!darkModeActivated); //Deactivate if dark mode is active

        //Accept button
        configAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfigColorActivity.this, ConfigurationActivityController.class);
                startActivity(i);
            }
        });

        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                swapDarkMode(b);
            }
        });

        //Paint bg Color
        CommonActivityThings.paintBackground(this);
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
            case 10: //Getting the background color for the activity
                color = preferences.getInt("bgColorActivity", 0xFFFFFF); //white if it doesn't exist
                break;
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

    @Override
    public void onBackPressed() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("bgColorNormalDay", prevColorNormal);
        editor.putInt("bgColorExamDay", prevColorExam);
        editor.putInt("bgColorHolidayDay", prevColorHoliday);
        editor.apply();
        startActivity(new Intent(this, ConfigurationActivityController.class));
    }

    /**
     * Method to change colors of the buttons from the palette views
     *
     * @param type
     */
    private void setColorFromPalette(int type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        int normalColor = prevColorNormal;
        int examColor = prevColorExam;
        int holidayColor = prevColorHoliday;
        int bgActivityColor = prevColorActivity;
        switch (type) {
            case PALETTE_NUMBER_1: //palette 1 chosen
                normalColor = getResources().getColor(R.color.P1NormalBg);
                examColor = getResources().getColor(R.color.P1ExamBg);
                holidayColor = getResources().getColor(R.color.P1HolidayBg);
                bgActivityColor = getResources().getColor(R.color.P1ActivityBg);
                break;
            case PALETTE_NUMBER_2:
                normalColor = getResources().getColor(R.color.P2NormalBg);
                examColor = getResources().getColor(R.color.P2ExamBg);
                holidayColor = getResources().getColor(R.color.P2HolidayBg);
                bgActivityColor = getResources().getColor(R.color.P2ActivityBg);
                break;
        }
        editor.putInt("bgColorNormalDay", normalColor);
        editor.putInt("bgColorExamDay", examColor);
        editor.putInt("bgColorHolidayDay", holidayColor);
        editor.putInt("bgColorActivity", bgActivityColor);
        //Repaint the buttons
        colorPickerNormalButton.setBackgroundColor(normalColor);
        colorPickerExamButton.setBackgroundColor(examColor);
        colorPickerHolidayButton.setBackgroundColor(holidayColor);
        editor.apply();
    }

    private void swapDarkMode(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ConfigColorActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("DarkModeActive", b);
        editor.apply();
        activateColorPicketButtons(!b);
        this.recreate();//repaint the activity
    }

    /**
     * Method used to activate or deactivate color picker buttons
     */
    private void activateColorPicketButtons(boolean b){
        int visibility = b ? View.VISIBLE : View.INVISIBLE;
        colorPickerNormalButton.setVisibility(visibility);
        colorPickerExamButton.setVisibility(visibility);
        colorPickerHolidayButton.setVisibility(visibility);
        customizeColorsTextView.setVisibility(visibility);
    }

}

