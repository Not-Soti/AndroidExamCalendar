package com.example.examcalendar.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.examcalendar.ConfigurationActivity.ConfigurationActivityController;
import com.example.examcalendar.HelpClasses.CommonActivityThings;
import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the bg color
        CommonActivityThings.paintBackground(this);
    }

    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }

    //Starts the month activity
    public void startMonthActivity(View view){
        Intent i = new Intent(this, MonthActivityController.class);
        startActivity(i);
    }

    //Se abre la actividad de configuracion
    public void startConfigurationActivity(View view){
        Intent i = new Intent(this, ConfigurationActivityController.class);
        startActivity(i);
    }

}
