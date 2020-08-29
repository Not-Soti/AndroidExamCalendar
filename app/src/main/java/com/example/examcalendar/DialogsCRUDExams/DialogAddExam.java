package com.example.examcalendar.DialogsCRUDExams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.R;

public class DialogAddExam extends Activity {
    private Button accept;
    //private EditText dayEditText,monthEditText,yearEditText;
    private TextView dateTextView;
    private EditText nameEditText;
    private String day, month, year;

    DialogExamModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_exam);

        model = new DialogExamModel(this);
        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        accept = (Button) findViewById(R.id.AddExamDia_AcceptButton);
        //dayEditText = (EditText) findViewById(R.id.textAddExamDay);
        //monthEditText = (EditText) findViewById(R.id.textAddExamMonth);
        //yearEditText = (EditText) findViewById(R.id.textAddExamYear);
        dateTextView = findViewById(R.id.AddExamDia_DateTextView);
        nameEditText = (EditText) findViewById(R.id.AddExamDia_NameTextView);

        this.setTitle("Añadir examen");

        day = bundle.getString("day");
        month = bundle.getString("month");
        year = bundle.getString("year");


        //Paint bg Color
        CommonActivityThings.paintBackground(this);

        /*
        String dayAux = bundle.getString("day");
        if(dayAux!=null){
            //dayEditText.setText(dayAux);
        }
        monthEditText.setText(bundle.getString("month"));
        yearEditText.setText(bundle.getString("year"));
        */

        dateTextView.setText(model.getFormattedDate(day,month,year, "dd-MM-yyyy"));
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
        /*
        String day = dayEditText.getText().toString();
        int monthAux = Integer.parseInt(monthEditText.getText().toString());
        String month = String.valueOf(monthAux);
        String year = yearEditText.getText().toString();
        */

        String name = nameEditText.getText().toString();

        /*
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
        */

        if(TextUtils.isEmpty(name)){
            nameEditText.setError("Introduce un nombre"); return;
        }


        String examDate = model.getFormattedDate(day, month, year, "yyyy-MM-dd");

        //Adds the exam to the db
        model.addExam(name, examDate);

        //Go back to MonthActivity
        Intent i = new Intent(this, MonthActivityController.class);
        i.putExtra("month", month);
        i.putExtra("year", year);
        startActivity(i);
    }


}
