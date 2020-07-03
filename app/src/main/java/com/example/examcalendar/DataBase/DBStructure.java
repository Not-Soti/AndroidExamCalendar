package com.example.examcalendar.DataBase;

public class DBStructure {

    private DBStructure(){}

    //TABLE exams
    public static final String TABLE_NAME_EXAMS = "Exams";
    public static final String COLUMN_ID_EXAMS = "Id";
    public static final String COLUMN_DATE_EXAMS = "Date";
    public static final String COLUMN_NAME_EXAMS = "Name";

    public static final String SQL_CREATE_EXAMS =
            "CREATE TABLE " + DBStructure.TABLE_NAME_EXAMS + " ( " +
                DBStructure.COLUMN_ID_EXAMS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBStructure.COLUMN_DATE_EXAMS + " DATE, " +
                    DBStructure.COLUMN_NAME_EXAMS + " TEXT)";

    public static final String SQL_DELETE_EXAMS =
            "DROP TABLE IF EXISTS " + DBStructure.TABLE_NAME_EXAMS;


    //TABLE holidays
    public static final String TABLE_NAME_HOLIDAYS = "Holidays";
    public static final String COLUMN_ID_HOLIDAYS = "Id";
    public static final String COLUMN_START_DATE_HOLIDAYS = "StartDate";
    public static final String COLUMN_END_DATE_HOLIDAYS = "EndDate";

    public static final String SQL_CREATE_HOLIDAYS =
            "CREATE TABLE " + DBStructure.TABLE_NAME_HOLIDAYS + " ( " +
                    DBStructure.COLUMN_ID_HOLIDAYS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBStructure.COLUMN_START_DATE_HOLIDAYS + " DATE, " +
                    DBStructure.COLUMN_END_DATE_HOLIDAYS + " DATE)";

    public static final String SQL_DELETE_HOLIDAYS =
            "DROP TABLE IF EXISTS " + DBStructure.TABLE_NAME_HOLIDAYS;

}
