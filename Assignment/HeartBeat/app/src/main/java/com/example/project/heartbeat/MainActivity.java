package com.example.project.heartbeat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    EditText nameEditText,ageEditText,idEditText;
    String contactName;
    String Contactage;
    String userID;
    String gender;
    String tableName;
    boolean tableExists;
    private RadioGroup radioGenderGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = (EditText) findViewById(R.id.editText2);
        ageEditText =  (EditText) findViewById(R.id.editText3);
        idEditText =  (EditText) findViewById(R.id.IDeditText);
        radioGenderGroup = (RadioGroup) findViewById(R.id.radioGroup2);
    }
    public void gotoNext(View view) {
      Intent intent = new Intent(this, Display.class);
        startActivity(intent);
        // Do something in response to button
    }

    public boolean nullOrEmptyCheck(String a)
    {
        if(a==null || a.length()==0)
        {
            return true;
        }
        return false;
    }


    public void CreateuserInDb(View view) {
        Intent intent = new Intent(this, InitialActivity.class);
        contactName = nameEditText.getText().toString();
        Contactage = ageEditText.getText().toString();
        userID=idEditText.getText().toString();
        int radioId=radioGenderGroup.getCheckedRadioButtonId();
      
        if(nullOrEmptyCheck(contactName) || nullOrEmptyCheck(Contactage) || nullOrEmptyCheck(userID) || radioId==-1)
        {
            Toast.makeText(this, "One or more fields empty", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            RadioButton rad=(RadioButton) findViewById(radioId);
            gender=rad.getText().toString();
            contactsDB = this.openOrCreateDatabase("MyContacts1.db", MODE_PRIVATE, null);
            contactName=contactName.replace("_","");
            userID=userID.replace("_","");
            tableName = contactName + "_" + Contactage + "_" + userID + "_" + gender;
            tableName=tableName.replace(" ","");
            try {
                Cursor c = null;
                c = contactsDB.query(tableName, null, null, null, null, null, null);
                tableExists = true;
                Toast.makeText(this, "Table Exists", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                contactsDB.execSQL("CREATE TABLE " + tableName +
                        " (timestamp TEXT, xValue integer, yValue integer, zValue integer);");
                tableExists = false;
                Toast.makeText(this, "Table Created", Toast.LENGTH_SHORT).show();
            }
        }
        intent.putExtra("tableExists", tableExists);
        intent.putExtra("tableName", tableName);
        intent.putExtra("userName", contactName);
        intent.putExtra("message", contactName );
        intent.putExtra("messageAge", ageEditText.getText().toString() );
        startActivity(intent);
/*
        try{
            RadioButton rad=(RadioButton) findViewById(radioId);
            String gender=rad.getText().toString();
            System.out.println("=================="+gender);
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
                Toast.makeText(this, "Table Created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Database Missing", Toast.LENGTH_SHORT).show();
            }
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
*/
    }

}
