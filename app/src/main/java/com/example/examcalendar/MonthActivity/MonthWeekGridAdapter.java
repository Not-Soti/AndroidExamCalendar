package com.example.examcalendar.MonthActivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.examcalendar.R;

import java.util.ArrayList;

public class MonthWeekGridAdapter extends BaseAdapter {


    Context context;
    ArrayList<MonthWeekSquare> weekViews;

    public MonthWeekGridAdapter(Context c, ArrayList<MonthWeekSquare> weekViews) {
        this.context = c;
        this.weekViews = weekViews;
    }

    @Override
    public int getCount() {
        return weekViews.size();
    }

    @Override
    public Object getItem(int i) {
        return weekViews.get(i);
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
        MonthWeekSquare ds = (MonthWeekSquare) getItem(i);
        view = ds;
        return view;
    }
}
