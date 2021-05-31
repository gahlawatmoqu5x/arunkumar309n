package com.example.project.heartbeat;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase contactsDB = null;

    Button createDBButton, getContactsButton;
    EditText nameEditText,ageEditText;
    String contactName;
    String Contactage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = (EditText) findViewById(R.id.editText2);
        ageEditText =  (EditText) findViewById(R.id.editText3);
    }
    public void gotoNext(View view) {
       Intent intent = new Intent(this, Display.class);
        startActivity(intent);
        // Do something in response to button
    }


    public void CreateuserInDb(View view) {
        try{

            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            contactsDB = this.openOrCreateDatabase("MyContacts1.db", MODE_PRIVATE, null);

            // Execute an SQL statement that isn't select
            contactsDB.execSQL("CREATE TABLE IF NOT EXISTS contacts2 " +
                    "(id integer primary key, name VARCHAR, age VARCHAR);");

            // The database on the file system
            File database = getApplicationContext().getDatabasePath("MyContacts1.db");

            // Check if the database exists
            if (database.exists()) {
                Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Database Missing", Toast.LENGTH_SHORT).show();
            }

             contactName = nameEditText.getText().toString();
            Contactage = ageEditText.getText().toString();
            // Execute SQL statement to insert new data
            contactsDB.execSQL("INSERT INTO contacts2 (name, age) VALUES ('" +
                    contactName + "', '" + Contactage + "');");

        }

        catch(Exception e){

            Log.e("CONTACTS ERROR", "Error Creating Database");

        }

        Intent intent = new Intent(this, InitialActivity.class);
        intent.putExtra("message", contactName );
        intent.putExtra("messageAge", ageEditText.getText().toString() );
        startActivity(intent);
    }

}
