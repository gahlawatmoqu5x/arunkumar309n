package com.example.project.heartbeat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.Series;

import org.w3c.dom.Text;

import java.io.File;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;


import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Toast;

public class InitialActivity extends AppCompatActivity implements SensorEventListener {


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // dummey method
    }

    // 3/6/2016 start change
    SQLiteDatabase AccelorometerDB = null;
    private Sensor mySensor;
    private SensorManager SM;
    int yaxisValue;
    int xaxisValue;
    int zaxisValue;
    EditText nameEditText;
    //editText3
    EditText ageEditText;
    String message="defaultMessage";
    String ageMessage="defaultAge";
    // 3/6/2016 end change
    private Button startButton;
    private Button stopButton;
    private GraphView graph;
    private TextView textField;
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private Queue<Double> queue = new LinkedList<Double>();
    private static Random random = new Random();
    private boolean pause = false;
    final int N = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialactivity);
        // 3/6/2016 start change
        Intent intent = getIntent();
        message = intent.getStringExtra("message");
        message= message.toString();
        ageMessage = intent.getStringExtra("messageAge");
        ageMessage= ageMessage.toString();

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        nameEditText= (EditText) findViewById(R.id.editText4);
        ageEditText = (EditText) findViewById(R.id.editText7);
        // 3/6/2016 end change
        startButton = (Button) findViewById(R.id.button);
        stopButton = (Button) findViewById(R.id.button2);
        textField = (TextView) findViewById(R.id.textView);
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(80);
        viewport.setMinX(0);
        viewport.setMaxX(N);
        viewport.setScrollable(false);
        nameEditText.setText(message);
        ageEditText.setText(ageMessage);
    }
    // 3/6/2016 start change
    @Override
    public void onSensorChanged(SensorEvent event) {
         xaxisValue = (int) event.values[0]+ 30;
        yaxisValue = ((int) event.values[1] + (int) event.values[2])+ 30;
        zaxisValue = (int) event.values[2]+ 30;

        // normalizing by adding +30
        // we have to discuss what to do with the xaxisValue because I am not using it

    }

    // 3/6/2016 end change
    @Override
    protected void onResume(){
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < N; i++) {
                    // 3/6/2016 start change
                    //queue.add(random.nextDouble()*100d);
                    queue.add((double) yaxisValue);
                    // 3/6/2016 end change
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
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
        String contactName = nameEditText.getText().toString();
        // here, we choose to display max 10 points on the viewport and we scroll to end
        Object data = queue.poll();
        if (data != null && pause) {
            series.appendData(new DataPoint(lastX++, (double) data), true, 10);
            textField.setText(Double.toString((Double) data));

            AccelorometerDB.execSQL("INSERT INTO MyAccTable1 (name, xaxis, yaxis, zaxis) VALUES ('" +
                    contactName + "', '" + xaxisValue + "', '" + yaxisValue + "', '" + zaxisValue + "');");
        }
    }
    public void startFunc(View view){
       // textField.setText(R.string.startPressed);
        pause = true;

        // 3/6/2016 start change

        try{

            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            AccelorometerDB = this.openOrCreateDatabase("MyAccDB.db", MODE_PRIVATE, null);

            // Execute an SQL statement that isn't select
            AccelorometerDB.execSQL("CREATE TABLE IF NOT EXISTS MyAccTable1 " +
                    "(id integer primary key, name TEXT, xaxis INT, yaxis INT, zaxis INT);");

            // The database on the file system
            File database = getApplicationContext().getDatabasePath("MyAccDB.db");

            // Check if the database exists
            if (database.exists()) {
                Toast.makeText(this, "Accele Database Created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Accele Database Missing", Toast.LENGTH_SHORT).show();
            }

            //String contactName = nameEditText.getText().toString();
            //int xaxisValue1 = xaxisValue;
            //int yaxisValue1 = yaxisValue ;
            //int zaxisValue1 = zaxisValue ;
            // Execute SQL statement to insert new data
           // AccelorometerDB.execSQL("INSERT INTO MyAccTable1 (name, xaxis, yaxis, zaxis) VALUES ('" +
             //       contactName + "', '" + xaxisValue + "', '" + yaxisValue + "', '" + zaxisValue + "');");

           // Cursor cursor = contactsDB.rawQuery("SELECT * FROM contacts", null);


        }

        catch(Exception e){

            Log.e("CONTACTS ERROR", "Error Creating Accele Database");

        }

        // 3/6/2016 end change




    }
    public void stopFunc(View view){
      //  textField.setText(R.string.stopPressed);
        pause = false;
        // 3/6/2016 start change
        Intent intent = new Intent(this, Display.class);
        intent.putExtra("message1", message);
        startActivity(intent);
        // 3/6/2016 start change

    }
}
