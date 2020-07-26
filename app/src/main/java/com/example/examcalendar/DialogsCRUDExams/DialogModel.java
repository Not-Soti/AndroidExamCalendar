package com.example.examcalendar.DialogsCRUDExams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.examcalendar.DataBase.DBHelper;
import com.example.examcalendar.DataBase.DBStructure;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Class made to act as the model for adding/deleting exams/holidays dialogs
 */
public class DialogModel {

    DBHelper dbHelper;
    String tag = "DialogDBHelper";

    public DialogModel(Context con){
        this.dbHelper = new DBHelper(con);
    }

    /**
     * Method to get exams existing in a determined day
     */
    public ArrayList<String> getExistingExams(String date){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> exams = new ArrayList<>(3);

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DBStructure.COLUMN_NAME_EXAMS //name
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DBStructure.COLUMN_DATE_EXAMS + "= ?";
        String[] selectionArgs = {date};

        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";
        try {
            Cursor cursor = db.query(
                    DBStructure.TABLE_NAME_EXAMS,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null              // The sort order
            );

            //Adding results to the exams list
            if(cursor.moveToFirst()) {
                do {
                    exams.add(cursor.getString(0));
                    Log.println(Log.DEBUG,"CURSOR CAP",String.valueOf(exams.size()));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            //exam=null;
            exams=null;

        }finally {
            db.close();
            return exams;
        }
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
        ArrayList<ExistingHolidaysDate> prevHolidays = getExistingHolidays(newStartDateString,newEndDateString);
        Log.d(tag, "prevHolidays size: " + prevHolidays.size());

        //Loop for every holiday entry that match
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date oldStartDate, newStartDate, oldEndDate, newEndDate;
        String oldStartString, oldEndString;
        Calendar cal = Calendar.getInstance();
        for(ExistingHolidaysDate prevHoliday : prevHolidays) {

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
                        /*TODO seguir añadiendo casos donde oldStart == newStart y oldEnd == newEnd
                         */


            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to add a exam to the DB
     */

    public void addExam(String name, String date){
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBStructure.COLUMN_NAME_EXAMS,name);
        values.put(DBStructure.COLUMN_DATE_EXAMS,date);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBStructure.TABLE_NAME_EXAMS, null, values);

        db.close();
    }

    /**
     * Method to delete a exam from the DB
     */
    public void deleteExam(String name, String date){
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        //Name and date
        String selection = DBStructure.COLUMN_NAME_EXAMS + " LIKE ? AND "
                + DBStructure.COLUMN_DATE_EXAMS + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {name, date};
        // Issue SQL statement.
        int deletedRows = db.delete(DBStructure.TABLE_NAME_EXAMS, selection, selectionArgs);

        db.close();

    }

    /**
     * Method to get holidays existing that somehow overlap startDate - endDate gap
     */
    private ArrayList<ExistingHolidaysDate> getExistingHolidays(String startDate, String endDate){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<ExistingHolidaysDate> holidays = new ArrayList<>();
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
                    holidays.add(new ExistingHolidaysDate(cursor.getString(0), cursor.getString(1)));
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

    /**
     * Method to edit an existing exam
     */
    public void editExam(String newName, String newDate, String oldName, String oldDate){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        String title = "MyNewTitle";
        ContentValues values = new ContentValues();
        values.put(DBStructure.COLUMN_NAME_EXAMS, newName);
        values.put(DBStructure.COLUMN_DATE_EXAMS, newDate);

        // Which row to update, based on the title
        String selection = DBStructure.COLUMN_NAME_EXAMS + " LIKE ? AND " +
                DBStructure.COLUMN_DATE_EXAMS + " = ?;";
        String[] selectionArgs = {oldName, oldDate};

        int count = db.update(
                DBStructure.TABLE_NAME_EXAMS,
                values,
                selection,
                selectionArgs);
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
}