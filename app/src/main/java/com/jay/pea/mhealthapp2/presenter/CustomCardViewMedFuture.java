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
import com.jay.pea.mhealthapp2.model.FutureDoses;

import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardViewMedFuture extends LinearLayout {

    protected TextView medicationName, doseNeededTV;

    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");
    String TAG = "CustomCardViewMedFuture";

    /**
     * Constructor for the CustomCardView which defines the view.
     */
    public CustomCardViewMedFuture(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view_med_future, this, true);

        medicationName = (TextView) findViewById(R.id.medicationName);
        doseNeededTV = (TextView) findViewById(R.id.doseNeededTV);
    }

    /**
     * method to setup each card and its views
     *
     * @param fd
     */
    public void setCard(FutureDoses fd) {

        int noDays = Days.daysBetween(fd.getStartDate().toLocalDate(), fd.getEndDate().toLocalDate()).getDays() + 1;
        int doseNo = noDays * fd.getFreq();

        medicationName.setText(fd.getMedName());
        doseNeededTV.setText("Over " + noDays + " days, you require " + doseNo + " Doses (" + fd.getDose() + ") of " + fd.getMedName());

    }

}




