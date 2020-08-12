package com.example.examcalendar.MonthActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.examcalendar.HelpClasses.AutoGridView;
import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MonthActivityController extends Activity{

    private static final String TAG = "MonthActivityController";

    private MonthActivityModel model;
    private AutoGridView dayGridView;
    private MonthDayGridAdapter dayGridAdapter;
    private TextView monthTextView, yearTextView;
    private Button nextMonthButton, prevMonthButton;
    private RelativeLayout globalLayout;


    //TODO Hacerlo en resources
    private static final String[] MONTH_NAMES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    private static int year, month;
    //private int representedDays; //number of days that should be represented
    //private int dayOfStart; //1st day of the month

    @Override
    protected void onCreate(Bundle savedIntenceState) {
        super.onCreate(savedIntenceState);
        setContentView(R.layout.activity_month);

        model = new MonthActivityModel(this);

        dayGridView = (AutoGridView) findViewById(R.id.MonthAct_DaysAutoGridView);
        monthTextView = (TextView)  findViewById(R.id.MonthAct_MonthTextView);
        yearTextView = (TextView) findViewById(R.id.MonthAct_YearTextView);
        nextMonthButton = (Button) findViewById(R.id.MonthAct_NextMonthButton);
        prevMonthButton = (Button) findViewById(R.id.MonthAct_PrevMonthButton);
        globalLayout = findViewById(R.id.MonthAct_GlobalLayout);

        year = getYear(); //TODO hacer que sean los datos de las pantallas de crud examen/vacaciones
        month = getMonth(); //from 0 to 11
        Log.d(TAG, "month " + month);
        //dayOfStart = getDayOfWeek(year, month); //The 1st day of the month is monday, tuesday...

        //Paint the bgColor
        CommonActivityThings.paintBackground(this);

        drawUI();

        //Set listeners
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonthPressed();
            }
        });
        prevMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthPressed();
            }
        });

    }

    /**
     * Method that draws the GUI
     */
    private void drawUI(){

        monthTextView.setText(MONTH_NAMES[month]);
        yearTextView.setText(String.valueOf(year));
        int nColumns = this.getResources().getInteger(R.integer.MonthCols); //Colums to draw


        //Number of days and weeks in current month
        int dayOfStart = getDayOfWeek(year, month); //The 1st day of the month is monday, tuesday...
        int numberOfDaysCurrentMonth = getDays(year,month);
        int numberOfWeeksCurrentMonth = getWeeks(year, month);
        Log.d(TAG, "day of start current month " + dayOfStart);

        /*days to draw which are number of days(Monday-Friday) in the current month
            + number of days in previous and next month if the fit in the grid
         */
        int numberOfDaysToDrawCurrentMonth = (nColumns*numberOfWeeksCurrentMonth) - ((7-nColumns)*numberOfWeeksCurrentMonth);
        int posGrid = 0; //Position to draw in the grid

        ArrayList<MonthDaySquare> dayViews = new ArrayList<>(numberOfDaysToDrawCurrentMonth);

        //Drawing the days from the previous month
        int daysPreviousMonth = getDays(year, month-1);
        Log.d(TAG, "days prev month " + daysPreviousMonth + " month " + (month-1));
        int dayToDrawPreviousMonth = daysPreviousMonth - (dayOfStart-2); //number of the monday from te previous month
        Log.d(TAG, "day to draw prev month " + dayToDrawPreviousMonth);

        while ((posGrid < dayOfStart-1) && (posGrid < 5)){
            int type = MonthDaySquare.NORMAL;
            int auxMonth = month; //remember months goes from 0 to 11 but on model they are 1 to 12
            String printingDateAux = new String(year+"-"+auxMonth+"-"+dayToDrawPreviousMonth);
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String printingDate = null;
            try{
                printingDate = newFormat.format(oldFormat.parse(printingDateAux));
            } catch (ParseException e){
                e.printStackTrace();
            }
            //Checks if the day has any exam
            ArrayList<String> examList = model.searchExam(printingDate);
            //If there adre exams sets the day type to exam
            if(!examList.isEmpty()) type = MonthDaySquare.EXAM;

            //Checks if the day is holiday! :D
            boolean isHoliday = model.searchHolidays(printingDate);
            //If it's holiday, sets de dat type to it
            if(isHoliday){
                type = MonthDaySquare.HOLIDAY;
            }

            String dayToDrawStr = String.valueOf(dayToDrawPreviousMonth);
            MonthDaySquare ds = new MonthDaySquare(this, examList, dayToDrawStr, type, month, year, false, false);

            //Adding a click listener to de MonthDaySquare to open the popupMenu
            //setDaySquareListener(ds, dayToRepresent);
            dayViews.add(ds);

            dayToDrawPreviousMonth++;
            posGrid++;
        }//while prev month


        //Drawing days from current month
        int dayToDrawCurrentMonth = 1;
        //while(posGrid < numberOfDaysCurrentMonth){
        while(dayToDrawCurrentMonth <= numberOfDaysCurrentMonth){

            //if it's monday and its not the 1st day, add days not represented (5 here since I dont
            //want weekends now)
            if((dayToDrawCurrentMonth==1) && (dayOfStart == 6)){
                dayToDrawCurrentMonth+=2;
            }else if((dayToDrawCurrentMonth==1) && (dayOfStart == 7)){
                dayToDrawCurrentMonth+=1;
            }else if((posGrid%5==0) && (posGrid>=5)){
                dayToDrawCurrentMonth += (7-nColumns); //Todo noviembre no funciona
            }

            //Break if its going to paint a day ou tof range
            if(dayToDrawCurrentMonth>numberOfDaysCurrentMonth) break;
            //TODO mejorar esto

            int type = MonthDaySquare.NORMAL;

            int auxMonth = month+1;
            String printingDateAux = new String(year+"-"+auxMonth+"-"+dayToDrawCurrentMonth);
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String printingDate = null;
            try{
                printingDate = newFormat.format(oldFormat.parse(printingDateAux));
            } catch (ParseException e){
                e.printStackTrace();
            }

            //Checks if the printing date is today
            String today = newFormat.format(new Date());
            boolean isToday = today.equals(printingDate);

            //Checks if the day has any exam
            ArrayList<String> examList = model.searchExam(printingDate);
            //If there are exams sets the day type to exam
            if(!examList.isEmpty()) type = MonthDaySquare.EXAM;

            //Checks if the day is holiday! :D
            boolean isHoliday = model.searchHolidays(printingDate);
            //If it's holiday, sets de dat type to it
            if(isHoliday){
                type = MonthDaySquare.HOLIDAY;
            }

            String dayToDrawStr = String.valueOf(dayToDrawCurrentMonth);

            MonthDaySquare ds = new MonthDaySquare(this, examList, dayToDrawStr, type, month+1, year, isToday, true);
            dayViews.add(ds);

            dayToDrawCurrentMonth++;
            posGrid++;
        }//while draw current month


        //Draw days of next month if the last week has blank cells
        int dayOfEnd = posGrid%nColumns; //Position in the week of the las day of the month
        int daysNextMonth = nColumns - dayOfEnd;
        int dayToDrawNextMonth=1;
        for (int i=0; (i < daysNextMonth) && (daysNextMonth<nColumns); i++){

            int type = MonthDaySquare.NORMAL;
            int auxMonth = month+2; //remember months goes from 0 to 11 but on model they are 1 to 12
            String printingDateAux = new String(year+"-"+auxMonth+"-"+dayToDrawNextMonth);
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String printingDate = null;
            try{
                printingDate = newFormat.format(oldFormat.parse(printingDateAux));
            } catch (ParseException e){
                e.printStackTrace();
            }
            //Checks if the day has any exam
            ArrayList<String> examList = model.searchExam(printingDate);
            //If there adre exams sets the day type to exam
            if(!examList.isEmpty()) type = MonthDaySquare.EXAM;

            //Checks if the day is holiday! :D
            boolean isHoliday = model.searchHolidays(printingDate);
            //If it's holiday, sets de dat type to it
            if(isHoliday){
                type = MonthDaySquare.HOLIDAY;
            }

            String dayToDrawStr = String.valueOf(dayToDrawNextMonth);
            MonthDaySquare ds = new MonthDaySquare(this, examList, dayToDrawStr, type, month+2, year, false, false);

            //Adding a click listener to de MonthDaySquare to open the popupMenu
            //setDaySquareListener(ds, dayToRepresent);
            dayViews.add(ds);

            dayToDrawNextMonth++;
        }//for next month



        dayGridAdapter = new MonthDayGridAdapter(this, dayViews);
        dayGridView.setAdapter(dayGridAdapter);


        /*
        //Days to paint since its from Monday to Friday
        representedDays = ((numberOfDays + dayOfStart) - (2 * numberOfWeeks));

        //Creates the list of days to display and
        //inflates the days grid view
        ArrayList<MonthDaySquare> dayViews = new ArrayList<>(representedDays);
        int dayToRepresent = 0;
        for (int i = 1; (i <=representedDays+dayOfStart) && (dayToRepresent < numberOfDays); i++) { //From 1 to n
            //The numbers skip 2 positions when it's Saturday or Sunday if i is in position of Friday
            dayToRepresent++;
            if(i<dayOfStart) dayToRepresent=0; //to avoid weeks where 1st day is not a Monday
            if(dayOfStart==7 && i==6) dayToRepresent=2; //control when it starts on sunday

            int type = MonthDaySquare.NORMAL;

            int auxMonth = month+1;
            String printingDateAux = new String(year+"-"+auxMonth+"-"+dayToRepresent);
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String printingDate = null;
            try{
                printingDate = newFormat.format(oldFormat.parse(printingDateAux));
            } catch (ParseException e){
                e.printStackTrace();
            }

            //Checks if the printing date is today
            String today = newFormat.format(new Date());
            boolean isToday = today.equals(printingDate);

            //Checks if the day has any exam
            ArrayList<String> examList = model.searchExam(printingDate);
            //If there adre exams sets the day type to exam
            if(!examList.isEmpty()) type = MonthDaySquare.EXAM;

            //Checks if the day is holiday! :D
            boolean isHoliday = model.searchHolidays(printingDate);
            //If it's holiday, sets de dat type to it
            if(isHoliday){
                type = MonthDaySquare.HOLIDAY;
            }

            String dayToDraw = dayToRepresent == 0 ? "" : String.valueOf(dayToRepresent);
            //MonthDaySquare ds = new MonthDaySquare(this, exam.toString(), dayToDraw, type);
            int examFontSize = preferences.getInt("fontSizeExamTextView", R.integer.examTextSizeMonthGrid);

            MonthDaySquare ds = new MonthDaySquare(this, examList, dayToDraw, type, month, year, examFontSize, isToday);

            //Adding a click listener to de MonthDaySquare to open the popupMenu
            //setDaySquareListener(ds, dayToRepresent);
            dayViews.add(ds);

            //Skip counting saturdays and sundays
            if((i%5 == 0) && (i>=5)){
                dayToRepresent +=2;
            }
        }//for
        dayGridAdapter = new MonthDayGridAdapter(this, dayViews);
        dayGridView.setAdapter(dayGridAdapter);


         */
    }



    /*
     * Button listeners
     */

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
    }


    public void nextMonthPressed(){
        month++;
        if(month==12){
            year++;
            month=0;
        }
        //dayOfStart = getDayOfWeek(year,month);
        drawUI();
    }
    public void previousMonthPressed(){
        if(month==0){
            year--;
            month=11;
        }else{
            month--;
        }
        //dayOfStart = getDayOfWeek(year,month);
        drawUI();
    }


    /*
     * Help methods to calculate dates
     */

    //get number of days in current month
    private  int getDays(int year, int month){
        Log.d(TAG, "getDays year month " + year + " " + month);
        Calendar myCal = new GregorianCalendar(year, month, 1);
        int numberOfDays = myCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return numberOfDays;
    }

    //get number of weeks in current month
    private int getWeeks(int year, int month){
        Calendar myCal = new GregorianCalendar(year, month, 1);
        myCal.setMinimalDaysInFirstWeek(1);
        int weeks = myCal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        return weeks;
    }

    //get current year
    private int getYear(){
        Calendar myCal = Calendar.getInstance();
        int year = myCal.get(Calendar.YEAR);
        return year;
    }

    //get current month
    private int getMonth(){
        Calendar myCal = Calendar.getInstance();
        int month = myCal.get(Calendar.MONTH);
        return month;
    }

    //Get the first day of a week
    private int getDayOfWeek(int year, int month){
        Calendar myCal = Calendar.getInstance();
        myCal.set(Calendar.MONTH, month);
        myCal.set(Calendar.YEAR, year);
        myCal.set(Calendar.DAY_OF_MONTH, 1);

        Date firstDay = myCal.getTime();
        int day = firstDay.getDay();
        //int day = myCal.get(Calendar.DAY_OF_WEEK);
        if(day==0)//If it's Sunday then it's the 7th day
            day=7;
        return day;
    }
}
