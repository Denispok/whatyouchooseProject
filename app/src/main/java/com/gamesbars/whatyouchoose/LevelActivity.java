package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE_PERCENTAGE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO_PERCENTAGE;
import static com.gamesbars.whatyouchoose.MainActivity.TABLE_QUESTIONS_NAME;

public class LevelActivity extends AppCompatActivity {

    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

    DataBaseHelper myDbHelper;
    SQLiteDatabase myDb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        //  Открываем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        // Открываем DataBase
        openDB();

        //  Считываем значения
        Toast.makeText(this, Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_ONE_PERCENTAGE))), Toast.LENGTH_LONG).show();

        //  Закрываем DataBase
        myDb.close();
    }



    public void openDB() {
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        myDb = myDbHelper.getDB();

        cursor = myDb.query(TABLE_QUESTIONS_NAME, new String[]{KEY_QUESTION_ONE, KEY_QUESTION_TWO,
                        KEY_QUESTION_ONE_PERCENTAGE, KEY_QUESTION_TWO_PERCENTAGE},
                "_id = " + Integer.toString(mSettings.getInt(APP_PREFERENCES_LVL, 0)), null,
                null, null, null);

        cursor.moveToNext();
    }
}
