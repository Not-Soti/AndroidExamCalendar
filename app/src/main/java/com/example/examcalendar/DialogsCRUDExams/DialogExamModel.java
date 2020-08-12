package com.example.examcalendar.DialogsCRUDExams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.examcalendar.DataBase.DBHelper;
import com.example.examcalendar.DataBase.DBStructure;

import java.util.ArrayList;



/**
 * Class made to act as the model for adding/deleting exams/holidays dialogs
 */
public class DialogExamModel {

    DBHelper dbHelper;
    String tag = "DialogDBHelper";

    public DialogExamModel(Context con){
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


}