package com.example.examcalendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class MonthActivityController extends Activity{

    private GridView dayGridView;
    private MonthDayGridAdapter dayGridAdapter;
    private GridView weekGridView;
    private MonthWeekGridAdapter weekGridAdapter;

    @Override
    protected void onCreate(Bundle savedIntenceState) {
        super.onCreate(savedIntenceState);
        setContentView(R.layout.month_activity);

        //Creates the list of weeks and inflates de weekGridView
        ArrayList<MonthWeekSquare> weekViews = new ArrayList<>();
        for(int i = 0; i<5; i++){
            MonthWeekSquare ws = new MonthWeekSquare(this,String.valueOf(i));
            weekViews.add(ws);
        }

        weekGridView = (GridView) findViewById(R.id.WeeksGridView);
        weekGridAdapter = new MonthWeekGridAdapter(this, weekViews);
        weekGridView.setAdapter(weekGridAdapter);


        //Creates the list of days to display and
        //inflates the days grid view
        ArrayList<MonthDaySquare> dayViews = new ArrayList<>();
        for(int i = 0; i<20; i++) {
            int type = 0; //TODO Obtener el tipo de dia

            //TODO COMPARAR WIDTH DEL TEXTO CON EL WIDTH DEL CUADRADO Y SI ES MAS GRANDE HACER EL TEXTO MAS PEQUEÑO
            //TODO de alguna forma averiguar el cuadrado mas grande para que todos sean iguales
            MonthDaySquare ds = new MonthDaySquare(this, "Examen", String.valueOf(i), type);
            if(i==5) ds = new MonthDaySquare(this, "HOY NO HAY EXAMEEEN", "Día", type);
            if(i==13) ds = new MonthDaySquare(this, "", "Día", type);
            dayViews.add(ds);
        }

        /*
        dayGridView = (GridView) findViewById(R.id.DaysGridview);
        dayGridAdapter = new MonthDayGridAdapter(this, dayViews);
        dayGridView.setAdapter(dayGridAdapter);
        */
        AutoGridView dayGridView = (AutoGridView) findViewById(R.id.DaysAutoGridView);
        dayGridAdapter = new MonthDayGridAdapter(this, dayViews);
        dayGridView.setAdapter(dayGridAdapter);
    }
}
