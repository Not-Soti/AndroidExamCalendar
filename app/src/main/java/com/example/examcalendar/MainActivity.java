package com.example.examcalendar;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    //Starts the month activity
    public void startMonthActivity(View view){
        Intent i = new Intent(this, MonthActivityController.class);

        startActivity(i);
    }

}
