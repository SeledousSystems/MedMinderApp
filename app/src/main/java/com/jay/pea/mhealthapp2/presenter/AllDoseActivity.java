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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.AllDoseRecord;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

public class AllDoseActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<AllDoseRecord> allDoseAL;
    private CustomCardViewAdaptorAllDoseRecord listViewAdaptorAllDose;
    private MedDBOpenHelper dbHelper;
    private ArrayList<Medication> medList;
    private String TAG = "AllDoseActivity";
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dose);

        Log.d(TAG, "Oncreate");

        updateView();

    }

    private void updateView() {
        listView = (ListView) findViewById(R.id.listView);
        allDoseAL = new ArrayList<>();

        dbHelper = new MedDBOpenHelper(this);
        medList = dbHelper.getAllMeds();

        HashMap<String, Integer> hashMap = new HashMap<>();

        for (Medication med : medList) {

            for (DateTime doseTime : med.getDoseMap1().keySet()) {

                DateTime takenTime = med.getDoseMap1().get(doseTime);
                String doseTimeString = doseTime.toString(dtfDate);

                if (doseTime.isBefore(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)))
                {

                    Log.d(TAG, doseTime.toString(dtfDate) + " -------- " + takenTime.toString(dtfDate));

                    if (!hashMap.containsKey(doseTimeString)) hashMap.put(doseTimeString, 0000);

                    if (takenTime.isBefore(new DateTime(0).plusYears(2))) {
                        hashMap.put(doseTimeString, hashMap.get(doseTimeString) + 1);
                    } else {
                        hashMap.put(doseTimeString, hashMap.get(doseTimeString) + 100);
                    }


                }
            }

        }

        Log.d("string TAG", hashMap.size() + "      ------- ");

        for(String doseDateString: hashMap.keySet()) {

            AllDoseRecord adr = new AllDoseRecord(doseDateString, hashMap.get(doseDateString)%100, hashMap.get(doseDateString)/100 );
            allDoseAL.add(adr);
            Log.d("string TAG", adr.getMissedDoses()+ "      " + adr.getTakenDoses() + "    " + adr.getDate());
        }


        listViewAdaptorAllDose = new CustomCardViewAdaptorAllDoseRecord(this, allDoseAL);
        if(!hashMap.isEmpty())listView
                .setAdapter(listViewAdaptorAllDose);


    }

}
