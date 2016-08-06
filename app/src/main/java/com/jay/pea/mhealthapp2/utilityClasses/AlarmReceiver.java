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

package com.jay.pea.mhealthapp2.utilityClasses;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.AlertManager;
import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.presenter.MainActivity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    String ACTION_DISMISS = "ACTION_DISMISS";
    String ACTION_SNOOZE = "ACTION_SNOOZE";
    String TAG = "ALARMRECIEVER";
    DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    Context context;
    Dose dose;

    private long[] alertMissedWindowArray = new long[]{43200000l, 21600000l, 10800000l, 7200000l, 5400000l, 3600000l};

    /**
     * OnRecieve method that recieves calls form the AlertManager class and then applies some logic to fire notifications to the user.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onRecieve");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MedMinder");
        //Acquire the lock
        wl.acquire();

        dose = null;
        dose = (Dose) intent.getSerializableExtra(AlertManager.ALARMSTRING);
        Log.d(TAG, " This dose is " + dose.getMedication().getMedName() + "  " + dose.getDoseTime().toString(dtfTime));

        if (dose == null) {
            Log.d(TAG, "Alert object Extra is null");
        }

        //get the parent med
        Medication med = dose.getMedication();
        int alertCount = 0;
        for (DateTime doseDueDT : med.getDoseMap1().keySet()) {
            DateTime doseTakenDT = med.getDoseMap1().get(doseDueDT);
            if (doseDueDT.isBefore(new DateTime().now()) && doseDueDT.isAfter(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)) && doseTakenDT.equals(new DateTime(0))) {
                alertCount++;
            }
        }

        if (alertCount == 0) {
            return;
        }

        SharedPreferences sharedPref = context.getSharedPreferences("AlertCounter", 0);
        int alertCountPref = sharedPref.getInt(med.getMedName(), 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        DateTime now = new DateTime().now();
        int notifID = dose.getMedication().getDbID();
        Random rand = new Random();
        //int notifID = rand.nextInt();
        Log.d(TAG, dose.getMedication().getDbID() + "   " + dose.getDoseTime().getMillisOfDay());

        if (dose.getTakenTime().toString(dtfDate).equals(new DateTime(0).toString(dtfDate))) {
//            if ((dose.getDoseTime().getMillis() + alertMissedWindowArray[dose.getMedication().getFreq() - 1] * 2) > new DateTime().getMillis());
            String alertString = "You have " + alertCount + " missed doses for " + med.getMedName();
            if (alertCount == 1) {
                alertString = "You have a missed dose for " + med.getMedName();
            }

            //check if the dose is due +/- 15mins and advise that a dose is due.
            long diffMillis = dose.getDoseTime().getMillis() - new DateTime().now().getMillis();
            Log.d(TAG, diffMillis + "   dose time = " + dose.getDoseTime().getMillis() + " now= " + new DateTime().now().getMillis());
            boolean doseDue = Math.abs(diffMillis) < 1800000;
            if (doseDue) {
                alertString = dose.getMedication().getDose() + " of " + dose.getMedication().getMedName() + " is due now.";
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.medminder_icon)
                            .setContentTitle("MedMinder")
                            .setContentText(alertString);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, MainActivity.class);

            // The stack builder object will contain an artificial back stack for the started Activity.
            // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            // mId allows you to update the notification later on.
            mNotificationManager.notify(notifID, mBuilder.build());
            Log.d(TAG, " Notification sent " + notifID);
//
//
//            editor.putInt(med.getMedName(), alertCount);
//            editor.commit();


            //Release the lock
            wl.release();
        }
    }


    //Unused methods for set, cancel and onsetime set

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 30 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi);
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context) {
        Log.d(TAG, "set on time timer");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);

    }
}