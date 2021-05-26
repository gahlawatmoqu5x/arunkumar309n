package com.example.project.heartbeat;

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

public class InitialActivity extends AppCompatActivity {
    private Button startButton;
    private Button stopButton;
    private GraphView graph;
    private TextView textField;
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private Queue<Double> queue = new LinkedList<Double>();
    private static Random random = new Random();
    private boolean pause = true;
    final int N = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialactivity);
        startButton = (Button) findViewById(R.id.button);
        stopButton = (Button) findViewById(R.id.button2);
        textField = (TextView) findViewById(R.id.textView);
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setMinX(0);
        viewport.setMaxX(N);
        viewport.setScrollable(false);
    }

    @Override
    protected void onResume(){
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < N; i++) {
                    queue.add(random.nextDouble()*100d);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run (){
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        Object data = queue.poll();
        if (data != null && pause) {
            series.appendData(new DataPoint(lastX++, (double) data), true, 10);
            textField.setText(Double.toString((Double) data));
        }
    }
    public void startFunc(View view){
        textField.setText(R.string.startPressed);
        pause = true;
    }
    public void stopFunc(View view){
        textField.setText(R.string.stopPressed);
        pause = false;
    }
}
