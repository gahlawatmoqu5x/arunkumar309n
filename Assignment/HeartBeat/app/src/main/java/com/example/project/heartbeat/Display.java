package com.example.project.heartbeat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;


public class Display extends AppCompatActivity {
EditText displayEditText,displayContactsDbEditTest;
    SQLiteDatabase mDB;
    SQLiteDatabase AccelorometerDB;
    String Studentname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        displayEditText = (EditText) findViewById(R.id.editText);
        displayContactsDbEditTest = (EditText) findViewById(R.id.editText6);
        Intent intent1 = getIntent();
        Studentname = intent1.getStringExtra("message1");
        Studentname= Studentname.toString();

       mDB =  this.openOrCreateDatabase("MyContacts1.db", MODE_PRIVATE, null);
       AccelorometerDB = this.openOrCreateDatabase("MyAccDB.db", MODE_PRIVATE, null);
        // start
       Cursor cursor = mDB.rawQuery("SELECT * FROM contacts2 WHERE name = '" + Studentname + "'",null);
      Cursor cursorForAccel = AccelorometerDB.rawQuery("SELECT * FROM MyAccTable1 WHERE name = '" + Studentname + "'" +"LIMIT 6",null);

        // Get the index for the column name provided

       int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int ageColumn = cursor.getColumnIndex("age");

        cursor.moveToFirst();

        String contactList = "";

        // Verify that we have results
        if(cursor != null && (cursor.getCount() > 0)){

            do{
                // Get the results and store them in a String
                String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);
                String age = cursor.getString(ageColumn);

                contactList = contactList + id + " Name : " + name + " " +"Age : " + age+ "\n" + "\n" + "\n";

                // Keep getting results as long as they exist
            }while(cursor.moveToNext());

           // displayContactsDbEditTest.setText(contactList);
            displayEditText.setText(contactList);

        } else {

            Toast.makeText(this, "No Results to Show", Toast.LENGTH_SHORT).show();
            //displayContactsDbEditTest.setText("");
            displayEditText.setText("");

        }
        // code for retirving acdelerometr data


     // Get the index for the column name provided
        int idColumn1 = cursorForAccel.getColumnIndex("id");
        int nameColumn1 = cursorForAccel.getColumnIndex("name");
        int xaxisColumn1 = cursorForAccel.getColumnIndex("xaxis");
        int yaxisColumn1 = cursorForAccel.getColumnIndex("yaxis");
        int zaxisColumn1 = cursorForAccel.getColumnIndex("zaxis");
        // int emailColumn = cursor.getColumnIndex("email");

        // Move to the first row of results
        cursorForAccel.moveToFirst();

        String AccelDetails = "";

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

        }



    }
}
