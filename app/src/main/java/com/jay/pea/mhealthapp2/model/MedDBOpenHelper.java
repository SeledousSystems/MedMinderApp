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
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides a facade to the inbuilt SQLite DB. It provides methods to CRUD the medication records from the
 * Database. It also provides a method for updating versions of the DB.
 */
public class MedDBOpenHelper extends SQLiteOpenHelper {
    protected static final int version = 1;
    protected static final String databaseName = "meds.db";
    protected String CREATE_SQL_MED = "create table Med (_id INTEGER PRIMARY KEY, medname TEXT, mednotes TEXT, dose TEXT, freq INTEGER, startdate INTEGER, enddate INTEGER, image INTEGER, alert1 INTEGER, alert2 INTEGER, alert3 INTEGER, alert4 INTEGER, alert5 INTEGER, alert6 INTEGER, alertson INTEGER)";
    protected String CREATE_SQL_DOSEMAP = "create table Dose (_id INTEGER PRIMARY KEY, medid INTEGER, key INT, value INT, alerton INT)";
    protected String CREATE_SQL_PHOTO = "create table Photo (_id INTEGER PRIMARY KEY, medid INTEGER, photo )";

    String TAG = "DATABASE";

    /**
     * Constructor for helper class.
     *
     * @param context
     */
    public MedDBOpenHelper(Context context) {
        super(context, databaseName, null, version);
    }

    /**
     * Override onCreate method of super class
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL_MED);
        db.execSQL(CREATE_SQL_DOSEMAP);
    }

    /**
     * method for upgrading the database through new versions of the application
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
        }
    }

    public HashMap[] getDoseMaps(Medication med) {

        String[] columns = new String[]{"medid", "key", "value", "alerton"};
        String[] medDbId = new String[]{Integer.toString(med.getDbID())};

        HashMap[] hashMapArray = new HashMap[2];
        HashMap<DateTime, DateTime> hashMap1 = new HashMap<>();
        HashMap<DateTime, Integer> hashMap2 = new HashMap<>();

        Cursor cursor = getReadableDatabase().query("Dose", columns, "medid=?", medDbId, null, null, null);

        int key = cursor.getColumnIndex("key");
        int value = cursor.getColumnIndex("value");
        int alertOnCursor = cursor.getColumnIndex("alerton");

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            Long keyLong = cursor.getInt(key) * 1000l;
            Long valueLong = cursor.getInt(value) * 1000l;
            DateTime doseDate = new DateTime(keyLong);
            final DateTime takenDate = new DateTime(valueLong);
            final int alertInt = cursor.getInt(alertOnCursor);
            hashMap1.put(doseDate, takenDate);
            hashMap2.put(doseDate, alertInt);
        }

        //close the cursor
        cursor.close();

        //add hashmaps to arrand return array
        hashMapArray[0] = hashMap1;
        hashMapArray[1] = hashMap2;
        return hashMapArray;
    }

    /**
     * method utilised to get all cards stored in the DB. Used to populate the app with data
     * the user has created previously.
     *
     * @return an ArrayList of all the Cards stored in the Database
     */
    public ArrayList<Medication> getAllMeds() {
        String sql = "select * from Med";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{});
        int dbIndex = cursor.getColumnIndex("_id");
        int medNameIndex = cursor.getColumnIndex("medname");
        int medNotesIndex = cursor.getColumnIndex("mednotes");
        int doseIndex = cursor.getColumnIndex("dose");
        int freqIndex = cursor.getColumnIndex("freq");
        int startDateIndex = cursor.getColumnIndex("startdate");
        int endDateIndex = cursor.getColumnIndex("enddate");
        int imageIndex = cursor.getColumnIndex("image");
        int alert1Index = cursor.getColumnIndex("alert1");
        int alert2Index = cursor.getColumnIndex("alert2");
        int alert3Index = cursor.getColumnIndex("alert3");
        int alert4Index = cursor.getColumnIndex("alert4");
        int alert5Index = cursor.getColumnIndex("alert5");
        int alert6Index = cursor.getColumnIndex("alert6");
        int alertsOnIndex = cursor.getColumnIndex("alertson");

        ArrayList<Medication> tempArray = new ArrayList();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            Medication newMed = new Medication(); //(cursor.getString(languageIndex), cursor.getString(allergyIndex), cursor.getInt(dateIndex));
            newMed.setDbID(cursor.getInt(dbIndex));
            newMed.setMedName(cursor.getString(medNameIndex));
            newMed.setMedNotes(cursor.getString(medNotesIndex));
            newMed.setDose(cursor.getString(doseIndex));
            newMed.setFreq(cursor.getInt(freqIndex));
            newMed.setMedStart(cursor.getInt(startDateIndex));
            newMed.setMedEnd(cursor.getInt(endDateIndex));
            newMed.setImageRes(cursor.getString(imageIndex));
            newMed.setAlert1(cursor.getInt(alert1Index));
            newMed.setAlert2(cursor.getInt(alert2Index));
            newMed.setAlert3(cursor.getInt(alert3Index));
            newMed.setAlert4(cursor.getInt(alert4Index));
            newMed.setAlert5(cursor.getInt(alert5Index));
            newMed.setAlert6(cursor.getInt(alert6Index));
            newMed.setAlertsOn(cursor.getInt(alertsOnIndex));

