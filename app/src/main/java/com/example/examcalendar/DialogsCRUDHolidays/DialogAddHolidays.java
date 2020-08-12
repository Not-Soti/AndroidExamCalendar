package com.example.examcalendar.DialogsCRUDHolidays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.examcalendar.DialogsCRUDExams.DialogExamModel;
import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogAddHolidays extends Activity {
    private Button accept;
    private EditText startDayEditText, endDayEditText, startMonthEditText, endMonthEditText, startYearEditText, endYearEditText;
    private DialogHolidayModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_holiday);

        model = new DialogHolidayModel(this);
        Bundle bundle = getIntent().getExtras(); //Get info from previous Activity

        accept = (Button) findViewById(R.id.buttonAddHoliday);
        startDayEditText = (EditText) findViewById(R.id.textAddHolidayStartDay);
        endDayEditText = (EditText) findViewById(R.id.textAddHolidayEndDay);
        startMonthEditText = (EditText) findViewById(R.id.textAddHolidayStartMonth);
        endMonthEditText = (EditText) findViewById(R.id.textAddHolidayEndMonth);
        startYearEditText = (EditText) findViewById(R.id.textAddHolidayStartYear);
        endYearEditText = (EditText) findViewById(R.id.textAddHolidayEndYear);

        //Paint bg Color
        CommonActivityThings.paintBackground(this);

        String dayAux = bundle.getString("day");
        if(dayAux!=null){
            startDayEditText.setText(dayAux);
            endDayEditText.setText(dayAux);
        }
        startYearEditText.setText(bundle.getString("year"));
        startMonthEditText.setText(bundle.getString("month"));
        endYearEditText.setText(bundle.getString("year"));
        endMonthEditText.setText(bundle.getString("month"));

        //When writing on the start dates, same info is
        //written on the end dates to make the app faster to use
        //in case there's only 1 day for example
        startDayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                endDayEditText.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        startMonthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                endMonthEditText.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        startYearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                endYearEditText.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHolidays();
            }
        });
    }

    protected void addHolidays(){
        //Get the values
        String startDay = startDayEditText.getText().toString();
        String endDay = endDayEditText.getText().toString();
        int startMonthAux = Integer.parseInt(startMonthEditText.getText().toString());
        String startMonth = String.valueOf(startMonthAux);
        int endMonthAux = Integer.parseInt(endMonthEditText.getText().toString());
        String endMonth = String.valueOf(endMonthAux);
        String startYear = startYearEditText.getText().toString();
        String endYear = endYearEditText.getText().toString();

        //Checking for void textfields
        if(TextUtils.isEmpty(startDay)){
            startDayEditText.setError("Introduce un día"); return;
        }
        if(TextUtils.isEmpty(endDay)){
            endDayEditText.setError("Introduce un día"); return;
        }
        if(TextUtils.isEmpty(startMonth)){
            startDayEditText.setError("Introduce un mes"); return;
        }
        if(TextUtils.isEmpty(endMonth)){
            endMonthEditText.setError("Introduce un día"); return;
        }if(TextUtils.isEmpty(startYear)){
            startDayEditText.setError("Introduce un año"); return;
        }
        if(TextUtils.isEmpty(endYear)){
            startDayEditText.setError("Introduce un año"); return;
        }

        //Date strings of format yyyy-MM-dd
        String startDateAux = new String(startYear+"-"+startMonth+"-"+startDay);
        String endDateAux = new String(endYear+"-"+endMonth+"-"+endDay);
        String startDate = null, endDate=null;
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

        try{
            startDate = newFormat.format(oldFormat.parse(startDateAux));
            endDate = newFormat.format(oldFormat.parse(endDateAux));

            //Check if startDate < endDate
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            if(d2.compareTo(d1)<0){
                Toast.makeText(this, "La fecha de fin no puede ser anterior a la de inicio", Toast.LENGTH_LONG);
                return;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }

        model.addHolidays(startDate, endDate);
        /*TODO model.addHolidaysNotOverlap(startDate, endDate); para no sobreescribir varios periodos de vacaciones
            Ej: existen del 15 al 28 y se quieren añadir del 10 al 17 -> que se añada solo del 10 al 14
         */

        //Go back to MonthActivity
        startActivity(new Intent(this, MonthActivityController.class));
    }
}
