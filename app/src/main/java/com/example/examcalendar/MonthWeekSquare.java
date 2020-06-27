package com.example.examcalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class MonthWeekSquare extends LinearLayout {

    private TextView weekTextView;
    private String weekStr;
    private Paint brush;

    public MonthWeekSquare(Context context) {
        super(context);
        initializeViews(context);
    }

    public MonthWeekSquare(MonthActivityController context, String weekStr) {
        super(context);
        this.weekStr = weekStr;

        initializeViews(context);
    }

    public MonthWeekSquare(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MonthWeekSquare(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public MonthWeekSquare(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context);
    }

    /**
     * Inflates the view in the layout
     */
    private void initializeViews(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.week_square, this);

        weekTextView = (TextView) this.findViewById(R.id.weekTextView);
        weekTextView.setText(weekStr);

        //Sets the background as defined by /res/drawable/month_week_square
        this.setBackgroundResource(R.drawable.month_week_square);
    }
}
