package com.example.examcalendar.MonthActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.examcalendar.DataBase.DBHelper;
import com.example.examcalendar.DataBase.DBStructure;
import com.example.examcalendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DialogAddExam extends Activity {
    private Button accept;
    private EditText dayEditText,monthEditText,yearEditText,nameEditText;


    DialogDBHelper model;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_exam);

        model = new DialogDBHelper(this);
        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        accept = (Button) findViewById(R.id.buttonAcceptAddExam);
        dayEditText = (EditText) findViewById(R.id.textAddExamDay);
        monthEditText = (EditText) findViewById(R.id.textAddExamMonth);
        yearEditText = (EditText) findViewById(R.id.textAddExamYear);
        nameEditText = (EditText) findViewById(R.id.textAddExamName);

        String dayAux = bundle.getString("day");
        if(dayAux!=null){
            dayEditText.setText(dayAux);
        }
        monthEditText.setText(bundle.getString("month"));
        yearEditText.setText(bundle.getString("year"));

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptExam();
            }
        });
    }

    /**
     * Connect with the database and add the exam
     */
    private void acceptExam(){
        //Get the values
        String day = dayEditText.getText().toString();
        int monthAux = Integer.parseInt(monthEditText.getText().toString());
        String month = String.valueOf(monthAux);
        String year = yearEditText.getText().toString();
        String name = nameEditText.getText().toString();

        //Checking for empty fields
        if(TextUtils.isEmpty(day)){
            dayEditText.setError("Introduce un dia"); return;
        }
        if(TextUtils.isEmpty(month)){
            monthEditText.setError("Introduce un mes"); return;
        }
        if(TextUtils.isEmpty(year)){
            yearEditText.setError("Introduce un año"); return;
        }
        if(TextUtils.isEmpty(name)){
            nameEditText.setError("Introduce un nombre"); return;
        }

        //String date onf yyyy-MM-dd format
        String examDateAux = new String(year+"-"+month+"-"+day);
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        String examDate = null;
        try{
            examDate = newFormat.format(oldFormat.parse(examDateAux));
        } catch (ParseException e){
            e.printStackTrace();
        }

        //Adds the exam to the db
        model.addExam(name, examDate);

        //Go back to MonthActivity
        startActivity(new Intent(this, MonthActivityController.class));
    }
}
