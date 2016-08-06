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

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Paul on 8/07/2016.
 * <p/>
 * Helper class for building Medication data and datetime to int.
 */
public class MedicationManager {

    String TAG = "MedicationManager";
    DateTime today = new DateTime().withHourOfDay(0).withMinuteOfHour(0);
    MedDBOpenHelper dbOpenHelper;

    /**
     * Method to build the first dose map which holds the dose due time and the dose taken time.
     * If not taken the dose taken time is recorded as epoch 01-01-70.
     *
     * @param med
     * @param context
     * @return
     */
    public HashMap<DateTime, DateTime> buildDoseMap1(Medication med, Context context) {
        //get a db object
        dbOpenHelper = new MedDBOpenHelper(context);

        //get final med
        final Medication medication = med;

        //hash map to map doses to taken bool
        HashMap<DateTime, DateTime> doseMap1 = new HashMap();

        //get existing hashMap details if exist
        doseMap1 = dbOpenHelper.getDoseMaps(medication)[0];

        //erase all future dose data, retain past data

        //get inclusive start and end date
        DateTime startDate = new DateTime(med.getMedStart() * 1000l);
        DateTime endDate = new DateTime(med.getMedEnd() * 1000l);

        //set hashStart date as today
        DateTime hashStart = today;

        //if medication is in future set hashStart to future date, if med start is in the past, set hashStart to today (for update to HashMap)
        if (hashStart.isBefore(startDate)) hashStart = startDate;

        //get alarm times
        DateTime alert1 = new DateTime(med.getAlert1() * 1000l);
        DateTime alert2 = new DateTime(med.getAlert2() * 1000l);
        DateTime alert3 = new DateTime(med.getAlert3() * 1000l);
        DateTime alert4 = new DateTime(med.getAlert4() * 1000l);
        DateTime alert5 = new DateTime(med.getAlert5() * 1000l);
        DateTime alert6 = new DateTime(med.getAlert6() * 1000l);

        DateTime[] dtArray = new DateTime[]{alert1, alert2, alert3, alert4, alert5, alert6};

        //get the number of days of med prescription
        int days = Days.daysBetween(hashStart.toLocalDate(), endDate.toLocalDate()).getDays() + 1;

        //get Frequency for alerts to ignore non required alerts.
        int freq = med.getFreq();

        //build the hashmap for daily dose due dates and bool for taken, if in the past exclude the reminder
        for (int i = 0; i < days; i++) {
            DateTime thisDay = hashStart.plusDays(i);
            //for the freq setup all alerts
            for (int j = 0; j < freq; j++) {
                DateTime alertTime = thisDay.withHourOfDay(dtArray[j].getHourOfDay()).withMinuteOfHour(dtArray[j].getMinuteOfHour()).withSecondOfMinute(0);
                final DateTime zeroDate = new DateTime(0);
                doseMap1.put(alertTime, zeroDate);
                Log.d(TAG, alertTime + " Time" + dtArray[j].getHourOfDay() + " zero date  " + zeroDate);
            }


        }
        //get existing hashMap details if exist
        HashMap<DateTime, DateTime> tempDoseMap1 = dbOpenHelper.getDoseMaps(medication)[0];

        //add all past dose data,
        if (!tempDoseMap1.isEmpty()) {
            for (DateTime dateTime : tempDoseMap1.keySet()) {

                if (dateTime.isBefore(today)) {
                    doseMap1.put(dateTime, tempDoseMap1.get(dateTime));
                }
            }
        }
        Log.d(TAG, doseMap1.size() + " doseMap1 size");
        return doseMap1;
    }

