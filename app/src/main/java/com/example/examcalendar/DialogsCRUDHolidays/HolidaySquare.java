package com.example.examcalendar.DialogsCRUDHolidays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.example.examcalendar.DialogsCRUDExams.DialogDeleteExam;
import com.example.examcalendar.DialogsCRUDExams.DialogEditExam;
import com.example.examcalendar.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

/**
 * Class that draws a cell of the MonthActivity grid and its components
 */

public class HolidaySquare extends LinearLayout {

    private Context context;
    private TextView dayTextView;
    private String dayStr; //Strings to set on the view after inflating
    private int month, year;

    private boolean isCurrentMonth; //To check if the day displayed is the main displayed month

    //made to check if the square is normal, for adding holydays or delete them
    public static final int NO_ACTION = 0;
    public static final int ADD_HOLIDAY = 1;
    public static final int DEL_HOLIDAY = 2;
    public static final int FIRST_CHOSEN = 4;
    private int action;

    private boolean isHoliday;

    public HolidaySquare(Context context) {
        super(context);
        this.context = context;
        initializeViews(context);
    }

    public HolidaySquare(Context context, String dayStr, int month, int year, boolean isCurrentMonth, int action, boolean isHoliday) {
        super(context);
        this.context = context;
        this.dayStr = dayStr;
        this.month = month;
        this.year = year;
        this.isCurrentMonth = isCurrentMonth; //chechs if the square is from the current main month
        this.action = action; //checks if the square is from the selected holiday range
        this.isHoliday=isHoliday;

        initializeViews(context);
    }

    public HolidaySquare(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeViews(context);
    }

    public HolidaySquare(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        dayTextView = this.findViewById(R.id.DayText);
        dayTextView.setText(dayStr);

        //Sets the background as defined by /res/drawable/month_week_square
        this.setBackgroundResource(R.drawable.month_day_square_normal);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //TODO MOVER LAS COSAS DEL METODO DE ARRIBA AQUI

        //Getting de BG color from the user settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int bgColor = 0xFFFFFF;

        if(!isCurrentMonth){
            //Make the color more transparent from the normal color
            bgColor = preferences.getInt("bgColorNormalDay", getResources().getColor(R.color.NormalBg));
            bgColor = (bgColor & 0x00FFFFFF) |0x40000000; //First byte is the transparecy, using 25% of current
            int dayColor = (getResources().getColor(R.color.todayDay));
            dayColor = (dayColor & 0x00FFFFFF) |0x40000000;
            dayTextView.setTextColor(dayColor);
        }
        if(action==ADD_HOLIDAY){ //checks if the day is from the selected holiday range
            int borderColor = preferences.getInt("bgColorHolidayDay", Color.RED);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(borderColor);
            paint.setStrokeWidth(10);

            //Paint diagonal lines to days selected
            canvas.drawLine(0, getHeight()/2, getWidth()/2, 0, paint);
            canvas.drawLine(0,getHeight(), getWidth(), 0, paint);
            canvas.drawLine(getWidth()/2, getHeight(), getWidth(), getHeight()/2, paint);
            canvas.drawRect(0,0,getWidth(),getHeight(),paint);
            dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.BOLD);
        }
        if(isHoliday){ //checks if the day is from the selected holiday range
            bgColor = preferences.getInt("bgColorHolidayDay", Color.RED);
        }

        //If we are deleting holidays, draw a cross in the square
        if(this.action==DEL_HOLIDAY) {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5);
            canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
            canvas.drawLine(0, getHeight(), getWidth(), 0, paint);
        }
        ((GradientDrawable) this.getBackground()).setColor(bgColor);
    }

    public TextView getDayTextView() {
        return dayTextView;
    }

    public String getDayStr(){
        return this.dayStr;
    }
    public int getMonth(){
        return  this.month;
    }
    public int getYear(){
        return this.year;
    }

    public int getAction(){
        return this.action;
    }

}