            //get hashmaps and set them to the med object
            HashMap[] hashMapArray = getDoseMaps(newMed);
            newMed.setDoseMap1(hashMapArray[0]);
            newMed.setDoseMap2(hashMapArray[1]);
            tempArray.add(newMed);
        }
        cursor.close();

        //return the array to be used at runtime
        return tempArray;
    }

    /**
     * Method to add a medication to the DB.
     */
    public void addMed(SQLiteDatabase db, Medication med) {
        ContentValues values = new ContentValues();
        //delete the med if it exists already
        deleteMedID(med.getDbID());
        values.put("medname", med.getMedName());
        values.put("mednotes", med.getMedNotes());
        values.put("dose", med.getDose());
        values.put("freq", med.getFreq());
        values.put("startdate", med.getMedStart());
        values.put("enddate", med.getMedEnd());
        values.put("image", med.getImageRes());
        values.put("alert1", med.getAlert1());
        values.put("alert2", med.getAlert2());
        values.put("alert3", med.getAlert3());
        values.put("alert4", med.getAlert4());
        values.put("alert5", med.getAlert5());
        values.put("alert6", med.getAlert6());
        values.put("alertson", med.getAlertsOn());
        //returns long ID of the med in the database. Allocate this to the card to track cards between the DB
        // and the application/list. Cast to int.

        med.setDbID((int) db.insert("Med", "null", values));
        addDoseMaps(db, med);
        db.close();

    }

    public void addDoseMaps(SQLiteDatabase db, Medication med) {
        ContentValues values = new ContentValues();
        HashMap<DateTime, DateTime> doseMap1 = med.getDoseMap1();
        HashMap<DateTime, Integer> doseMap2 = med.getDoseMap2();

        int doseDateInt = 0;
        int takenDateInt = 0;
        int alertOn = 0;
        for (DateTime doseDate : doseMap1.keySet()) {
            long doseDateLong = doseDate.getMillis() / 1000; //trim millis to fit into an int
            doseDateInt = (int) doseDateLong; //first element (key) of both hashMaps
            long takenDateLong = doseMap1.get(doseDate).getMillis() / 1000; //trim millis to fit into an int
            takenDateInt = (int) takenDateLong; // second element (value of first hashmap)
            alertOn = doseMap2.get(doseDate); // third element (value of second hashmap)

            // put the values into a values object to insert into database
            values.put("medid", med.getDbID());
            values.put("key", doseDateInt);
            values.put("value", takenDateInt);
            values.put("alerton", alertOn);
            //insert into the database
            db.insert("Dose", "null", values);
        }
        db.close();
    }

    /**
     * method to remove a med from the database based on its given dbID attribute
     *
     * @param id
     * @return
     */
    public int deleteMedID(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //delete the hash tables
        db.delete("Dose",
                "medid = ? ",
                new String[]{Integer.toString(id)});
        //delete the medication
        int returnInt = db.delete("Med",
                "_id = ? ",
                new String[]{Integer.toString(id)});
        return returnInt;

    }

    public void editMed(Medication med) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "med Dose  = " + med.getDose() + " medID = " + med.getDbID());
        ContentValues values = new ContentValues();
        values.put("medname", med.getMedName());
        values.put("mednotes", med.getMedNotes());
        values.put("dose", med.getDose());
        values.put("freq", med.getFreq());
        values.put("startdate", med.getMedStart());
        values.put("enddate", med.getMedEnd());
        values.put("image", med.getImageRes());
        values.put("alert1", med.getAlert1());
        values.put("alert2", med.getAlert2());
        values.put("alert3", med.getAlert3());
        values.put("alert4", med.getAlert4());
        values.put("alert5", med.getAlert5());
        values.put("alert6", med.getAlert6());
        values.put("alertson", med.getAlertsOn());

        Log.d(TAG, med.getFreq() + " " + values.get("dose"));

        //update database, ref to maps remains through the MedID
        //String[] idArray = new String[]{Integer.toString(med.getDbID())};
        String[] idArray = new String[]{med.getMedName()};
        String strFilter = "_id = " + med.getDbID();

        int dbReturn = db.update("Med", values, "medname = ? ", idArray);
//        int dbReturn = db.update("Med", values, "_id = ? ", idArray);
        //int dbReturn = db.update("Med", values, strFilter, null);
        Log.d(TAG, med.getFreq() + "  " + dbReturn);

        //delete the hash tables
        db.delete("Dose",
                "medid = ? ",
                new String[]{Integer.toString(med.getDbID())});

        //insert the updated hashtable
        addDoseMaps(db, med);

        //close db connection
        db.close();
    }

}