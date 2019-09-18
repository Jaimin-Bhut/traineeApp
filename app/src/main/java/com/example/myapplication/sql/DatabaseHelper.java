package com.example.myapplication.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.myapplication.Utility;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "User.db";
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_FIRSTNAME = "First_Name";
    private static final String COLUMN_USER_LASTNAME = "Last_Name";
    private static final String COLUMN_USER_EMAIL = "Email";
    private static final String COLUMN_USER_GENDER = "Gender";
    private static final String COLUMN_USER_PASSWORD = "Password";
    public static final String COLUMN_USER_PHOTO = "User_Profile_Pic";


    private String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "("
            + COLUMN_USER_FIRSTNAME + " TEXT,"
            + COLUMN_USER_LASTNAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT PRIMARY KEY,"
            + COLUMN_USER_GENDER + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_USER_PHOTO + " blob)";


    private String DROP_TABLE_USER = "DROP TABLE IF EXISTS " + TABLE_USER;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE_USER);
        onCreate(sqLiteDatabase);
    }

    public boolean addUser(User user) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_FIRSTNAME, user.getFirst_name());
            values.put(COLUMN_USER_LASTNAME, user.getLast_name());
            values.put(COLUMN_USER_EMAIL, user.getEmail());
            values.put(COLUMN_USER_GENDER, user.getGender());
            values.put(COLUMN_USER_PASSWORD, user.getPassword());
            values.put(COLUMN_USER_PHOTO, Utility.getBytes(user.getPhoto()));

            db.insert(TABLE_USER, null, values);
            db.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "addUser: " + e);
        }
        return false;
    }

    public boolean updateUser(User user) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_FIRSTNAME, user.getFirst_name());
            values.put(COLUMN_USER_LASTNAME, user.getLast_name());
            values.put(COLUMN_USER_EMAIL, user.getEmail());
            values.put(COLUMN_USER_GENDER, user.getGender());
            values.put(COLUMN_USER_PASSWORD, user.getPassword());
            values.put(COLUMN_USER_PHOTO, Utility.getBytes(user.getPhoto()));

            // updating row
            db.update(TABLE_USER, values, COLUMN_USER_EMAIL + " = ?",
                    new String[]{String.valueOf(user.getEmail())});
            db.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "add user" + e);
        }
        return false;
    }
    public User getUserDataByEmail(String value) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + TABLE_USER + " WHERE "
                    + COLUMN_USER_EMAIL + "='" + value + "';";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                user.setFirst_name(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRSTNAME)));
                user.setLast_name(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LASTNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPhoto(Utility.getPhoto(cursor.getBlob(cursor.getColumnIndex(COLUMN_USER_PHOTO))));
                return user;
            }
        } catch (Exception e) {
            Log.e(TAG, "getUserDataByEmail " + e);
        }
        return user;
    }

    public boolean checkUser(String email) {
        String[] columns = {
                COLUMN_USER_FIRSTNAME,
                COLUMN_USER_LASTNAME,
                COLUMN_USER_EMAIL,
                COLUMN_USER_PASSWORD
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {String.valueOf(email)};

        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }


    public List<User> getAllUser() {
        String[] columns = {
                COLUMN_USER_FIRSTNAME,
                COLUMN_USER_LASTNAME,
                COLUMN_USER_EMAIL,
        };
        String sortOrder =
                COLUMN_USER_EMAIL + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setFirst_name(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRSTNAME)));
                user.setLast_name(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LASTNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return userList;
    }
    public boolean checkUserForLogin(String email, String pass) {

        String[] columns = {
                COLUMN_USER_EMAIL
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        String[] selectionArgs = {email, pass};

        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }
    public void deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete( TABLE_USER, COLUMN_USER_EMAIL + " = ?",
                new String[]{email} );
        db.close();
    }
}
