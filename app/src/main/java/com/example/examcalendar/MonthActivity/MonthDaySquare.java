package com.example.examcalendar.MonthActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.examcalendar.R;

/**
 * Class that draws a cell of the MonthActivity grid and its components
 */

public class MonthDaySquare extends LinearLayout {

    private TextView examNameTextView;
    private TextView dayTextView;
    private String examStr, dayStr; //Strings to set on the view after inflating
    private int type; //Tells if a particular day is normal, a exam one or a holiday one
    public static final int NORMAL = 0;
    public static final int EXAM = 1;
    public static final int HOLIDAY = 2;

    public MonthDaySquare(Context context) {
        super(context);
        initializeViews(context);
    }

    public MonthDaySquare(Context context, String examStr, String dayStr, int type) {
        super(context);
        this.examStr=  examStr;
        this.dayStr = dayStr;
        this.type = type;

        initializeViews(context);
    }

    public MonthDaySquare(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MonthDaySquare(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    /**
     * Inflates the view in the layout
     */
    private void initializeViews(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_square, this);

        examNameTextView = (TextView) this.findViewById(R.id.ExamNameText);
        dayTextView = (TextView) this.findViewById(R.id.DayText);

        examNameTextView.setText(examStr);
        dayTextView.setText(dayStr);

        //Sets the background as defined by /res/drawable/month_week_square
        this.setBackgroundResource(checkTypeForBackground(this.type));
    }

    /**
     * Checks the type of the day and returns the correct background to set
     */
    private int checkTypeForBackground(int i){
        int ret = 0;
        switch (i){
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

    public TextView getExamNameTextView(){
        return examNameTextView;
    }
    public TextView getDayTextView(){
        return dayTextView;
    }

    /**
     * Overriding the click listener to open a popup menu
     */


    /**
     * Sets atributes for the exam name and the day
     */
    /* NO FUNCIONA
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        examNameTextView.setText(examStr);
        dayTextView.setText(dayStr);
    }
    */

}
