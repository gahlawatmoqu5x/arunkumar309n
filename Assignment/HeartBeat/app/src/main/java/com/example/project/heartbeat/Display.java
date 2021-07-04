package com.example.project.heartbeat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.heartbeat.util.DatabaseDownloader;
import com.example.project.heartbeat.util.DatabaseUploader;
import com.example.project.heartbeat.util.TaskDelegate;

import java.io.File;


public class Display extends AppCompatActivity implements TaskDelegate{
TextView displayEditText,displayContactsDbEditTest;
    SQLiteDatabase mDB;
    SQLiteDatabase AccelorometerDB;
    String Studentname;

    public void refreshText()
    {
        mDB= this.openOrCreateDatabase("MyContacts1.db", MODE_PRIVATE, null);

        Cursor c = mDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        StringBuilder sb=new StringBuilder();

        int count=1;
        if (c.moveToFirst()) {
            while (!c.isAfterLast() ) {
                //System.out.println("Table Name=> " + c.getString(0));
                String temp=c.getString(0);
                String [] array=temp.split("_");
                String [] namearr={"Name:","Age:","ID:","Gender:"};

                if(array.length==4)
                {
                    sb.append("Table "+count+":"+temp+"\n");
                    for(int i=0;i<array.length;i++)
                    {
                        String val=namearr[i];
                        sb.append(val);
                        sb.append(array[i]);
                        sb.append("\n");
                    }
                    count++;
                    sb.append("\n");
                }
                //Toast.makeText(Display.this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();

                c.moveToNext();
            }
        }
        c.close();
        mDB.close();
        String str=sb.toString();
        System.out.println(str);
        displayContactsDbEditTest.setText(str);
    }

    public void taskCompletionResult(String result)
    {
        System.out.println("ello");
        refreshText();


    }

    public void DownloadDB(View v)
    {
        DatabaseDownloader dbDownloader = new DatabaseDownloader();
        dbDownloader.setDelegate(this);
        File database = getApplicationContext().getDatabasePath("MyContacts1.db");
        if (database.exists()) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                dbDownloader.execute(database);
            }
        }
        else
        {
            System.out.println("No database");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setTitle("Database table names(Subset)");
//        displayEditText = (TextView) findViewById(R.id.editText);
//        displayEditText.setTextSize(16);
//        displayEditText.setText("Hi");
        displayContactsDbEditTest = (TextView) findViewById(R.id.editText6);
        displayContactsDbEditTest.setTextSize(16);
        displayContactsDbEditTest.setMaxLines(Integer.MAX_VALUE);
        displayContactsDbEditTest.setMovementMethod(new ScrollingMovementMethod());
        refreshText();




        Intent intent1 = getIntent();
        /**
        Studentname = intent1.getStringExtra("message1");
        String tableName = intent1.getStringExtra("tableName");
        Studentname= Studentname.toString();
        StringBuffer tableContents = new StringBuffer();

       mDB =  this.openOrCreateDatabase("MyContacts1.db", MODE_PRIVATE, null);
       AccelorometerDB = this.openOrCreateDatabase("MyAccDB.db", MODE_PRIVATE, null);
        // start
        // Cursor cursor = mDB.rawQuery("SELECT * FROM contacts2 WHERE name = '" + Studentname + "'",null);
        Cursor cursor = mDB.rawQuery("SELECT * FROM " + tableName + ";", null);
        Cursor cursorForAccel = AccelorometerDB.rawQuery("SELECT * FROM MyAccTable1 WHERE name = '" + Studentname + "'" + "LIMIT 6", null);

        // Get the index for the column name provided

        cursor.moveToFirst();

        StringBuffer contactList = new StringBuffer();
        tableContents.append("Timestamp\t\txValue\tyValue\tzValue");

        // Verify that we have results
        if(cursor != null && (cursor.getCount() > 0)){

            do{
                // Get the results and store them in a String
               /* String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);
                String age = cursor.getString(ageColumn);

                String timeStamp = cursor.getString(0);
                String xValue = cursor.getString(1);
                String yValue = cursor.getString(2);
                String zValue = cursor.getString(3);
                tableContents.append(timeStamp).append("\t\t").append(xValue).append("\t").append(yValue).append("\t").append(zValue);
                //contactList = contactList + id + " Name : " + name + " " +"Age : " + age+ "\n" + "\n" + "\n";

                // Keep getting results as long as they exist
            }while(cursor.moveToNext());
            contactList.append(tableName);

            // displayContactsDbEditTest.setText(contactList);
            displayEditText.setText(contactList.toString());
            displayContactsDbEditTest.setText(tableContents.toString());

        } else {

            Toast.makeText(this, "No Results to Show", Toast.LENGTH_SHORT).show();
            //displayContactsDbEditTest.setText("");
            displayEditText.setText("Table not found");
            displayContactsDbEditTest.setText("Table not found");
        }
/*        // code for retirving acdelerometr data*//*


     // Get the index for the column name provided
        int idColumn1 = cursorForAccel.getColumnIndex("id");
        int nameColumn1 = cursorForAccel.getColumnIndex("name");
        int xaxisColumn1 = cursorForAccel.getColumnIndex("xaxis");
        int yaxisColumn1 = cursorForAccel.getColumnIndex("yaxis");
        int zaxisColumn1 = cursorForAccel.getColumnIndex("zaxis");
        // int emailColumn = cursor.getColumnIndex("email");

        // Move to the first row of results
        cursorForAccel.moveToFirst();

        //String AccelDetails = "";

        // Verify that we have results
        if(cursorForAccel != null && (cursorForAccel.getCount() > 0)){

            do{
                // Get the results and store them in a String
                String idForAcc = cursorForAccel.getString(idColumn1);
                String nameForAcc = cursorForAccel.getString(nameColumn1);
                int xaxisVal= cursorForAccel.getInt(xaxisColumn1);
                int yaxisVal= cursorForAccel.getInt(yaxisColumn1);
                int zaxisVal= cursorForAccel.getInt(zaxisColumn1);
                //String email = cursor.getString(emailColumn);

                AccelDetails = AccelDetails + idForAcc + " : " + nameForAcc + " : " + xaxisVal + " : " + yaxisVal + " : " + zaxisVal +"\n";

                // Keep getting results as long as they exist
            }while(cursorForAccel.moveToNext());

           // displayEditText.setText(AccelDetails);
            displayContactsDbEditTest.setText(AccelDetails);

        } else  {

            Toast.makeText(this, "No Results to Show", Toast.LENGTH_SHORT).show();
           // displayEditText.setText("");
            displayContactsDbEditTest.setText("");

        }*/
    }
}
