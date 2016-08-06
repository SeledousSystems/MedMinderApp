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
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardViewMissedMeds extends LinearLayout {

    private TextView title, textView1, textView2;
    private DateTimeFormatter dtfDateDay = DateTimeFormat.forPattern("E dd-MMM-yyyy");
    private DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");
    private String TAG = "CustomCardViewMedRecord";

    /**
     * Constructor for the CustomCardView which defines the view.
     */
    public CustomCardViewMissedMeds(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view_missed_med, this, true);

        title = (TextView) findViewById(R.id.title);
        textView1 = (TextView) findViewById(R.id.textView1);
    }

    /**
     * setCard method for setting up each cards views
     *
     * @param med
     */
    public void setCard(Medication med) {
        //set the content for Text and Image view of each dose

        int missedDoses = 0;
        DateTime fromDate = new DateTime();
        DateTime toDate = new DateTime(0);

        for (DateTime doseDueDT : med.getDoseMap1().keySet()) {

            DateTime takenDT = med.getDoseMap1().get(doseDueDT);

            if (takenDT.equals(new DateTime(0)) && doseDueDT.isBefore(new DateTime().now())) {

                if (doseDueDT.isBefore(fromDate)) fromDate = doseDueDT;
                if (doseDueDT.isAfter(toDate)) toDate = doseDueDT;

                missedDoses++;
            }

        }

        if (missedDoses == 0) {
            title.setVisibility(GONE);
            textView1.setVisibility(GONE);

        }


        String missedDosesString = " missed doses";
        if (missedDoses == 1) missedDosesString = " missed dose";

        title.setText(med.getMedName());
        textView1.setText("You have " + missedDoses + missedDosesString + " from " + fromDate.toString(dtfDate) + " to " + toDate.toString(dtfDate));
        if (fromDate.toString(dtfDate).equals(toDate.toString(dtfDate)))
            textView1.setText("You have " + missedDoses + missedDosesString + " on " + fromDate.toString(dtfDate));

    }
}

