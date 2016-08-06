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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.DayMedRecord;
import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardViewMedRecord extends LinearLayout {

    private TextView title, textView1, textView2, textView3, textView4, textView5, textView6;
    private DateTimeFormatter dtfDateDay = DateTimeFormat.forPattern("E dd-MMM-yyyy");
    private DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    private TextView[] textViewArray;
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");
    private String TAG = "CustomCardViewMedRecord";

    /**
     * Constructor for the CustomCardView which defines the view.
     */
    public CustomCardViewMedRecord(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view_med_record, this, true);

        title = (TextView) findViewById(R.id.title);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
    }

    /**
     * setCard method for setting up each cards views
     * @param dmr
     */
    public void setCard(DayMedRecord dmr) {
        //set the content for Text and Image view of each dose
        Medication dmrMed = dmr.getMed();

        DateTime zeroTime = new DateTime(0);
        long now = new DateTime().now().getMillis();
        textViewArray = new TextView[]{textView1, textView2, textView3, textView4, textView5, textView6};


        title.setText(dmr.getDate().toString(dtfDateDay));

        Dose[] doseArray = dmr.getDoseArray().toArray(new Dose[dmr.getDoseArray().size()]);

        for (int i = 0; i < doseArray.length; i++) {

            String doseStringTime = doseArray[i].getDoseTime().toString(dtfTime);

            String doseString = doseStringTime + " Dose: " + checkDoseTaken(doseArray[i]);
            textViewArray[i].setTextColor(checkDate(doseArray[i]));
;
            if (doseArray[i].getDoseTime().getMillis() > now) {
                doseString = doseStringTime + " Dose: ";
                textViewArray[i].setTextColor(Color.BLACK);
            }


            textViewArray[i].setText(doseString);
            textViewArray[i].setVisibility(View.VISIBLE);

        }

    }

    /**
     * method to check each dose and set it as taken or missed
     * @param dose
     * @return
     */
    private String checkDoseTaken(Dose dose) {

        if (dose.getTakenTime().toString(dtfDate).equals(new DateTime(0).toString(dtfDate)))
            return "Missed";

        if (dose.getTakenTime().equals(new DateTime(0).plusYears(1))) return "Confirm Missed";

        String doseTime = "Taken at " + dose.getTakenTime().toString(dtfTime) + "";

        return doseTime;
    }

    /**
     * checks if a date is in the past and sets the text colour
     *
     * @param dose
     * @return
     */
    private int checkDate(Dose dose) {
        int black = Color.BLACK;
        int red = Color.RED;
        int blue = Color.BLUE;
        if (dose.getTakenTime().equals(new DateTime(0))) return red;
        if (dose.getTakenTime().equals(new DateTime(0).plusYears(1))) return blue;
        return black;
    }
}

