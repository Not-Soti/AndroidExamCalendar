package com.example.examcalendar.MonthActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.example.examcalendar.DialogsCRUDExams.DialogAddExam;
import com.example.examcalendar.DialogsCRUDHolidays.ActivityAddHoliday;
import com.example.examcalendar.DialogsCRUDHolidays.ActivityDelHoliday;
import com.example.examcalendar.DialogsCRUDExams.DialogDeleteExam;
import com.example.examcalendar.DialogsCRUDExams.DialogEditExam;
import com.example.examcalendar.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

/**
 * Class that draws a cell of the MonthActivity grid and its components
 */

public class MonthDaySquare extends LinearLayout {

    private Context context;
    private LinearLayout examNameLinearLayout;
    private TextView dayTextView;
    private ArrayList<String> examListStr;
    private String dayStr; //Strings to set on the view after inflating
    private int type; //Tells if a particular day is normal, a exam one or a holiday one
    private int month, year; //needed to start new activities from the menu popup
    public static final int NORMAL = 0;
    public static final int EXAM = 1;
    public static final int HOLIDAY = 2;

    private boolean isToday;
    private boolean isCurrentMonth; //To check if the day displayed is the main displayed month

    public MonthDaySquare(Context context) {
        super(context);
        this.context = context;
        initializeViews(context);
    }

    public MonthDaySquare(Context context, ArrayList<String> examStr, String dayStr, int type, int month, int year, boolean isToday, boolean isCurrentMonth) {
        super(context);
        this.context = context;
        this.examListStr = examStr;
        this.dayStr = dayStr;
        this.type = type;
        this.month = month;
        this.year = year;
        this.isToday = isToday;
        this.isCurrentMonth = isCurrentMonth;

        initializeViews(context);
    }

    public MonthDaySquare(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeViews(context);
    }

    public MonthDaySquare(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initializeViews(context);
    }

    /**
     * Inflates the view in the layout
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_square, this);



        examNameLinearLayout = this.findViewById(R.id.LinearLayoutExamNames);
        dayTextView = this.findViewById(R.id.DayText);

        //adding exams to the linear layout
        for(String exam : examListStr){
            //TextView examView = new TextView(context);
            AutofitTextView examView = new AutofitTextView(context); //Class from the maven repo

            /*TODO Arreglar que se pueda cambiar el tamaño de la letra.
                Entiendo que lo que pasa es que como son pixeles escalados, al hacer el setTextSize
                aquí le pregunta a la auto grid view la medida, y ésta le pregunta al propio textView la medida,
                creandose un bucle infinito de preguntarse. Vamos, digo yo.
             */
            //Getting the text size from the preferences
            examView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            examView.setMaxTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            //examView.setTextSize(TypedValue.COMPLEX_UNIT_PX, examTextSize);
            //examView.setMaxTextSize(TypedValue.COMPLEX_UNIT_PX, examTextSize);


            int minTextSize = Math.round(24*75/100); //75% of the value
            //int minTextSize = 12;
            examView.setMinTextSize(minTextSize);

            examView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            examView.setMaxLines(2);
            examView.setText(exam);

            if(!isCurrentMonth){
                examView.setTextColor((0x00000000 & 0x00FFFFFF) |0x40000000);
            }

