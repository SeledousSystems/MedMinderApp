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
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.DayMedRecord;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;


public class MissedMedicationActivity extends AppCompatActivity {

    static String MED = "medication";
    private ArrayList<DayMedRecord> dmrList;
    private Toolbar actionToolbar;
    private TextView toolBar;
    private ListView listView;
    private Intent intent;
    private Medication medication;
    private CustomCardViewAdaptorMissedMeds listViewAdaptorMissedMeds;
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");
    private DateTimeFormatter titleTimeFormat = DateTimeFormat.forPattern("dd-MMM");
    private ArrayList<Medication> medList;
    private ArrayList<Medication> missedMedList;
    private Button ackMissedButton;
    private String TAG = "MEDACTIVITY";
    private Context context;
    private MedDBOpenHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * onCreate method for setting up the views and listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_meds);

        context = this;

        dbHelper = new MedDBOpenHelper(context);
        db = dbHelper.getReadableDatabase();
        actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(actionToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(context, MedicationRecordActivity.class);
                i.putExtra(MED, medList.get(position));
                Log.d(TAG, medList.get(position).getMedName());
                startActivity(i);
            }
        });


        updateListView();
        ackMissedButton = (Button) findViewById(R.id.ackMissedButton);
        ackMissedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Medication med : missedMedList) {

                    for (DateTime doseDue : med.getDoseMap1().keySet()) {

                        DateTime takenDT = med.getDoseMap1().get(doseDue);

                        if (doseDue.isBefore(new DateTime().now()) && takenDT.equals(new DateTime(0))) {
                            med.getDoseMap1().put(doseDue, new DateTime(0).plusYears(1));
                        }
                    }
                    dbHelper.editMed(med);
                }
                updateListView();
            }
        });

    }


    private void updateListView() {


        dbHelper = new MedDBOpenHelper(this);
        medList = dbHelper.getAllMeds();

        missedMedList = new ArrayList<>();

        for (Medication med : medList) {
            for (DateTime doseTakenTime : med.getDoseMap1().keySet()) {

                DateTime doseDueTime = med.getDoseMap1().get(doseTakenTime);
                if (doseDueTime.equals(new DateTime(0))) {
                    if (!missedMedList.contains(med)) missedMedList.add(med);
                }
            }
        }
        listViewAdaptorMissedMeds = new CustomCardViewAdaptorMissedMeds(this, missedMedList);
        listView.setAdapter(listViewAdaptorMissedMeds);
        //dmrListViewAdaptorMedRec.notifyDataSetChanged();
    }
}




