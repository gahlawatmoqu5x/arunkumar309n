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
import android.graphics.Color;

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

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import com.example.project.heartbeat.util.DatabaseUploader;

public class InitialActivity extends AppCompatActivity implements SensorEventListener {



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // dummey method
    }
    @Override
    public void onBackPressed() {
        System.out.println("Bye bye");
        AccelorometerDB.close();;
        AccelorometerDB=null;
        SM.unregisterListener(this);
        this.finish();
        super.onBackPressed();

    }

    // 3/6/2016 start change
    SQLiteDatabase AccelorometerDB = null;
    private Sensor mySensor;
    private SensorManager SM;
    int yaxisValue;
    int xaxisValue;
    int zaxisValue;
    TextView nameEditText;
    //editText3
    TextView ageEditText;
    String message="defaultMessage";
    String ageMessage="defaultAge";
    String tableName;
    String userName;
    boolean tableExists;
    // 3/6/2016 end change
    private Button startButton;
    private Button stopButton;
    private GraphView graph;
    //private TextView textField;
    private LineGraphSeries<DataPoint> xseries;
    private LineGraphSeries<DataPoint> yseries;
    private LineGraphSeries<DataPoint> zseries;
    private int lastX = 0;
    private Queue<Float> xqueue = new LinkedList<Float>();
    private Queue<Float> yqueue = new LinkedList<Float>();
    private Queue<Float> zqueue = new LinkedList<Float>();
    long lastUpdate = 0;

    private static Random random = new Random();
    private boolean pause = false;
    private boolean senseData = false;
    final int N = 1000;
    private boolean startPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ewrwe");
        setContentView(R.layout.initialactivity);
        // 3/6/2016 start change
        Intent intent = getIntent();
        message = intent.getStringExtra("message");
        message= message.toString();
        ageMessage = intent.getStringExtra("messageAge");
        ageMessage= ageMessage.toString();

        tableName = intent.getStringExtra("tableName").toString();
        userName = intent.getStringExtra("userName").toString();
        if (intent.getBooleanExtra("tableExists", false)) tableExists = true;
        else tableExists = false;
        if (tableExists)
            Toast.makeText(this, "Welcome back, " + userName + "!", (Toast.LENGTH_SHORT)).show();
        else
            Toast.makeText(this, "Happy to have you, " + userName + "! :)", Toast.LENGTH_SHORT).show();

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        nameEditText= (TextView) findViewById(R.id.editText2);
        ageEditText = (TextView) findViewById(R.id.editText3);
        // 3/6/2016 end change
        startButton = (Button) findViewById(R.id.button);
        stopButton = (Button) findViewById(R.id.button2);
        //textField = (TextView) findViewById(R.id.textView);
        graph = (GraphView) findViewById(R.id.graph);
        xseries = new LineGraphSeries<DataPoint>();
        xseries.setColor(Color.parseColor("#00ff00"));
        yseries = new LineGraphSeries<DataPoint>();
        yseries.setColor(Color.parseColor("#ff0000"));
        zseries = new LineGraphSeries<DataPoint>();
        zseries.setColor(Color.parseColor("#0000ff"));
        graph.addSeries(xseries);
        graph.addSeries(yseries);
        graph.addSeries(zseries);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-15);
        viewport.setMaxY(15);
        viewport.setMinX(0);
        viewport.setMaxX(N);
        viewport.setScrollable(false);
        nameEditText.setText(message);
        ageEditText.setText(ageMessage);
    }
    // 3/6/2016 start change
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (senseData) {
            Sensor mySensor = event.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                long curTime = System.currentTimeMillis();
                xqueue.add(x);
                yqueue.add(y);
                zqueue.add(z);
                if ((curTime - lastUpdate) > 1000) {
                    lastUpdate = curTime;
                    if(AccelorometerDB!=null)
                    AccelorometerDB.execSQL("INSERT INTO " + tableName + "(timestamp, xValue, yValue, zValue) VALUES ('" +
                            curTime + "', '" + x + "', '" + y + "', '" + z + "');");
                    if(startPressed){
                        getValueFromTable();
                    }
                }

            }
            xaxisValue = (int) event.values[0] ;
            yaxisValue = (int) event.values[1] ;
            zaxisValue = (int) event.values[2] ;


            // normalizing by adding +30
            // we have to discuss what to do with the xaxisValue because I am not using it
        }
         xaxisValue = (int) event.values[0];
        yaxisValue = (int) event.values[1] ;
        zaxisValue = (int) event.values[2];

        // normalizing by adding +30
        // we have to discuss what to do with the xaxisValue because I am not using it

    }

    public void connectDB(View view){
        AccelorometerDB = this.openOrCreateDatabase("MyContacts1.db", MODE_PRIVATE, null);
        senseData = true;
        Toast.makeText(this, "DB CONNECTED", Toast.LENGTH_SHORT).show();
    }
    public void uploadDB(View view){
        DatabaseUploader dbUploader = new DatabaseUploader();
        File database = getApplicationContext().getDatabasePath("MyContacts1.db");
        if (database.exists()) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                dbUploader.execute(database);
            }
        }
        else
        {
            System.out.println("No database");
        }

    }

    // 3/6/2016 end change
    @Override
    protected void onResume(){
        super.onResume();
        View rootView = null;
        View currentFocus = getWindow().getCurrentFocus();
        if (currentFocus != null)
            rootView = currentFocus.getRootView();
        connectDB(rootView);
/*        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < N; i++) {
                    // 3/6/2016 start change
                    //queue.add(random.nextDouble()*100d);
                    xqueue.add((double) xaxisValue);
                    yqueue.add((double) yaxisValue);
                    zqueue.add((double) zaxisValue);
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
        }).start();*/
    }

    private void getValueFromTable(){
        String sqlQuery = "select * from " + tableName + ";";
        System.out.print(sqlQuery);
        Cursor c = null;
        c = AccelorometerDB.rawQuery(sqlQuery,null);
        // AccelorometerDB.query(tableName, new String[] {"xValue", "yValue", "zValue"}, null, null, null, null, null);
        //AccelorometerDB.rawQuery("select * from ?", new String[] {tableName});
        if (c!= null){
            int count = c.getCount();
            if(count>10){
                for(int i = count-10;i<count; i++){
                    c.moveToPosition(i);
                    Double xVal = Double.parseDouble(c.getString(1));
                    Double yVal = Double.parseDouble(c.getString(2));
                    Double zVal = Double.parseDouble(c.getString(3));
                    xseries.appendData(new DataPoint(lastX, (double) xVal), true, 10);
                    yseries.appendData(new DataPoint(lastX, (double) yVal), true, 10);
                    zseries.appendData(new DataPoint(lastX++, (double) zVal), true, 10);
                }
            }
            else{
                Toast.makeText(this, "Not enough values to plot - Wait for atleast 10 seconds!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // add random data to graph
//    private void addEntry() {
//        String contactName = nameEditText.getText().toString();
//        // here, we choose to display max 10 points on the viewport and we scroll to end
//        Object datax = xqueue.poll();
//        Object datay = yqueue.poll();
//        Object dataz=zqueue.poll();
//
//        if (datax != null && datay!=null && dataz!=null && pause) {
//            xseries.appendData(new DataPoint(lastX, (double) datax), true, 10);
//            yseries.appendData(new DataPoint(lastX, (double) datay), true, 10);
//            zseries.appendData(new DataPoint(lastX++, (double) dataz), true, 10);
//            //textField.setText(Double.toString((Double) datax));
//
//            AccelorometerDB.execSQL("INSERT INTO MyAccTable1 (name, xaxis, yaxis, zaxis) VALUES ('" +
//                    contactName + "', '" + xaxisValue + "', '" + yaxisValue + "', '" + zaxisValue + "');");
//        }
//    }
    public void startFunc(View view){
       // textField.setText(R.string.startPressed);
        if(!pause){
            graph.addSeries(xseries);
            graph.addSeries(yseries);
            graph.addSeries(zseries);
        }
        pause = true;
        startPressed = true;
        getValueFromTable();

/*
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

*/



    }
    public void stopFunc(View view){
      //  textField.setText(R.string.stopPressed);
        pause = false;
        graph.removeAllSeries();
        // 3/6/2016 start change
//        Intent intent = new Intent(this, Display.class);
//        intent.putExtra("tableName", tableName).putExtra("message1", message);
//        startActivity(intent);
        // 3/6/2016 start change

    }
}
