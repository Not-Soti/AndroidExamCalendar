package com.example.examcalendar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class MonthDayGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<MonthDaySquare> dayViews;

    public MonthDayGridAdapter(Context c, ArrayList<MonthDaySquare> dayViews) {
        this.context = c;
        this.dayViews = dayViews;
    }

    @Override
    public int getCount() {
        return dayViews.size();
    }

    @Override
    public Object getItem(int i) {
        return dayViews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            //Inflate the layour
            LayoutInflater inf = ((Activity) context).getLayoutInflater();
            view = inf.inflate(R.layout.month_activity,viewGroup, false);
        }

        //Get current item to be displayed
        MonthDaySquare ds = (MonthDaySquare) getItem(i);
        view = ds;

        //Disable if the day is 0, which means it has not to be represented
        String day = ds.getDayTextView().getText().toString();
        if (day.isEmpty()){
            ds.setEnabled(false);
            ds.setBackgroundResource(0);
        }

        return view;
    }
}
