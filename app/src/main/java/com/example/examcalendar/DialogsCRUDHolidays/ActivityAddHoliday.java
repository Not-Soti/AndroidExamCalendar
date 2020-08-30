package com.example.examcalendar.DialogsCRUDHolidays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examcalendar.HelpClasses.AutoGridView;
import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.HelpClasses.MonthGridOperations;
import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.MonthActivity.MonthDaySquare;
import com.example.examcalendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ActivityAddHoliday extends Activity implements MonthGridOperations {

    private DialogHolidayModel model;
    private Button nextMonthButton, previousMonthButton;

    private AutoGridView dayGridView;
    private AddHolidayDayGridAdapter dayGridAdapter;
    private TextView monthTextView, yearTextView;
    private Button acceptButton;

    private static int nColumns;
    private LinearLayout dayNamesLinearLayout;

    private static final String[] MONTH_NAMES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    private static int printedYear, printedMonth;

    private String startHolidayDay, startHolidayMonth, startHolidayYear;
    private String endHolidayDay, endHolidayMonth, endHolidayYear;

    private String endDate; //end date for the holiday range
    private String startDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_holiday);

        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        model = new DialogHolidayModel(this);
        nextMonthButton = findViewById(R.id.AddHolAct_NextMonthButton);
        previousMonthButton = findViewById(R.id.AddHolAct_PrevMonthButton);
        dayGridView = findViewById(R.id.AddHolAct_DaysAutoGridView);
        monthTextView = findViewById(R.id.AddHolAct_MonthTextView);
        yearTextView = findViewById(R.id.AddHolAct_YearTextView);
        acceptButton = findViewById(R.id.AddHolAct_AcceptButton);
        dayNamesLinearLayout = findViewById(R.id.AddHolAct_DayNamesGridLayout);

        //Getting month and year to print
        try {
            printedYear = Integer.valueOf(bundle.getString("year"));
            printedMonth = Integer.valueOf(bundle.getString("month"))-1;
        }catch (NullPointerException e){
            printedYear = model.getYear(); //TODO hacer que sean los datos de las pantallas de crud examen/vacaciones
            printedMonth = model.getMonth(); //from 0 to 11
        }

        //Getting the starting range date
        startHolidayDay = bundle.getString("day");
        startHolidayMonth = bundle.getString("month");
        startHolidayYear = bundle.getString("year");
        String dateAux = startHolidayYear + "-" + startHolidayMonth + "-" + startHolidayDay;
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDate = null;
        try {
            startDate = newFormat.format(oldFormat.parse(dateAux));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Getting the number of columns to display
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nColumns = preferences.getInt("nColumsMonthGrid", getResources().getInteger(R.integer.columnsOnDayGrid));
        dayGridView.setNumColumns(nColumns);

        //Adding Sat and Sun TextViews to the activity
        if(nColumns==7){
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            TextView textViewSaturday = new TextView(this);
            textViewSaturday.setGravity(Gravity.CENTER);
            textViewSaturday.setLayoutParams(lp);
            textViewSaturday.setText("S");
            dayNamesLinearLayout.addView(textViewSaturday);

            TextView textViewSunday = new TextView(this);
            textViewSunday.setGravity(Gravity.CENTER);
            textViewSunday.setLayoutParams(lp);
            textViewSunday.setText("D");
            dayNamesLinearLayout.addView(textViewSunday);
        }

        //Paint the bgColor
        CommonActivityThings.paintBackground(this);
        drawGrid();


        //Set listeners
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonthPressed();
            }
        });
        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthPressed();
            }
        });

        //setting listener to the grid items to get the complete holiday range
        dayGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Getting the ending holiday range date
                endHolidayDay = ((HolidaySquare) view).getDayStr();
                endHolidayMonth = String.valueOf(((HolidaySquare) view).getMonth());
                endHolidayYear = String.valueOf(((HolidaySquare) view).getYear());

                String dateAux = endHolidayYear + "-" + endHolidayMonth + "-" + endHolidayDay;
                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                endDate = null;
                try {
                    endDate = newFormat.format(oldFormat.parse(dateAux));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //If the selected date is before the 1st one, reorder them
                if(startDate.compareTo(endDate)>0){
                    String aux = startDate;
                    startDate = endDate;
                    endDate=aux;
                }

                //redraw the GUI
                drawGrid();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startDate==null || endDate==null){
                    Toast.makeText(ActivityAddHoliday.this, "Selecciona un rango de vacaciones", Toast.LENGTH_LONG).show();
                }else {
                    model.addHolidays(startDate, endDate);
                    Intent i = new Intent(ActivityAddHoliday.this, MonthActivityController.class);
                    i.putExtra("month", startHolidayMonth);
                    i.putExtra("year", startHolidayYear);
                    Toast.makeText(ActivityAddHoliday.this, "¡Vacaciones guardadas!", Toast.LENGTH_LONG).show();
                    startActivity(i);
                }
            }
        });

    }

    /**
     * Method that draws the GUI
     */
    private void drawGrid() {

        monthTextView.setText(MONTH_NAMES[printedMonth]);
        yearTextView.setText(String.valueOf(printedYear));

        //Number of days and weeks in current month
        int dayOfStart = model.getDayOfWeek(printedYear, printedMonth); //The 1st day of the month is monday, tuesday...
        int numberOfDaysCurrentMonth = model.getDays(printedYear, printedMonth);
        int numberOfWeeksCurrentMonth = model.getWeeks(printedYear, printedMonth);

        /*days to draw which are number of days(Monday-Friday) in the current month
            + number of days in previous and next month if the fit in the grid
         */
        int numberOfDaysToDrawCurrentMonth = (nColumns * numberOfWeeksCurrentMonth) - ((7 - nColumns) * numberOfWeeksCurrentMonth);
        int posGrid = 0; //Position to draw in the grid

        ArrayList<HolidaySquare> dayViews = new ArrayList<>(numberOfDaysToDrawCurrentMonth);

        //Drawing the days from the previous month
        int daysPreviousMonth = model.getDays(printedYear, printedMonth - 1);
        int dayToDrawPreviousMonth = daysPreviousMonth - (dayOfStart - 2); //number of the monday from te previous month

        while ((posGrid < dayOfStart - 1) && (posGrid < nColumns)) {
            int auxMonth = printedMonth; //remember months goes from 0 to 11 but on model they are 1 to 12
            String currentDateAux = printedYear + "-" + auxMonth + "-" + dayToDrawPreviousMonth;
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = null;
            try {
                currentDate = newFormat.format(oldFormat.parse(currentDateAux));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dayToDrawStr = String.valueOf(dayToDrawPreviousMonth);

            int squareAction = HolidaySquare.NO_ACTION;

            if(startDate.equals(currentDate)){ //checks if this day is the selected starting day
                squareAction=HolidaySquare.ADD_HOLIDAY;
            }
            if(endDate!=null) { //checks for the selected range
                if(model.dateIsBetween(startDate, endDate, currentDate))
                    squareAction=HolidaySquare.ADD_HOLIDAY;
            }
            //Checks if the day was already holiday! :D
            boolean holidayExist = false;
            if(model.searchHolidays(currentDate)){
                holidayExist=true;
            }

            HolidaySquare ds = new HolidaySquare(this, dayToDrawStr, printedMonth, printedYear, false, squareAction,holidayExist);

            //Adding a click listener to de MonthDaySquare to open the popupMenu
            //setDaySquareListener(ds, dayToRepresent);
            dayViews.add(ds);

            dayToDrawPreviousMonth++;
            posGrid++;
        }//while prev month


        //Drawing days from current month
        int dayToDrawCurrentMonth = 1;
        while (dayToDrawCurrentMonth <= numberOfDaysCurrentMonth) {

            //if it's monday and its not the 1st day, add days not represented (5 here since I dont
            //want weekends now)
            if(nColumns==5) {
                if ((dayToDrawCurrentMonth == 1) && (dayOfStart == 6)) {
                    dayToDrawCurrentMonth += 2;
                } else if ((dayToDrawCurrentMonth == 1) && (dayOfStart == 7)) {
                    dayToDrawCurrentMonth += 1;
                } else if ((posGrid % 5 == 0) && (posGrid >= 5)) {
                    dayToDrawCurrentMonth += (7 - nColumns);
                }
            }

            //Break if its going to paint a day out of range
            if (dayToDrawCurrentMonth > numberOfDaysCurrentMonth) break;

            int auxMonth = printedMonth + 1;
            String currentDateAux = printedYear + "-" + auxMonth + "-" + dayToDrawCurrentMonth;
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = null;
            try {
                currentDate = newFormat.format(oldFormat.parse(currentDateAux));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dayToDrawStr = String.valueOf(dayToDrawCurrentMonth);
            int squareAction=HolidaySquare.NO_ACTION;
            if(startDate.equals(currentDate)){ //checks if this day is the selected starting day
                squareAction=HolidaySquare.ADD_HOLIDAY;
            }
            if(endDate!=null) {
                if(model.dateIsBetween(startDate, endDate, currentDate))
                    squareAction=HolidaySquare.ADD_HOLIDAY;
            }
            //Checks if the day was already holiday! :D
            boolean holidayExist = false;
            if(model.searchHolidays(currentDate)){
                //squareAction=HolidaySquare.HOLIDAY_EXIST;
                holidayExist=true;
            }
            HolidaySquare ds = new HolidaySquare(this, dayToDrawStr, printedMonth + 1, printedYear, true, squareAction,holidayExist);
            dayViews.add(ds);

            dayToDrawCurrentMonth++;
            posGrid++;
        }//while draw current month


        //Draw days of next month if the last week has blank cells
        int dayOfEnd = posGrid % nColumns; //Position in the week of the las day of the month
        int daysNextMonth = nColumns - dayOfEnd;
        int dayToDrawNextMonth = 1;
        for (int i = 0; (i < daysNextMonth) && (daysNextMonth < nColumns); i++) {

            int type = MonthDaySquare.NORMAL;
            int auxMonth = printedMonth + 2; //remember months goes from 0 to 11 but on model they are 1 to 12
            String currentDateAux = new String(printedYear + "-" + auxMonth + "-" + dayToDrawNextMonth);
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = null;
            try {
                currentDate = newFormat.format(oldFormat.parse(currentDateAux));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dayToDrawStr = String.valueOf(dayToDrawNextMonth);
            int squareAction = HolidaySquare.NO_ACTION;
            if(startDate.equals(currentDate)){ //checks if this day is the selected starting day
                squareAction=HolidaySquare.ADD_HOLIDAY;
            }
            if(endDate!=null) {
                if(model.dateIsBetween(startDate, endDate, currentDate))
                    squareAction=HolidaySquare.ADD_HOLIDAY;
            }

            //Checks if the day was already holiday! :D
            boolean holidayExist = false;
            if(model.searchHolidays(currentDate)){
                //squareAction=HolidaySquare.HOLIDAY_EXIST;
                holidayExist = true;
            }
            HolidaySquare ds = new HolidaySquare(this, dayToDrawStr, printedMonth + 2, printedYear, false, squareAction,holidayExist);

            //Adding a click listener to de MonthDaySquare to open the popupMenu
            //setDaySquareListener(ds, dayToRepresent);
            dayViews.add(ds);

            dayToDrawNextMonth++;
        }//for next month

        dayGridAdapter = new AddHolidayDayGridAdapter(this, dayViews);
        dayGridView.setAdapter(dayGridAdapter);
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(ActivityAddHoliday.this, MonthActivityController.class);
        i.putExtra("month", startHolidayMonth);
        i.putExtra("year", startHolidayYear);
        startActivity(i);
    }


    public void nextMonthPressed(){
        printedMonth++;
        if(printedMonth ==12){
            printedYear++;
            printedMonth =0;
        }
        //dayOfStart = getDayOfWeek(year,month);
        drawGrid();
    }
    public void previousMonthPressed(){
        if(printedMonth ==0){
            printedYear--;
            printedMonth =11;
        }else{
            printedMonth--;
        }
        //dayOfStart = getDayOfWeek(year,month);
        drawGrid();
    }

    @Override
    public void drawNextMonth() {
        this.nextMonthPressed();
    }

    @Override
    public void drawPreviousMonth() {
        this.previousMonthPressed();
    }
}
