package com.example.examcalendar.DialogsCRUDExams;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DialogDeleteExam extends Activity {
    private Button accept;
    private EditText dayEditText,monthEditText,yearEditText,nameEditText;
    private ListView examListView;
    private static String examToDelete; //used to get the exam selected from the ListView

    DialogModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_exam);

        examToDelete = new String();
        model = new DialogModel(this);
        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        accept = findViewById(R.id.buttonAcceptDeleteExam);
        dayEditText = findViewById(R.id.textDeleteExamDay);
        monthEditText =  findViewById(R.id.textDeleteExamMonth);
        yearEditText = findViewById(R.id.textDeleteExamYear);
        //nameEditText = (EditText) findViewById(R.id.textDeleteExamName);
        examListView = findViewById(R.id.listDeleteExamNames);

        String dayAux = bundle.getString("day");
        if(dayAux!=null){
            dayEditText.setText(dayAux);
        }
        monthEditText.setText(bundle.getString("month"));
        yearEditText.setText(bundle.getString("year"));
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
        List<String> exams = model.getExistingExams(getAndFormatDate());
        //Adding them to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exams);
        examListView.setAdapter(adapter);
    }

    /**
     * Method used to compound the date from the view fields in a yyyy-MM-dd format
     * @return
     */
    private String getAndFormatDate(){
        String date = new String();

        //Get the values
        String day = dayEditText.getText().toString();
        int monthAux = Integer.parseInt(monthEditText.getText().toString());
        String month = String.valueOf(monthAux);
        String year = yearEditText.getText().toString();

        //String date onf yyyy-MM-dd format
        String examDateAux = new String(year+"-"+month+"-"+day);
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            date = newFormat.format(oldFormat.parse(examDateAux));
        } catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    protected void deleteExam(){
        //Get the values
        String day = dayEditText.getText().toString();
        int monthAux = Integer.parseInt(monthEditText.getText().toString());
        String month = String.valueOf(monthAux);
        String year = yearEditText.getText().toString();
        //String name = nameEditText.getText().toString();
        String name = examToDelete;

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
        /*
        if(TextUtils.isEmpty(name)){
            nameEditText.setError("Introduce un nombre"); return;
        }
        */
        String examDate = getAndFormatDate();
        model.deleteExam(name, examDate);

        //Volver a la actividad anterior
        startActivity(new Intent(this, MonthActivityController.class));
    }
}
