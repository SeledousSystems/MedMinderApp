package com.jay.pea.mhealthapp2.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.DbBitmapUtility;
import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.model.PhotoDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Created by Paul on 3/07/2016.
 * <p/>
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardView extends LinearLayout {

    protected TextView medText, doseText, alarmText;
    protected ImageView medImage, takenImage, alarmImage;
    Bitmap imageBitmap;
    PhotoDatabase pdb;
    Context context;

    /**
     * Constructor for the CustomCardView which defines the view.
     *
     * @param context
     */
    public CustomCardView(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view, this, true);
        medText = (TextView) findViewById(R.id.medText);
        doseText = (TextView) findViewById(R.id.doseText);
        medImage = (ImageView) findViewById(R.id.medImage);
        alarmText = (TextView) findViewById(R.id.alarmText);
        alarmImage = (ImageView) findViewById(R.id.alarmImage);
        takenImage = (ImageView) findViewById(R.id.takenImage);
    }

    /**
     * Sets the views for each card
     *
     * @param dose
     */
    public void setCard(Dose dose) {
        DateTime now = new DateTime().now();
        //set the content for Text and Image view of the card
        Medication med = dose.getMedication();
        medText.setText(med.getMedString());
        doseText.setText(med.getDose());
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
        if (dose.getDoseTime().isBefore(now))
            alarmText.setTextColor(getResources().getColor(R.color.red));
        alarmText.setText(dose.getDoseTime().toString(timeFormat));
        if (med.getImageRes().equals("photo")) {
            pdb = new PhotoDatabase(context);
            imageBitmap = DbBitmapUtility.getImage(pdb.getBitmapImage(med.getMedName()));
            medImage.setImageBitmap(imageBitmap);
        } else {
            medImage.setImageResource(getResources().getIdentifier(med.getImageRes(), "drawable", "com.jay.pea.mhealthapp2"));
        }

        DateTime zeroTime = new DateTime(0);
        DateTime zeroPluOneTime = new DateTime(0).plusYears(1);


        if (dose.getAlertOn() == 1) {
            alarmImage.setImageResource(R.drawable.ic_alarm_on_black_24dp);
        } else {
            alarmImage.setImageResource(R.drawable.ic_alarm_off_black_24dp);
        }
        if (dose.getTakenTime().equals(zeroTime)) {
            takenImage.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        } else {
            takenImage.setImageResource(R.drawable.ic_check_box_black_24dp);
        }

        if (dose.getTakenTime().equals(zeroPluOneTime)) {
            takenImage.setVisibility(GONE);
            alarmImage.setVisibility(GONE);
            medText.setTextColor(Color.BLUE);
            doseText.setTextColor(Color.BLUE);
            alarmText.setTextColor(Color.BLUE);
            alarmText.setText("Missed");
        }

    }
}

