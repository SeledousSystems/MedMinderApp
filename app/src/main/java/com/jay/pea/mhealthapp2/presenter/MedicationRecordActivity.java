package com.jay.pea.mhealthapp2.presenter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.DMRComparator;
import com.jay.pea.mhealthapp2.model.DayMedRecord;
import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.DoseComparator;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;


public class MedicationRecordActivity extends AppCompatActivity {

    private  ArrayList<DayMedRecord> dmrList;
    private Toolbar actionToolbar;
    private TextView toolBar;
    private ListView listView;
    private Intent intent;
    private Medication medication;
    private String TAG = "MEDACTIVITY";
    private MedDBOpenHelper dbHelper;
    private SQLiteDatabase db;
    private DateTime timeNow;
    private CustomCardViewAdaptorMedRecord dmrListViewAdaptorMedRec;
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");

    /**
     * onCreate method for setting up the views and listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(actionToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        intent = getIntent();
        medication = (Medication) intent.getSerializableExtra(MyMedsActivity.MED);

        DateTimeFormatter titleTimeFormat = DateTimeFormat.forPattern("dd-MMM");

        toolBar = (TextView) findViewById(R.id.toolbar);
        String medTitle = medication.getMedName() + " Dose Record";

//                " (" + new DateTime(medication.getMedStart() * 1000l).toString(titleTimeFormat) + " ~ " +
//                new DateTime(medication.getMedEnd() * 1000l).toString(titleTimeFormat) + ")";

        toolBar.setText(medTitle);

        listView = (ListView) findViewById(R.id.listView);
        updateListView();
    }


    private void updateListView() {

        dmrList = new ArrayList();
        dmrList.clear();

        ArrayList<DateTime> medicationDates = new ArrayList<>();

        for (DateTime doseTime : medication.getDoseMap1().keySet()) {
            DateTime dmrDate = new DateTime().withDate(doseTime.getYear(), doseTime.getMonthOfYear(), doseTime.getDayOfMonth()).withMillisOfDay(0);
            if (!medicationDates.contains(dmrDate)) {
                medicationDates.add(dmrDate);
                //Log.d(TAG, "   " + medicationDates.size() + "   " + dmrDate.toString(dtfDate));
            }

        }

        for (DateTime recordDate : medicationDates) {

            ArrayList<Dose> doseArrayList = new ArrayList();

            for (DateTime doseTime : medication.getDoseMap1().keySet()) {

                //Log.d(TAG, doseTime.toString(dtfDate) + " = doseTime        recordTime = " + recordDate.toString(dtfDate) + "   " + doseTime.toString(dtfDate).equals(recordDate.toString(dtfDate)));
                if (doseTime.toString(dtfDate).equals(recordDate.toString(dtfDate))) {
                    Dose dose = new Dose(medication, doseTime, medication.getDoseMap1().get(doseTime), medication.getDoseMap2().get(doseTime));
                    doseArrayList.add(dose);
                }

            }
            Collections.sort(doseArrayList, new DoseComparator());

            Dose[] doseArray = doseArrayList.toArray(new Dose[doseArrayList.size()]);

            //Log.d(TAG, doseArray.length + " length vvvvvvvvvvvvvv " + doseArray[2]);
            DayMedRecord dmr = new DayMedRecord(recordDate, doseArrayList, medication);

            dmrList.add(dmr);
        }


        Collections.sort(dmrList, new DMRComparator());

        for (DayMedRecord dmr : dmrList) {

            Log.d("xxxxxxx", dmr.getDate() + "  ");
        }


        dmrListViewAdaptorMedRec = new CustomCardViewAdaptorMedRecord(this, dmrList);
        listView.setAdapter(dmrListViewAdaptorMedRec);
        //dmrListViewAdaptorMedRec.notifyDataSetChanged();
    }
}




