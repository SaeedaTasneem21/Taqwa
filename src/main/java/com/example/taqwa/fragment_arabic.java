package com.example.taqwa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class fragment_arabic extends Fragment {
    TextView textView;



    public fragment_arabic() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arabic, container, false);


        String text=getArguments().getString("First");
        textView= view.findViewById(R.id.imge);
        textView.setText(text);
        return view;
    }
}