    /**
     * Method to build the second dose map which holds the dose due time and an int for if an alert
     * has been set.
     *
     * @param med
     * @param context
     * @return
     */
    public HashMap<DateTime, Integer> buildDoseMap2(Medication med, Context context) {

        //get a db object
        dbOpenHelper = new MedDBOpenHelper(context);

        //get final med
        final Medication medication = med;

        //hash map to map doses to taken bool
        HashMap<DateTime, Integer> doseMap2 = new HashMap();

        //set hashStart date as today
        DateTime hashStart = today;

        //get inclusive start and end date
        DateTime startDate = new DateTime(med.getMedStart() * 1000l);
        DateTime endDate = new DateTime(med.getMedEnd() * 1000l);

        //if medication is in future set hashStart to future date, if med start is in the past, set hashStart to today (for update to HashMap)
        if (hashStart.isBefore(startDate)) hashStart = startDate;

        //get alarm times
        DateTime alert1 = new DateTime(med.getAlert1() * 1000l);
        DateTime alert2 = new DateTime(med.getAlert2() * 1000l);
        DateTime alert3 = new DateTime(med.getAlert3() * 1000l);
        DateTime alert4 = new DateTime(med.getAlert4() * 1000l);
        DateTime alert5 = new DateTime(med.getAlert5() * 1000l);
        DateTime alert6 = new DateTime(med.getAlert6() * 1000l);

        DateTime[] dtArray = new DateTime[]{alert1, alert2, alert3, alert4, alert5, alert6};
        Log.d(TAG, Arrays.toString(dtArray));


        //get the number of days of med prescription
        int days = Days.daysBetween(hashStart.toLocalDate(), endDate.toLocalDate()).getDays() + 1;

        //get Frequency for alerts to ignore non required alerts.
        int freq = med.getFreq();
        Log.d(TAG, freq + " Days =" + days + " " + med.getMedName());

        //build the hashmap for daily dose due dates and alertsOn Integer, if in the past exclude the reminder
        for (int i = 0; i < days; i++) {
            DateTime thisDay = hashStart.plusDays(i);

            //for the freq setup all alertsOn
            for (int j = 0; j < freq; j++) {
                DateTime alertTime = thisDay.withHourOfDay(dtArray[j].getHourOfDay()).withMinuteOfHour(dtArray[j].getMinuteOfHour()).withSecondOfMinute(0);
                if (alertTime.isAfter(today))
                    doseMap2.put(alertTime, medication.getAlertsOn());

            }
            Log.d(TAG, doseMap2.size() + " doseMap2 size");

        }

        //get existing hashMap details if exist
        HashMap<DateTime, Integer> tempDoseMap2 = dbOpenHelper.getDoseMaps(medication)[1];

        //add all past dose data,
        if (!tempDoseMap2.isEmpty()) {
            for (DateTime dateTime : tempDoseMap2.keySet()) {

                if (dateTime.isBefore(today)) {
                    doseMap2.put(dateTime, tempDoseMap2.get(dateTime));
                }
            }
        }
        Log.d(TAG, doseMap2.size() + " doseMap2 size");
        return doseMap2;
    }

    /**
     * Method to get all the alert times set by the user for building the first dose map
     *
     * @param med
     * @return
     */
    public DateTime[] getAlertTimes(Medication med) {

        //build an array with all alert times (6 per day)
        DateTime[] fullDateTimeArray = new DateTime[]{convertSecsToDateTime(med.getAlert1()), convertSecsToDateTime(med.getAlert2()), convertSecsToDateTime(med.getAlert3()), convertSecsToDateTime(med.getAlert4()), convertSecsToDateTime(med.getAlert5()), convertSecsToDateTime(med.getAlert6())};

        //allocate only those needed based on freq of dose per day
        DateTime[] freqDateTimeArray = new DateTime[med.getFreq()];
        for (int i = 0; i < med.getFreq(); i++) {
            freqDateTimeArray[i] = fullDateTimeArray[i];
        }
        //return the array with only daily alerts set
        return freqDateTimeArray;
    }

    /**
     * Method to convert an integer of seconds to a Joda DateTime
     *
     * @param secs
     * @return
     */
    public DateTime convertSecsToDateTime(int secs) {
        if (secs < 0) secs = 0;
        long longMillis = secs * 1000l;
        return new DateTime(longMillis);
    }

}
