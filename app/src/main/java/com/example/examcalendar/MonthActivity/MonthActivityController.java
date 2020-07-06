package com.example.examcalendar.MonthActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.examcalendar.HelpClasses.AutoGridView;
import com.example.examcalendar.MainActivity.MainActivity;
import com.example.examcalendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ListIterator;

public class MonthActivityController extends Activity{

    private MonthActivityModel model;

    private AutoGridView dayGridView;
    private MonthDayGridAdapter dayGridAdapter;
    private GridView weekGridView;
    //private MonthWeekGridAdapter weekGridAdapter;
    private TextView monthTextView, yearTextView;
    private Button addExamButton, deleteExamButton, addHolidaysButton, deleteHolidaysButton;
    private Button nextMonthButton, prevMonthButton;


    //TODO Hacerlo en resources
    private static final String[] MONTH_NAMES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    private int year, month, day;
    private int representedDays; //number of days that should be represented
    private int dayOfStart; //1st day of the month

    @Override
    protected void onCreate(Bundle savedIntenceState) {
        super.onCreate(savedIntenceState);
        setContentView(R.layout.month_activity);

        model = new MonthActivityModel(this);

        //weekGridView = (GridView) findViewById(R.id.WeeksGridView);
        dayGridView = (AutoGridView) findViewById(R.id.DaysAutoGridView);
        monthTextView = (TextView)  findViewById(R.id.textViewMonth);
        yearTextView = (TextView) findViewById(R.id.textViewYear);
        addExamButton = (Button) findViewById(R.id.bAddExam);
        deleteExamButton = (Button) findViewById(R.id.bDeleteExam);
        addHolidaysButton = (Button) findViewById(R.id.bAddHoliday);
        deleteHolidaysButton = (Button) findViewById(R.id.bDeleteHoliday);
        nextMonthButton = (Button) findViewById(R.id.bNextMonth);
        prevMonthButton = (Button) findViewById(R.id.bPrevMonth);

        year = getYear();
        month = getMonth();
        dayOfStart = getDayOfWeek(year, month); //The 1st day of the month is monday, tuesday...


        drawUI();



        //Set listeners
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonthPressed(view);
            }
        });
        prevMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthPressed(view);
            }
        });

        addExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExamPressed(view);
            }
        });

         deleteExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteExamPressed(view);
            }
        });

         addHolidaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHolidaysPressed(view);
            }
        });

        deleteHolidaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteHolidaysPressed(view);
            }
        });

    }

    /**
     * Method that draws the GUI
     */
    private void drawUI(){
        monthTextView.setText(MONTH_NAMES[month]);
        yearTextView.setText(String.valueOf(year));

        //Number of days and weeks in current month
        int numberOfDays = getDays(year,month);
        int numberOfWeeks = getWeeks(year, month);

        //Days to paint since its from Monday to Friday
        representedDays = ((numberOfDays + dayOfStart) - (2 * numberOfWeeks));

/*
        //TODO contar las semanas desde el inicio del curso
        //Creates the list of weeks and inflates de weekGridView
        ArrayList<MonthWeekSquare> weekViews = new ArrayList<>();
        for(int i = 0; i<numberOfWeeks; i++){
            MonthWeekSquare ws = new MonthWeekSquare(this,String.valueOf(i+1));
            weekViews.add(ws);
        }
        weekGridAdapter = new MonthWeekGridAdapter(this, weekViews);
        weekGridView.setAdapter(weekGridAdapter);
*/

        //Creates the list of days to display and
        //inflates the days grid view
        ArrayList<MonthDaySquare> dayViews = new ArrayList<>(31);
        int dayToRepresent = 0;
        for (int i = 1; (i <=representedDays+dayOfStart) && (dayToRepresent < numberOfDays); i++) { //From 1 to n
        //for(int i = 0; i<representedDays+dayOfStart; i++) {
            //The numbers skip 2 positions when it's Saturday or Sunday if i is in position of Friday
            dayToRepresent++;
            if(i<dayOfStart) dayToRepresent=0; //to avoid weeks where 1st day is not a Monday
            if(dayOfStart==7 && i==6) dayToRepresent=2; //control when it starts on sunday

            int type = MonthDaySquare.NORMAL;
            StringBuilder exam = new StringBuilder("");

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

            //Checks if the day has any exam
            ArrayList<String> examList = model.searchExam(printingDate);

            if(!examList.isEmpty()){
                ListIterator<String> itr = examList.listIterator();
                //Add the first exam
                exam.append(itr.next());
                //If there are more, add all with a new lane
                while(itr.hasNext()){
                    exam.append("\n"+itr.next());
                }
                //Sets the day type to exam
                type = MonthDaySquare.EXAM;
            }

            //Checks if the day is holiday! :D
            boolean isHoliday = model.searchHolidays(printingDate);
            //If it's holiday, sets de dat type to it
            if(isHoliday){
                type = MonthDaySquare.HOLIDAY;

            }

            //TODO COMPARAR WIDTH DEL TEXTO CON EL WIDTH DEL CUADRADO Y SI ES MAS GRANDE HACER EL TEXTO MAS PEQUEÑO
            String dayToDraw = dayToRepresent == 0 ? "" : String.valueOf(dayToRepresent);
            MonthDaySquare ds = new MonthDaySquare(this, exam.toString(), dayToDraw, type);
            /*
                Adding a click listener to de MonthDaySquare to open the popupMenu
             */
            final int finalDayToRepresent = dayToRepresent; // neded to add the day to the intent bundle
            ds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(MonthActivityController.this, view);
                    //Inflate the popup xml file
                    popup.getMenuInflater().inflate(R.menu.month_cell_popup_menu, popup.getMenu());

                    //registering actions on clicking the menu items
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.addExamPopupMenu:
                                    Intent i = new Intent(MonthActivityController.this, DialogAddExam.class);
                                    i.putExtra("day", Integer.toString(finalDayToRepresent));
                                    i.putExtra("month", Integer.toString(month+1));
                                    i.putExtra("year", Integer.toString(year));
                                    startActivity(i);
                                    return true;
                                case R.id.addHolidaysPopupMenu:

                                    return  true;
                                default:
                                    return false;
                            }

                        }
                    }); // menu click listener
                    popup.show();
                }
            });//Set on click listener

            dayViews.add(ds);

            //Skip counting saturdays and sundays
            if((i%5 == 0) && (i>=5)){
                dayToRepresent +=2;
            }
        }//for

        dayGridAdapter = new MonthDayGridAdapter(this, dayViews);
        dayGridView.setAdapter(dayGridAdapter);

    }

    /*
     * Button listeners
     */

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void addExamPressed(View view){
        Intent i = new Intent(this, DialogAddExam.class);
        i.putExtra("month", Integer.toString(month+1));
        i.putExtra("year", Integer.toString(year));
        startActivity(i);
    }
    public void deleteExamPressed(View view){
        Intent i = new Intent(this, DialogDeleteExam.class);
        i.putExtra("month", Integer.toString(month+1));
        i.putExtra("year", Integer.toString(year));
        startActivity(i);
    }
    public void addHolidaysPressed(View view){
        Intent i = new Intent(this, DialogAddHolidays.class);
        i.putExtra("month", Integer.toString(month+1));
        i.putExtra("year", Integer.toString(year));
        startActivity(i);
    }
    public void deleteHolidaysPressed(View view){
        Intent i = new Intent(this, DialogDeleteHolidays.class);
        i.putExtra("month", Integer.toString(month+1));
        i.putExtra("year", Integer.toString(year));
        startActivity(i);
    }

    public void nextMonthPressed(View view){
        month++;
        if(month==12){
            year++;
            month=0;
        }
        dayOfStart = getDayOfWeek(year,month);
        drawUI();
    }
    public void previousMonthPressed(View view){
        if(month==0){
            year--;
            month=11;
        }else{
            month--;
        }
        dayOfStart = getDayOfWeek(year,month);
        drawUI();
    }



    /*
     * Help methods to calculate dates
     */

    //get number of days in current month
    private  int getDays(int year, int month){
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
