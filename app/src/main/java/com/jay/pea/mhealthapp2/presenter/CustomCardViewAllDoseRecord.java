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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.AllDoseRecord;
import com.jay.pea.mhealthapp2.model.DayMedRecord;
import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;


/**
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardViewAllDoseRecord extends LinearLayout {

    private TextView title, textView1, textView2, textView3, textView4, textView5, textView6;
    private DateTimeFormatter dtfDateDay = DateTimeFormat.forPattern("E dd-MMM-yyyy");
    private DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");
    private String TAG = "CustomCardViewMedRecord";

    /**
     * Constructor for the CustomCardView which defines the view.
     */
    public CustomCardViewAllDoseRecord(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view_all_doses, this, true);

        title = (TextView) findViewById(R.id.title);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
    }

    /**
     * setCard method for setting up each cards views
     * @param
     */
    public void setCard(AllDoseRecord allDoseRecord) {
        //set the content for Text and Image view of each dose

        title.setText(allDoseRecord.getDate());

        String takenDoseString = " Doses.";
        if (allDoseRecord.getTakenDoses() == 1) takenDoseString = " Dose.";
        String missedDoseString = " Doses.";
        if (allDoseRecord.getMissedDoses() == 1) missedDoseString = " Dose.";

        textView1.setText("Taken: " + allDoseRecord.getTakenDoses() + takenDoseString );
        textView2.setText("Missed: " + allDoseRecord.getMissedDoses() + missedDoseString );
    }
}

