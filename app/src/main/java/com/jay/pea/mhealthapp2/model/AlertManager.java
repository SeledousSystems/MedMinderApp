/*
 * Copyright (c) 2016.
 *
 * The source code contained in this file remains the intellectual property of Paul Wright (PeaJay).
 * Any reuse, adaption or replication of this code, without express permission, is prohibited.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 *
 */

package com.jay.pea.mhealthapp2.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jay.pea.mhealthapp2.utilityClasses.AlarmReceiver;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Paul on 17/07/2016.
 * <p/>
 * Class that creates an alert for a dose and for a missed alert.
 */
public class AlertManager {

    public static String ALARMSTRING = "alarm";
    private static String TAG = "AlertManager";
    Context context;
    DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    private long[] alertMissedWindowArray = new long[]{43200000l, 21600000l, 10800000l, 7200000l, 5400000l, 3600000l};

    /**
     * Method that sets up alerts for doses. Sends the alerts to the android alert manager
     *
     * @param context
     * @param dose
     */
    public void setAlerts(Context context, Dose dose) {
        this.context = context;

        Log.d(TAG, "set alert  " + dose.getDoseTime());

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.removeExtra(ALARMSTRING);

        intent.putExtra(ALARMSTRING, dose);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        //set due alert
        alarmManager.set(AlarmManager.RTC_WAKEUP, (dose.getDoseTime().getMillis() - 60000), pendingIntent);

        //set missed alert
        alarmManager.set(AlarmManager.RTC_WAKEUP, dose.getDoseTime().plus(alertMissedWindowArray[dose.getMedication().getFreq() - 1]).getMillis(), pendingIntent);
    }


}
