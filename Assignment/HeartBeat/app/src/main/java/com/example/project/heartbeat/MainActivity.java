package com.example.project.heartbeat;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.Series;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void gotoNext(View view) {
        Intent intent = new Intent(this, InitialActivity.class);
        startActivity(intent);
        // Do something in response to button
    }


}
