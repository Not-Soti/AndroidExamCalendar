package com.example.examcalendar.MonthActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import com.example.examcalendar.DialogsCRUDExams.DialogAddHolidays;
import com.example.examcalendar.DialogsCRUDExams.DialogDeleteExam;
import com.example.examcalendar.DialogsCRUDExams.DialogDeleteHolidays;
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

    private int examTextSize;
    private boolean isToday;

    public MonthDaySquare(Context context) {
        super(context);
        this.context = context;
        initializeViews(context);
    }

    public MonthDaySquare(Context context, ArrayList<String> examStr, String dayStr, int type, int month, int year, int textSize, boolean isToday) {
        super(context);
        this.context = context;
        this.examListStr = examStr;
        this.dayStr = dayStr;
        this.type = type;
        this.month = month;
        this.year = year;
        this.examTextSize = textSize;
        this.isToday = isToday;

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

            examNameLinearLayout.addView(examView);
        }

        dayTextView.setText(dayStr);

        //Sets the background as defined by /res/drawable/month_week_square
        this.setBackgroundResource(checkTypeForBackground(this.type));

        //Getting de BG color from the user settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int bgColor = 0xFFFFFF;

        if(type == MonthDaySquare.EXAM) {
            bgColor = preferences.getInt("bgColorExamDay", getResources().getColor(R.color.ExamBg));
        }else if(type == MonthDaySquare.HOLIDAY){
            bgColor = preferences.getInt("bgColorHolidayDay", getResources().getColor(R.color.HolidayBg));
        }else if(type == MonthDaySquare.NORMAL){
            bgColor = preferences.getInt("bgColorNormalDay", getResources().getColor(R.color.NormalBg));
        }

        //Make today color more intense
        if(isToday){
            float[] colorHSL = new float[3];
            ColorUtils.colorToHSL(bgColor, colorHSL);
            colorHSL[2] *= 0.8f; //adding brightness to the color
            bgColor = ColorUtils.HSLToColor(colorHSL);
        }
        ((GradientDrawable) this.getBackground()).setColor(bgColor);

        //Changing the color for the day if it's today
        if(isToday){
            dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.BOLD);
            dayTextView.setTextColor(getResources().getColor(R.color.todayDay));
        }

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
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.editExamPopupMenu:
                        i = new Intent(auxContext, DialogEditExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.deleteExamPopupMenu:
                        i = new Intent(auxContext, DialogDeleteExam.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.addHolidaysPopupMenu:
                        i = new Intent(auxContext, DialogAddHolidays.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
                        i.putExtra("year", Integer.toString(year));
                        auxContext.startActivity(i);
                        return true;
                    case R.id.deleteHolidaysPopupMenu:
                        i = new Intent(auxContext, DialogDeleteHolidays.class);
                        i.putExtra("day", dayStr);
                        i.putExtra("month", Integer.toString(month + 1));
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
            case 0:
                ret = R.drawable.month_day_square_normal;
                break;
            case 1:
                ret = R.drawable.month_day_square_exam;
                break;
            case 2:
                ret = R.drawable.month_day_square_holiday;
                break;
        }
        return ret;
    }

    /*
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float textSize = getResources().getDimension(R.dimen.examTextSizeMonthGrid);
        examNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
    */


    public TextView getDayTextView() {
        return dayTextView;
    }
    public LinearLayout getExamNameLinearLayout(){return examNameLinearLayout;}


}
