package com.example.examcalendar.DialogsCRUDHolidays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.examcalendar.DataBase.DBHelper;
import com.example.examcalendar.DataBase.DBStructure;
import com.example.examcalendar.DialogsCRUDExams.DialogExamModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DialogHolidayModel {

    DBHelper dbHelper;
    String tag = "DialogDBHelper";

    public DialogHolidayModel(Context con){
        this.dbHelper = new DBHelper(con);
    }

    /**
     * Method to add a holiday range to the DB
     */
    public void addHolidays(String startDate, String endDate){
        Log.d(tag, "ADDING holidays from " + startDate + " to " +endDate);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBStructure.COLUMN_START_DATE_HOLIDAYS, startDate);
        values.put(DBStructure.COLUMN_END_DATE_HOLIDAYS, endDate);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBStructure.TABLE_NAME_HOLIDAYS, null, values);

        db.close();
    }

    //Method to add holidays but not creating a new one if it already exists
    public void addHolidaysNotOverlap(String startDate, String endDate){

    }

    /**
     * Method to delete a holiday range from the DB.
     */
    private void deleteHolidays(String startDate, String endDate){
        Log.d(tag, "DELETING holidays from " + startDate + " to " +endDate);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        //Name and date
        String selection = " ? >= " + DBStructure.COLUMN_START_DATE_HOLIDAYS + " AND "
                + " ? <= " + DBStructure.COLUMN_END_DATE_HOLIDAYS;
        // Specify arguments in placeholder order.
        String[] selectionArgs = {startDate, endDate};
        // Issue SQL statement.
        int deletedRows = db.delete(DBStructure.TABLE_NAME_HOLIDAYS, selection, selectionArgs);

        db.close();
    }

    /**
     * Method to delete a holiday range from DB
     * If the desired range is inside an existing one, this will delete only the
     * desired days
     */
    public void deleteHolidaysSplitRange(String newStartDateString, String newEndDateString){
        //1- Get holidays overlapping the range
        ArrayList<DialogHolidayModel.ExistingHolidaysDate> prevHolidays = getExistingHolidays(newStartDateString,newEndDateString);
        Log.d(tag, "prevHolidays size: " + prevHolidays.size());

        //Loop for every holiday entry that match
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date oldStartDate, newStartDate, oldEndDate, newEndDate;
        String oldStartString, oldEndString;
        Calendar cal = Calendar.getInstance();
        for(DialogHolidayModel.ExistingHolidaysDate prevHoliday : prevHolidays) {

            try {
                oldStartDate = formatter.parse(prevHoliday.startDate);
                newStartDate = formatter.parse(newStartDateString);
                oldEndDate = formatter.parse(prevHoliday.endDate);
                newEndDate = formatter.parse(newEndDateString);
                oldStartString = formatter.format(oldStartDate); //Getting just the yyyy-MM-dd format
                oldEndString = formatter.format(oldEndDate);

                Log.d(tag, "Old start: " + oldStartString + " new start: " + newStartDateString +
                        "old end: " + oldEndString + "new end: " + newEndDateString);

            /*Case 1- If oldStart after newStart AND newEnd before oldEnd:
                 1) Delete de range
                 2) insert new range from oldStart to newStart-1
                 3) insert new range from newEnd+1 to oldEnd
             */
                if((oldStartDate.before(newStartDate)) && (newEndDate.before(oldEndDate))){
                    this.deleteHolidays(oldStartString, oldEndString);
                    //substracting 1 day to newStart
                    cal.setTime(newStartDate);
                    cal.add(Calendar.DATE, -1);
                    newStartDateString = formatter.format(cal.getTime());
                    this.addHolidays(oldStartString, newStartDateString);
                    //adding 1 day to newend
                    cal.setTime(newEndDate);
                    cal.add(Calendar.DATE, 1);
                    newEndDateString = formatter.format(cal.getTime());
                    this.addHolidays(newEndDateString, oldEndString);
                }else
                if( ((newStartDate.before(oldStartDate)) && (newEndDate.after(oldEndDate))) ||
                        ((newStartDate.equals(oldStartDate))&&(newEndDate.equals(oldEndDate))) ||
                        ((newStartDate.equals(oldStartDate))&&(newEndDate.after(oldEndDate))) ||
                        ((newStartDate.before(oldStartDate))&&(newEndDate.equals(oldEndDate))))
                { /*Case 2- newStart before oldStart AND newEnd after oldEnd: delete the old range
                    Case 2.1- newStart == oldStart AND newEnd == oldEnd: same as case 2
                    Case 2.2- newStart == oldStart AND newEnd after oldEnd: same as case 2
                    Case 2.3- newStart before oldStart AND newEnd == oldEnd: same as case 2
                  */
                    this.deleteHolidays(oldStartString, oldEndString);
                }else
                if (((newStartDate.before(oldStartDate)) && (newEndDate.before(oldEndDate))) ||
                        ((newStartDate.equals(oldStartDate)) && (newEndDate.before(oldEndDate))))
                {
                        /*Case 3- newStart before oldStart AND newEnd before oldEnd
                            1) Delete from oldStart to newEnd
                            2) insert new range from newEnd+1 to oldEnd

                          Case 3.1- newStart and oldStart are the same day
                            Same as case 3
                         */
                    this.deleteHolidays(oldStartString, newEndDateString);
                    //a 1 day to newEnd
                    cal.setTime(newEndDate);
                    cal.add(Calendar.DATE, 1);
                    newEndDateString = formatter.format(cal.getTime());
                    this.addHolidays(newEndDateString, oldEndString);
                } else
                if (((newStartDate.after(oldStartDate)) && (oldEndDate.before(newEndDate))) ||
                        ((newStartDate.after(oldStartDate)) && (newEndDate.equals(oldEndDate))))
                {
                            /*Case 4- newStart after oldStart AND newEnd after oldEnd
                                1) Delete from newStart to oldEnd
                                2) insert new range from oldStart to newStart-1

                              Case 4.1- newEnd and oldend are the same day
                                Same as case 4
                             */
                    this.deleteHolidays(newStartDateString, oldEndString);
                    //substracting 1 day to newStart
                    cal.setTime(newStartDate);
                    cal.add(Calendar.DATE, -1);
                    newStartDateString = formatter.format(cal.getTime());
                    this.addHolidays(oldStartString, newStartDateString);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to get holidays existing that somehow overlap startDate - endDate gap
     */
    private ArrayList<DialogHolidayModel.ExistingHolidaysDate> getExistingHolidays(String startDate, String endDate){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<DialogHolidayModel.ExistingHolidaysDate> holidays = new ArrayList<>();
        Log.d(tag, "Searching holidays between start date " + startDate + " and end date " + endDate);

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DBStructure.COLUMN_START_DATE_HOLIDAYS, //return the id of the holiday period
                DBStructure.COLUMN_END_DATE_HOLIDAYS
        };

        /*
        // Filter results WHERE startDate >= columnStartDate AND endDate <= columnEndDate
        String selection = " ? >= " + DBStructure.COLUMN_START_DATE_HOLIDAYS +
                " AND  ? <= " + DBStructure.COLUMN_END_DATE_HOLIDAYS;
         */
        /*
        String selection = " (" + DBStructure.COLUMN_START_DATE_HOLIDAYS + " BETWEEN ? AND ?) OR (" +
                            DBStructure.COLUMN_END_DATE_HOLIDAYS +  " BETWEEN ? AND ?) OR (" +
                            " ? BETWEEN " + DBStructure.COLUMN_START_DATE_HOLIDAYS + " AND " + DBStructure.COLUMN_END_DATE_HOLIDAYS + ") OR (" +
                            " ? BETWEEN " + DBStructure.COLUMN_START_DATE_HOLIDAYS + " AND " + DBStructure.COLUMN_END_DATE_HOLIDAYS + ");";
        */
        String selection = " (" + DBStructure.COLUMN_START_DATE_HOLIDAYS + " >= ? AND " + DBStructure.COLUMN_START_DATE_HOLIDAYS + " <= ?) OR (" +
                DBStructure.COLUMN_END_DATE_HOLIDAYS + " >= ? AND " + DBStructure.COLUMN_END_DATE_HOLIDAYS + " <= ?) OR (" +
                " ? >= " + DBStructure.COLUMN_START_DATE_HOLIDAYS + " AND ? <= " + DBStructure.COLUMN_END_DATE_HOLIDAYS + ") OR ("+
                " ? >= " + DBStructure.COLUMN_START_DATE_HOLIDAYS + " AND ? <= " + DBStructure.COLUMN_END_DATE_HOLIDAYS + ");";

        String[] selectionArgs = {startDate, endDate, startDate, endDate, startDate, startDate, endDate, endDate};

        try {
            Cursor cursor = db.query(
                    DBStructure.TABLE_NAME_HOLIDAYS,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null              // The sort order
            );

            Log.d(tag, "Tamaño del cursor: " + cursor.getCount());
            if(cursor.moveToFirst()) {
                do {
                    holidays.add(new DialogHolidayModel.ExistingHolidaysDate(cursor.getString(0), cursor.getString(1)));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
            return holidays;
        }
    }

    //Inner class used to operate when adding and deleting holidays.
    //It saves the dates of start and end of existing holidays to
    //compare them.
    private class ExistingHolidaysDate {
        public String startDate;
        public String endDate;
        public ExistingHolidaysDate(String startDate, String endDate){
            this.startDate = startDate;
            this.endDate = endDate;
        }
        public void setStartDate(String start){
            startDate=start;
        }
        public void setEndDate(String end){
            endDate=end;
        }
    }


    /**
     * Method to check if a date is in a holiday period
     * @param date Date to check if it's in a holiday period, format yyyy-MM-dd
     * @return true if it's holiday, false of it's not
     */
    public boolean searchHolidays(String date){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean isHoliday = false;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DBStructure.COLUMN_START_DATE_HOLIDAYS, //return the id of the holiday period
                DBStructure.COLUMN_END_DATE_HOLIDAYS
        };

        // Filter results WHERE date BETWEEN StartDate and EndDate
        String selection = " ? BETWEEN " + DBStructure.COLUMN_START_DATE_HOLIDAYS +
                " AND " + DBStructure.COLUMN_END_DATE_HOLIDAYS;
        String[] selectionArgs = {date};

        try {
            Cursor cursor = db.query(
                    DBStructure.TABLE_NAME_HOLIDAYS,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null              // The sort order
            );

            if(cursor.moveToFirst()) {
                //If a register exists is because it's holiday period
                isHoliday = true;

            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
            return isHoliday;
        }
    }



    /*
     * Help methods to calculate dates
     */

    //Method to calculate if a String date is between 2 others
    public boolean dateIsBetween(String date1, String date2, String dateToCompare){
        boolean isBetween = false;
        Log.d(tag, "start " + date1 + " between "+dateToCompare+" end "+date2);
        if((date1.compareTo(dateToCompare) < 0) && (date2.compareTo(dateToCompare)>0)){
            isBetween = true;
        }
        //Edges are included too
        if((date1.equals(dateToCompare)) || ((date2.equals(dateToCompare)))){
            isBetween = true;
        }
        return isBetween;
    }

    //get number of days in current month
    public int getDays(int year, int month){
        Calendar myCal = new GregorianCalendar(year, month, 1);
        int numberOfDays = myCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return numberOfDays;
    }

    //get number of weeks in current month
    public int getWeeks(int year, int month){
        Calendar myCal = new GregorianCalendar(year, month, 1);
        myCal.setMinimalDaysInFirstWeek(1);
        int weeks = myCal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        return weeks;
    }

    //get current year
    public int getYear(){
        Calendar myCal = Calendar.getInstance();
        int year = myCal.get(Calendar.YEAR);
        return year;
    }

    //get current month
    public int getMonth(){
        Calendar myCal = Calendar.getInstance();
        int month = myCal.get(Calendar.MONTH);
        return month;
    }

    //Get the first day of a week
    public int getDayOfWeek(int year, int month){
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
