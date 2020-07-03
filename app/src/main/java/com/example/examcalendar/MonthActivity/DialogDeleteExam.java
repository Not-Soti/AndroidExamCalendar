package com.example.examcalendar.MonthActivity;

import android.app.Activity;
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

public class DialogDeleteExam extends Activity {
    private Button accept;
    private EditText dayEditText,monthEditText,yearEditText,nameEditText;

    DialogDBHelper model;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_exam);

        model = new DialogDBHelper(this);
        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        accept = (Button) findViewById(R.id.buttonAcceptDeleteExam);
        dayEditText = (EditText) findViewById(R.id.textDeleteExamDay);
        monthEditText = (EditText) findViewById(R.id.textDeleteExamMonth);
        yearEditText = (EditText) findViewById(R.id.textDeleteExamYear);
        nameEditText = (EditText) findViewById(R.id.textDeleteExamName);

        monthEditText.setText(bundle.getString("month"));
        yearEditText.setText(bundle.getString("year"));

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteExam();
            }
        });
    }

    protected void deleteExam(){
        //Get the values
        String day = dayEditText.getText().toString();
        int monthAux = Integer.parseInt(monthEditText.getText().toString());
        String month = String.valueOf(monthAux);
        String year = yearEditText.getText().toString();
        String name = nameEditText.getText().toString();

        //Checking for empty fields
        if(TextUtils.isEmpty(day)){
            dayEditText.setError("Introduce un año"); return;
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

        model.deleteExam(name, examDate);

        //Volver a la actividad anterior
        startActivity(new Intent(this, MonthActivityController.class));
    }
}