            examNameLinearLayout.addView(examView);
        }

        dayTextView.setText(dayStr);

        //Sets the background as defined by /res/drawable/month_week_square
        this.setBackgroundResource(checkTypeForBackground(this.type));

        //Getting de BG color from the user settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int bgColor = 0xFFFFFF;

        boolean dmActive = preferences.getBoolean("DarkModeActive", false); //checks if dark mode is active

        if(type == MonthDaySquare.EXAM) {
            if(dmActive){
                //bgColor = preferences.getInt("bgColorExamDayDark", getResources().getColor(R.color.ExamBg));
                bgColor = getResources().getColor(R.color.PDarkExamBg);
            }else {
                bgColor = preferences.getInt("bgColorExamDay", getResources().getColor(R.color.ExamBg));
            }
        }else if(type == MonthDaySquare.HOLIDAY){
            if(dmActive){
                //bgColor = preferences.getInt("bgColorHolidayDayDark", getResources().getColor(R.color.HolidayBg));
                bgColor = getResources().getColor(R.color.PDarkHolidayBg);
            }else {
                bgColor = preferences.getInt("bgColorHolidayDay", getResources().getColor(R.color.HolidayBg));
            }
        }else if(type == MonthDaySquare.NORMAL){
            if(dmActive){
                //bgColor = preferences.getInt("bgColorNormalDayDark", getResources().getColor(R.color.NormalBg));
                bgColor = getResources().getColor(R.color.PDarkNormalBg);
            }else {
                bgColor = preferences.getInt("bgColorNormalDay", getResources().getColor(R.color.NormalBg));
            }
        }

        //set dayTextView general color
        if(dmActive){
            dayTextView.setTextColor(getResources().getColor(R.color.PDarkTodayDay));
        }else {
            dayTextView.setTextColor(getResources().getColor(R.color.todayDay));
        }

        //Make today color more intense
        if(isToday){
            float[] colorHSL = new float[3];
            ColorUtils.colorToHSL(bgColor, colorHSL);
            colorHSL[2] *= 0.8f; //adding brightness to the color
            bgColor = ColorUtils.HSLToColor(colorHSL);
            dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.BOLD);
            if(dmActive){
                dayTextView.setTextColor(getResources().getColor(R.color.PDarkTodayDay));
            }else {
                dayTextView.setTextColor(getResources().getColor(R.color.todayDay));
            }
        }
        if(!isCurrentMonth){
            //Make the color more transparent from the normal color
            if(dmActive){
                bgColor = getResources().getColor(R.color.PDarkNormalBg);
            }else {
                bgColor = preferences.getInt("bgColorNormalDay", getResources().getColor(R.color.NormalBg));
            }
            bgColor = (bgColor & 0x00FFFFFF) |0x40000000; //First byte is the transparecy, using 25% of current
            int dayColor;
            if(dmActive){
                dayColor = getResources().getColor(R.color.PDarkTodayDay);
            }else{
                dayColor = getResources().getColor(R.color.todayDay);
            }
            dayColor = (dayColor & 0x00FFFFFF) |0x40000000;
            dayTextView.setTextColor(dayColor);
        }
        ((GradientDrawable) this.getBackground()).setColor(bgColor);


        //Setting click listeners for the view
        final Context auxContext = context;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenuFunctionallity(auxContext, view);
            }
        });
    }

    /**
     * Method to set the same click listener
     * on this view and on the list
     */
    private void setMenuFunctionallity(final Context auxContext, View view) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(auxContext, view);
        //Inflate the popup xml file
        popup.getMenuInflater().inflate(R.menu.month_cell_popup_menu, popup.getMenu());

        //registering actions on clicking the menu items
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.addExamPopupMenu:
                        i = new Intent(auxContext, DialogAddExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.editExamPopupMenu:
                        i = new Intent(auxContext, DialogEditExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.deleteExamPopupMenu:
                        i = new Intent(auxContext, DialogDeleteExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.addHolidaysPopupMenu:
                        //i = new Intent(auxContext, DialogAddHolidays.class);
                        i = new Intent(auxContext, ActivityAddHoliday.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.deleteHolidaysPopupMenu:
                        //i = new Intent(auxContext, DialogDeleteHolidays.class);
                        i = new Intent(auxContext, ActivityDelHoliday.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    /**
     * Checks the type of the day and returns the correct background to set
     */
    private int checkTypeForBackground(int i) {
        int ret = 0;
        switch (i) {
            case MonthDaySquare.NORMAL:
                ret = R.drawable.month_day_square_normal;
                break;
            case MonthDaySquare.EXAM:
                ret = R.drawable.month_day_square_exam;
                break;
            case MonthDaySquare.HOLIDAY:
                ret = R.drawable.month_day_square_holiday;
                break;
        }
        return ret;
    }



    public TextView getDayTextView() { return dayTextView;}
    public LinearLayout getExamNameLinearLayout(){return examNameLinearLayout;}

}
