package com.example.taqwa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

import org.arabeyes.prayertime.PrayerTime;

public class MainActivity extends AppCompatActivity {
    public static final String UPDATE_VIEWS = "com.example.taqwa.UPDATE";
    GridLayout maingrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maingrid=(GridLayout) findViewById(R.id.gridL);
        setSinleEvent(maingrid);
    }

    private void setSinleEvent(GridLayout maingrid) {

   for(int i=0; i< maingrid.getChildCount();i++){
       CardView cardv = (CardView) maingrid.getChildAt(i);
       final int finall=i;
       cardv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(finall == 0) {
                   Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                   startActivity(intent);
               }
               else if(finall == 1){
                   Intent intent = new Intent(MainActivity.this,TasbeehCount.class);
                   startActivity(intent);
               }
               else if(finall == 2){
                   Intent intent = new Intent(MainActivity.this,QiblaCompass.class);
                   startActivity(intent);
               }
               else if(finall == 3){
                   Intent intent = new Intent(MainActivity.this,hadith.class);
                   startActivity(intent);
               }
           }
       });
   }

    }
}