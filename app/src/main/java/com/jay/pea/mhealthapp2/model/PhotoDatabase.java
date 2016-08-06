/*
 * Copyright (c) 2016.
 *
 * The source code contained in this file remains the intellectual property of Paul Wright (PeaJay).
 * Any reuse, adaption or replication of this code, without express permission, is prohibited.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied.
 *
 *
 */

package com.jay.pea.mhealthapp2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Paul on 16/07/2016.
 *
 * Facade to the image SQLite database. Allows creation of an image record and recovering it. Based on medID
 */
public class PhotoDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "database_name";
    // Table Names
    private static final String DB_TABLE = "table_image";
    // column names
    private static final String KEY_NAME = "image_name";
    private static final String KEY_IMAGE = "image_data";
    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "(_id INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_IMAGE + " BLOB);";
    String TAG = "PhotoDatabase";

    public PhotoDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
    }


    /**
     * Add image record.
     *
     * @param name
     * @param image
     * @throws SQLiteException
     */
    public void addEntry(String name, byte[] image) throws SQLiteException {
        Log.d(TAG, name + " " + image.length);
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_IMAGE, image);
        Log.d(TAG, cv.getAsString(KEY_IMAGE) + "    " + cv.getAsString(KEY_NAME));
        database.insert(DB_TABLE, null, cv);
    }

    /**
     * Recover image from database
     *
     * @param name
     * @return
     */

    public byte[] getBitmapImage(String name) {

        String[] columns = new String[]{KEY_NAME, KEY_IMAGE};
        String[] medName = new String[]{(name)};
        Log.d(TAG, name);
        Cursor cursor = getReadableDatabase().query(DB_TABLE, columns, "image_name=?", medName, null, null, null);
        cursor.moveToFirst();
        int key = cursor.getColumnIndex(KEY_IMAGE);

        Log.d(TAG, name + " key = " + key + " cursor count=" + cursor.getCount() + " " + cursor.toString() + " columns size=" + columns.length + " col names=" + cursor.getColumnNames().toString());
        byte[] image = cursor.getBlob(key);

        cursor.close();
        return image;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        // create new table
        onCreate(db);
    }
}