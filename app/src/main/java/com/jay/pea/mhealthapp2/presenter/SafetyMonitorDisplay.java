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

package com.jay.pea.mhealthapp2.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.safetyMonitor.SafetyMonitor;

/**
 * class for setting up the safety monitor activity
 */
public class SafetyMonitorDisplay extends AppCompatActivity implements View.OnTouchListener {

    private CoordinatorLayout coordinatorLayout;
    private String TAG = "SAFETYMONITOR";
    private SafetyMonitor safetyMonitor;
    private ImageView iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8;
    private LinearLayout smLL1, smLL2, smLL3, smLL4, smLL5, smLL6, smLL7, smLL8;
    private LinearLayout[] llArray;
    private Context context;
    private boolean safety1, safety2, safety3, safety4, safety5, safety6, safety7, safety8;

    /**
     * on Create method to set up views and onclick listeners etc
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_monitor);
        context = this;

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordlayout);

        smLL1 = (LinearLayout) findViewById(R.id.smLL1);
        smLL2 = (LinearLayout) findViewById(R.id.smLL2);
        smLL3 = (LinearLayout) findViewById(R.id.smLL3);
        smLL4 = (LinearLayout) findViewById(R.id.smLL4);
        smLL5 = (LinearLayout) findViewById(R.id.smLL5);
        smLL6 = (LinearLayout) findViewById(R.id.smLL6);
        smLL7 = (LinearLayout) findViewById(R.id.smLL7);
        smLL8 = (LinearLayout) findViewById(R.id.smLL8);

        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv4 = (ImageView) findViewById(R.id.iv4);
        iv5 = (ImageView) findViewById(R.id.iv5);
        iv6 = (ImageView) findViewById(R.id.iv6);
        iv7 = (ImageView) findViewById(R.id.iv7);
        iv8 = (ImageView) findViewById(R.id.iv8);


        safetyMonitor = new SafetyMonitor(context);
        runSafetyChecks();
        setSafetyDisplays();

        llArray = new LinearLayout[]{smLL1, smLL2, smLL3, smLL4, smLL5, smLL6, smLL7, smLL8};
        for (int i = 0; i < llArray.length; i++) {
            llArray[i].setOnTouchListener(this);
        }
    }

    /**
     * runs all safety checks to set up the booleans for the safety monitor
     */
    public void runSafetyChecks() {
        safety1 = safetyMonitor.checkMedData();
        safety2 = safetyMonitor.checkDoseFreq();
        safety3 = safetyMonitor.checkMissedDoses();
        safety4 = safetyMonitor.checkDosesTaken();
        safety5 = safetyMonitor.checkAlarms();
        safety6 = safetyMonitor.alertTiming();
        safety7 = safetyMonitor.startDateValid();
        safety8 = safetyMonitor.endDateValid();

        Log.d(TAG, safety1 + "");
    }

    /**
     * sets up the safety displays
     */
    public void setSafetyDisplays() {
        if (!safety1) {
            iv1.setImageResource(R.drawable.cross);
            smLL1.setBackgroundResource(R.drawable.border_list_med_orange);
        }
        if (!safety2) {
            iv2.setImageResource(R.drawable.cross);
            smLL2.setBackgroundResource(R.drawable.border_list_med_orange);
        }
        if (!safety3) {
            iv3.setImageResource(R.drawable.cross);
            smLL3.setBackgroundResource(R.drawable.border_list_med_orange);
        }
        if (!safety4) {
            iv4.setImageResource(R.drawable.cross);
            smLL4.setBackgroundResource(R.drawable.border_list_med_orange);
        }
        if (!safety5) {
            iv5.setImageResource(R.drawable.cross);
            smLL5.setBackgroundResource(R.drawable.border_list_med_orange);
        }
        if (!safety6) {
            iv6.setImageResource(R.drawable.cross);
            smLL6.setBackgroundResource(R.drawable.border_list_med_orange);
        }
        if (!safety7) {
            iv7.setImageResource(R.drawable.cross);
            smLL7.setBackgroundResource(R.drawable.border_list_med_orange);
        }
        if (!safety8) {
            iv8.setImageResource(R.drawable.cross);
            smLL8.setBackgroundResource(R.drawable.border_list_med_orange);
        }
    }

    /**
     * onTouch method for handling all the on touch events
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {

            case R.id.smLL1:
                if (safety1) snackMsg("Your Medication Data is Correct");
                else {
                    snackMsg("Your Medication Data is corrupt, Please Check Your Medication Information and Restart MedMinder");
                }

                break;

            case R.id.smLL2:
                if (safety2) snackMsg("Your Dose Frequency Data is Correct");
                else {
                    snackMsg("Your Dose Frequency Data is corrupt, Please Check All Today's Doses and Ensure You have Taken The Correct Amount");
                }

                break;

            case R.id.smLL3:
                if (safety3) snackMsg("You Have No Recent Missed Doses.");
                else {
                    snackMsg("You Have Missed Doses From Yesterday.");
                }
                break;

            case R.id.smLL4:
                if (safety4) snackMsg("Your Alert Frequency Data is Correct.");
                else {
                    snackMsg("Your Alerts May Not Be Operating Correctly. Please Check Your Day's Doses");
                }
                break;

            case R.id.smLL5:
                if (safety5) snackMsg("Your Alert Timing Data is Correct.");
                else {
                    snackMsg("Your Alerts May Not Be Operating Correctly. Please Check Your Day's Doses");
                }
                break;

            case R.id.smLL6:
                if (safety6) snackMsg("Your Alert Data is Correct.");
                else {
                    snackMsg("Your Alerts May Not Be Operating Correctly. Please Check Your Day's Doses");
                }
                break;

            case R.id.smLL7:
                if (safety7) snackMsg("Your Medication Start Dates Are Correct.");
                else {
                    snackMsg("Your Medication Start Dates Are Corrupt, Please Check Your Medications and Restart MedMinder");
                }
                break;

            case R.id.smLL8:
                if (safety8) snackMsg("Your Medication End Dates Are Correct.");
                else {
                    snackMsg("Your Medication End Dates Are Corrupt, Please Check Your Medication and Restart MedMinder");
                }
                break;

        }

        return false;
    }

    /**
     * method to create a snackbar with the String arg passed to it
     *
     * @param string
     */
    public void snackMsg(String string) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, string, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}

