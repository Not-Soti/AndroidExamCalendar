package com.example.examcalendar.DialogsCRUDExams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.examcalendar.DialogsCRUDHolidays.ActivityAddHoliday;
import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.R;

import java.util.List;

public class DialogDeleteExam extends Activity {
    private Button accept;
    //private EditText dayEditText,monthEditText,yearEditText,nameEditText;
    private ListView examListView;
    private static String examToDelete; //used to get the exam selected from the ListView
    private TextView dateTextView;
    private String day, month, year;

    DialogExamModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_exam);

        examToDelete = new String();
        model = new DialogExamModel(this);
        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        accept = findViewById(R.id.buttonAcceptDeleteExam);
        //dayEditText = findViewById(R.id.textDeleteExamDay);
        //monthEditText =  findViewById(R.id.textDeleteExamMonth);
       // yearEditText = findViewById(R.id.textDeleteExamYear);
        //nameEditText = (EditText) findViewById(R.id.textDeleteExamName);
        dateTextView = findViewById(R.id.DelExamDia_DateTextView);
        examListView = findViewById(R.id.listDeleteExamNames);

        this.setTitle("Eliminar examen");

        day = bundle.getString("day");
        month = bundle.getString("month");
        year = bundle.getString("year");

        //Paint bg Color
        CommonActivityThings.paintBackground(this);

        dateTextView.setText(model.getFormattedDate(day,month,year, "dd-MM-yyyy"));
        this.setExamNamesOnList();

        examListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedExam = examListView.getItemAtPosition(i).toString();
                examToDelete = selectedExam;

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
                deleteExam();
            }
        });

        /*
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
        }*/
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

    protected void deleteExam(){
        String name = examToDelete;

        /*
        //Checking for empty fields
        if(TextUtils.isEmpty(day)){
            dayEditText.setError("Introduce un día"); return;
        }
        if(TextUtils.isEmpty(month)){
            monthEditText.setError("Introduce un mes"); return;
        }
        if(TextUtils.isEmpty(year)){
            yearEditText.setError("Introduce un año"); return;
        }
        */
        String examDate = model.getFormattedDate(day,month,year,"yyyy-MM-dd");
        model.deleteExam(name, examDate);

        //Volver a la actividad anterior
        Intent i = new Intent(this, MonthActivityController.class);
        i.putExtra("month", month);
        i.putExtra("year", year);
        startActivity(i);
    }
}
