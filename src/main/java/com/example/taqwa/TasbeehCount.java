package com.example.taqwa;

import static com.example.taqwa.R.id.add1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TasbeehCount extends AppCompatActivity {
ImageButton reset;
ImageButton addbt;
int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbeeh_count);

        final TextView myTextv = (TextView) findViewById(R.id.count1);
        addbt= findViewById(R.id.add1);
        reset = findViewById(R.id.rzt1);
        addbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                myTextv.setText(" "+ count);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=0;
                if(count<0){
                    count=0;
                }
                myTextv.setText(" "+ count);
            }
        });

    }
}