package com.example.examcalendar.DialogsCRUDHolidays;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import java.util.ArrayList;

public class AddHolidayDayGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<HolidaySquare> dayViews;

    public AddHolidayDayGridAdapter(Context c, ArrayList<HolidaySquare> dayViews) {
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
            //Inflate the layout
            LayoutInflater inf = ((Activity) context).getLayoutInflater();
            view = inf.inflate(R.layout.activity_month,viewGroup, false);
        }

        //Get current item to be displayed
        HolidaySquare ds = (HolidaySquare) getItem(i);
        view = ds;

        //Disable if the day is 0, which means it has not to be represented
        String day = ds.getDayTextView().getText().toString();
        if (day.isEmpty()){
            view.setVisibility(View.INVISIBLE);
            //ds.setEnabled(false);
            //ds.setBackgroundResource(0);
        }

        return view;
    }
}
