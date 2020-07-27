package com.example.examcalendar.MainActivity;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.examcalendar.ConfigurationActivity.ConfigurationActivityController;
import com.example.examcalendar.MonthActivity.MonthActivityController;
import com.example.examcalendar.R;
import com.example.examcalendar.WeekActivity.WeekActivityController;


public class MainActivity extends AppCompatActivity {

    private static int numeroTroll = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    //Al pinchar en el boton prohibido, se muestran 2 mensajes y luego se abre
    //la foto de trolleo
    public void startWeekActivity(View view){
        /*
        switch (numeroTroll){
            case 0:
                Toast.makeText(this, "AH WACHON TE DIJE QUE NO PULSASES", Toast.LENGTH_LONG).show();
                numeroTroll++;
                break;
            case 1:
                Toast.makeText(this, "PERO PA QUE PULSAS OTRA VEZ ERES TONTITO", Toast.LENGTH_LONG).show();
                numeroTroll++;
                break;
            case 2:
                Toast.makeText(this, ":facepalm:", Toast.LENGTH_SHORT).show();
                numeroTroll=0;
                startActivity(new Intent(this, WeekActivityController.class));
                break;
            default:
                numeroTroll=0;
        }*/
    }

    //Se abre la actividad de configuracion
    public void startConfigurationActivity(View view){
        Intent i = new Intent(this, ConfigurationActivityController.class);
        startActivity(i);
    }

}
