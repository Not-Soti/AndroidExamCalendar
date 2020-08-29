package com.example.examcalendar.DialogsCRUDExams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DialogEditExam extends Activity {

    private Button accept;
    private EditText dayEditText,monthEditText,yearEditText,nameEditText;
    private ListView examListView;
    private static String examToEdit, oldDate; //used to get the exam selected from the ListView
    private String day, month, year;

    DialogExamModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_exam);

        examToEdit = new String();
        oldDate = new String();
        model = new DialogExamModel(this);
        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        accept = findViewById(R.id.EditExamDia_AcceptButton);
        dayEditText = findViewById(R.id.EditExamDia_dayEditText);
        monthEditText =  findViewById(R.id.EditExamDia_monthEditText);
        yearEditText = findViewById(R.id.EditExamDia_yearEditText);
        nameEditText = findViewById(R.id.EditExamDia_examNameEditText);
        examListView = findViewById(R.id.EditExamDia_examNameList);

        //Paint bg Color
        CommonActivityThings.paintBackground(this);

        day = bundle.getString("day");
        month = bundle.getString("month");
        year = bundle.getString("year");
        if(day!=null){
            dayEditText.setText(day);
        }
        monthEditText.setText(month);
        yearEditText.setText(year);
        this.setExamNamesOnList();

        examListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedExam = examListView.getItemAtPosition(i).toString();
                examToEdit = selectedExam;
                oldDate = model.getFormattedDate(day,month,year,"yyyy-MM-dd");
                nameEditText.setText(examToEdit);

                //Change color of selected listItem, and set the others to white
                for(int j = 0; j<examListView.getChildCount();j++){
                    if (i == j) {
                        examListView.getChildAt(j).setBackgroundColor(Color.LTGRAY);
                    } else {
                        examListView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                    }
                }

            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editExam();
            }
        });

        //Change the date related fields listeners to search exams and
        //set them on the ListView whenever the date is changed
        EditText[] views = {dayEditText, monthEditText, yearEditText};
        for(int i = 0; i<views.length; i++){
            views[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setExamNamesOnList();
                }
            });
        }
    }

    private void setExamNamesOnList(){
        //Getting existing exams
        List<String> exams = model.getExistingExams(model.getFormattedDate(day,month,year,"yyyy-MM-dd"));

        //get if darkMode is active
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dmActive = preferences.getBoolean("DarkModeActive", false);

        ArrayAdapter<String> adapter;
        //Adding them to the ListView
        if(dmActive){
            adapter = new ArrayAdapter<>(this, R.layout.listitemlayout_darkmode, exams);
        }else {
            adapter = new ArrayAdapter<>(this, R.layout.listitemlayout_lightmode, exams);
        }
        examListView.setAdapter(adapter);
    }

    protected void editExam(){
        //Get the values
        String newDay = dayEditText.getText().toString();
        int monthAux = Integer.parseInt(monthEditText.getText().toString());
        String newMonth = String.valueOf(monthAux);
        String newYear = yearEditText.getText().toString();
        String newName = nameEditText.getText().toString();


        //Checking for empty fields
        if(TextUtils.isEmpty(newDay)){
            dayEditText.setError("Introduce un día"); return;
        }
        if(TextUtils.isEmpty(newMonth)){
            monthEditText.setError("Introduce un mes"); return;
        }
        if(TextUtils.isEmpty(newYear)){
            yearEditText.setError("Introduce un año"); return;
        }

        if(TextUtils.isEmpty(newName)){
            nameEditText.setError("Introduce un nombre"); return;
        }

        String examDate = model.getFormattedDate(newDay,newMonth,newYear,"yyyy-MM-dd");
        model.editExam(newName, examDate, examToEdit, oldDate);

        //Go back to MonthActivity
        Intent i = new Intent(this, MonthActivityController.class);
        i.putExtra("month", month);
        i.putExtra("year", year);
        startActivity(i);
    }
}
