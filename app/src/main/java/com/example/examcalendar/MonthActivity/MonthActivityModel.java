package com.example.examcalendar.MonthActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.examcalendar.DataBase.DBHelper;
import com.example.examcalendar.DataBase.DBStructure;

import java.util.ArrayList;

public class MonthActivityModel {

    private static DBHelper dbHelper;
    private Context context;

    private MonthActivityModel(){}
    public MonthActivityModel(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    /**
     * Method to search for an exam on a specific date
     * @param date Date to check if an exam exists in
     *             the format yyyy-MM-dd
     */
    public ArrayList<String> searchExam(String date){
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
}
