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

package com.jay.pea.mhealthapp2.safetyMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.DoseComparator;
import com.jay.pea.mhealthapp2.model.MedComparator;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Paul on 12/07/2016.
 * <p/>
 * Safety monitor class that carries out all the safety functional checks and returns a
 * boolean for the safety state. Class has eight methods, on for each safety function.
 */
public class SafetyMonitor {

    public static final String PREFS_NAME_1 = "Alerts_Fired";
    public static final String PREFS_NAME_2 = "Alert_Times";
    MedDBOpenHelper dbHelper;
    ArrayList<Medication> medList;
    ArrayList<Medication> safeMedList;
    Context context;
    SQLiteDatabase db;
    SQLiteDatabase smdb;
    SafetyMonitorDBOpenHelper smDBHelper;
    String TAG = "SafetyMonitorDisplay";
    ArrayList<Dose> todaysDoseList;
    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    boolean safety1, safety2, safety3, safety4, safety5, safety6, safety7, safety8;
    SharedPreferences alertCount;
    SharedPreferences alertTimes;

    //constructor builds medArray and safetyArray
    public SafetyMonitor(Context context) {
        //get context
        this.context = context;

        //get medications
        medList = new ArrayList<>();
        medList.clear();
        dbHelper = new MedDBOpenHelper(context);
        //db = dbHelper.getWritableDatabase();
        medList = dbHelper.getAllMeds();
        Collections.sort(medList, new MedComparator());

        //get safety data
        safeMedList = new ArrayList<>();
        safeMedList.clear();
        smDBHelper = new SafetyMonitorDBOpenHelper(context);
        smdb = smDBHelper.getReadableDatabase();
        safeMedList = smDBHelper.getAllMeds();

//        for (Medication med : safeMedList) {
//            smDBHelper.deleteMedID(med.getDbID());
//        }
//
//        for (Medication med : medList) {
//            smDBHelper.addMed(smdb, med);
//        }

        Collections.sort(safeMedList, new MedComparator());
    }

    /**
     * Method that verifies the medication data in the main DB against the data in the safety DB.
     *
     * @return
     */
    public boolean checkMedData() {

        if (medList.size() == 0) return true;
        Log.d(TAG, medList.get(0).getMedName() + " " + safeMedList.get(0).getMedName());
        Log.d(TAG, medList.get(1).getMedName() + " " + safeMedList.get(1).getMedName());
        Log.d(TAG, medList.get(2).getMedName() + " " + safeMedList.get(2).getMedName());


        for (int i = 0; i < medList.size(); i++) {
            if (!(medList.get(i).getDose() + medList.get(i).getMedName())
                    .equals(safeMedList.get(i).getDose() + safeMedList.get(i).getMedName())) {
                Log.d(TAG, medList.get(i).getDose() + medList.get(i).getMedName() + " = norm. Safe = " + safeMedList.get(i).getDose() + safeMedList.get(i).getMedName());
                return false;
            }
        }
        return true;
    }

    /**
     * Independent Safety Monitor checks the number of doses taken per day for a medication and assures it does not exceed the required number of doses (frequency). Function must account for dismissed alarms.
     *
     * @return
     */
    public boolean checkDoseFreq() {
        //create a dose List to match the main dose list
        ArrayList<Dose> doseList = new ArrayList<>();
        if (medList.isEmpty()) return true;
        //get all doses and add to doseList
        for (Medication med : medList) {
            HashMap<DateTime, DateTime> doseMap1 = med.getDoseMap1();
            HashMap<DateTime, Integer> doseMap2 = med.getDoseMap2();
            for (DateTime doseDate : doseMap1.keySet()) {
                //get takenDate and alertOn
                DateTime takenDate = doseMap1.get(doseDate);
                int alertOn = doseMap2.get(doseDate);
                Dose dose = new Dose(med, doseDate, takenDate, alertOn);
                doseList.add(dose);
            }
        }
        DateTime today = new DateTime().now();
        todaysDoseList = new ArrayList<>();
        //get today's doses
        for (Dose dose : doseList) {
            DateTimeFormatter dayFormat = DateTimeFormat.forPattern("dd MMM yyyy");
            if (dose.getDoseTime().toString(dayFormat).equals(today.toString(dayFormat))) {
                todaysDoseList.add(dose);
            }
            //sort collect, add to adaptor
            Collections.sort(todaysDoseList, new DoseComparator());
        }
        for (Dose d : todaysDoseList) {
            for (Medication safeMed : safeMedList) {
                if (safeMed.getMedName().equals(d.getMedication().getMedName()) & safeMed.getMedStart() == (d.getMedication().getMedStart())) {
                    if (d.getMedication().getFreq() == safeMed.getFreq()) {
                        return true;
                    }
                }
            }
        }
        // safety fail as have not matched frequency
        return false;
    }

