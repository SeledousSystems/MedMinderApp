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
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.DbBitmapUtility;
import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.model.PhotoDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardViewMeds extends LinearLayout {

    private TextView medText, doseText, freqText, dateText;
    private ImageView medImage;
    private Bitmap imageBitmap;
    private PhotoDatabase pdb;
    private Context context;

    /**
     * Constructor for the CustomCardViewMeds which defines the view.
     */
    public CustomCardViewMeds(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view_meds, this, true);
        medText = (TextView) findViewById(R.id.myMedText);
        doseText = (TextView) findViewById(R.id.myDoseText);
        dateText = (TextView) findViewById(R.id.myDateText);
        medImage = (ImageView) findViewById(R.id.medImage);
    }

    /**
     * method to set up each card's views
     *
     * @param med
     */
    public void setCard(Medication med) {
        //set the content for Text views of the card
        if (med.getImageRes().equals("photo")) {
            pdb = new PhotoDatabase(context);
            imageBitmap = DbBitmapUtility.getImage(pdb.getBitmapImage(med.getMedName()));
            medImage.setImageBitmap(imageBitmap);
            medImage.setBackground(null);
        } else {
            medImage.setImageResource(getResources().getIdentifier(med.getImageRes(), "drawable", "com.jay.pea.mhealthapp2"));
        }
        medText.setText(med.getMedString());
        if (med.getFreq() == 1) {
            doseText.setText(med.getDose() + "   Once a day");
        } else {
            doseText.setText(med.getDose() + "   " + med.getFreq() + " x a day");
        }
        DateTime start = new DateTime(med.getMedStart() * 1000l);
        DateTime end = new DateTime(med.getMedEnd() * 1000l);

        // Format for date output
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MMM-yy");
        String date = start.toString(dtf) + " to " + end.toString(dtf);
        dateText.setText(date);


    }
}