    /**
     * Independent Safety monitor monitors the number of missed doses over a time period for each medication. If it exceeds a threshold the user will be notified.
     *
     * @return
     */
    public boolean checkMissedDoses() {

        int missedDoseCount = 0;
        for (Dose missedDose : todaysDoseList) {

            if (missedDose.getDoseTime().isBefore(new DateTime().now()) && missedDose.getMedication().getDoseMap1().get(missedDose.getDoseTime()).equals(new DateTime(0))) {
                missedDoseCount++;
            }
        }

        if (missedDoseCount > 0) return false;
        else return true;

//        for (Medication med : medList) {
//            HashMap<DateTime, DateTime> doseMap = med.getDoseMap1();
//            for (DateTime dateTime : doseMap.keySet()) {
//                DateTime doseDate = dateTime;
//                DateTime today = new DateTime().now();
//                if (doseDate.toString(dtfDate).equals(today.toString())) {
//                    DateTime takenTime = doseMap.get(dateTime);
//                    if (doseDate.isBefore(today) && takenTime.isBefore(new DateTime(0).plusYears(2))) {
//                        return false;
//                    }
//                }
//            }
//        }
//        return true;


    }

    /**
     * Independent Safety Monitor checks the number of doses taken per day for a medication and assures it does not exceed the required number of doses (frequency).
     *
     * @return
     */
    public boolean checkDosesTaken() {

        ArrayList<DateTime> takenAL = new ArrayList<>();
        for (Medication med : medList) {
            for (DateTime doseDateTime : med.getDoseMap1().keySet()) {
                DateTime takenDateTime = med.getDoseMap1().get(doseDateTime);
                if (takenDateTime != new DateTime(0)) {
                    takenAL.add(takenDateTime);
                }
            }
        }
        int doseTakenCount = 0;
        int tempCount = 0;
        for (DateTime takenDateTime : takenAL) {
            for (DateTime innerTakenDateTime : takenAL) {
                if (innerTakenDateTime.toString(dtfDate).equals(takenDateTime.toString(dtfDate)) && innerTakenDateTime != takenDateTime) {
                    tempCount++;
                }
            }
            if (tempCount > doseTakenCount) {
                doseTakenCount = tempCount;
            }
        }
        return true;
    }

    /**
     * Independent Safety monitor checks the number of alarms fired per day for a medication and assures it does not exceed the day's number of doses.
     *
     * @return
     */
    public boolean checkAlarms() {

        if (medList.isEmpty()) return true;
        alertCount = context.getSharedPreferences(PREFS_NAME_1, 0);
        for (Medication med : medList) {
            if (med.getFreq() == alertCount.getInt(med.getMedName(), 0)) return true;

        }
        return false;
    }

    // 6.X

    /**
     * Independent Safety Monitor checks alerts are fired within one hour of dose time.
     *
     * @return
     */
    public boolean alertTiming() {

        if (medList.isEmpty()) return true;

        boolean match = false;
        alertTimes = context.getSharedPreferences(PREFS_NAME_2, 0);

        for (Medication med : medList) {

            Set<String> tempS1 = alertTimes.getStringSet(med.getDose(), new HashSet<String>());

            for (String string :
                    tempS1) {

                for (DateTime doseTime : med.getDoseMap1().keySet()) {

                    if (doseTime.toString(dtfDate).equals(new DateTime().toString(dtfDate))) {

                        if (doseTime.toString(dtfTime).equals(string)) match = true;
                    }
                }
            }
        }
        return match;
    }

    /**
     * Independent Safety monitor checks Medication Start Date matches medication Start Dates input by user.
     *
     * @return
     */
    public boolean startDateValid() {

        if (medList.isEmpty()) return true;

        for (Medication med : medList) {
            for (Medication safeMed : safeMedList) {
                if (safeMed.getMedName().equals(med.getMedName()) && safeMed.getMedEnd() == (med.getMedEnd())) {
                    if (med.getMedStart() == safeMed.getMedStart()) return true;
                }
            }
        }
        return false;
    }

    //  8.X

    /**
     * Independent Safety monitor checks Medication End Date matches medication End Date input by user.
     *
     * @return
     */
    public boolean endDateValid() {
        if (medList.isEmpty()) return true;

        for (Medication med : medList) {
            for (Medication safeMed : safeMedList) {
                if (safeMed.getMedName().equals(med.getMedName()) && safeMed.getMedStart() == (med.getMedStart())) {
                    if (med.getMedEnd() == safeMed.getMedEnd()) return true;
                }
            }
        }
        return false;
    }

    /**
     * run all safety checks
     */
    public boolean runSafetyChecks() {
        safety1 = checkMedData();
        safety2 = checkDoseFreq();
        safety3 = checkMissedDoses();
        safety4 = checkDosesTaken();
        safety5 = checkAlarms();
        safety6 = alertTiming();
        safety7 = startDateValid();
        safety8 = endDateValid();

        if (!safety1 || !safety2 || !safety3 || !safety4 || !safety5 || !safety6 || !safety7 || !safety8) {
            return false;
        } else return true;
    }
}
