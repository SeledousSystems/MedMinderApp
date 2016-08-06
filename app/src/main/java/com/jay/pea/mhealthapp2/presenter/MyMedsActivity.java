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
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.MedComparator;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.safetyMonitor.SafetyMonitorDBOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

public class MyMedsActivity extends AppCompatActivity {

    static String MED = "medication";
    private static int DBID = 100;
    private ArrayList<Medication> medList;
    private CustomCardViewAdaptorMeds doseListViewAdaptorMeds;
    private ListView listView;
    private String TAG = "MYMEDSACTIVITY";
    private MedDBOpenHelper dbHelper;
    private SafetyMonitorDBOpenHelper smDBHelper;
    private SQLiteDatabase db;
    private Context context;
    private Button allDosesButton;

    /**
     * onCreate method for setting up views and listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meds);

        dbHelper = new MedDBOpenHelper(this);
        db = dbHelper.getReadableDatabase();
        smDBHelper = new SafetyMonitorDBOpenHelper(this);

        context = this;

        listView = (ListView) findViewById(R.id.listView);
        updateListView();

        registerForContextMenu(listView);

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


        allDosesButton = (Button) findViewById(R.id.allDosesButton);
        allDosesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), AllDoseActivity.class);
                startActivity(i);
            }
        });

    }

    /**
     * onCreateContextMenu
     */
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        this.getMenuInflater().inflate(R.menu.my_med_menu, menu);
    }

    /**
     * item selected form context menu method
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                Intent i = new Intent(this, NewMedication.class);
                i.putExtra(MED, medList.get(info.position));
                startActivity(i);
                return true;
            case R.id.delete:
                dbHelper.deleteMedID(medList.get(info.position).getDbID());
                smDBHelper.deleteMedID(medList.get(info.position).getDbID());
                updateListView();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * method for updating the listview for when data changes
     */
    private void updateListView() {
        medList = dbHelper.getAllMeds();
        Collections.sort(medList, new MedComparator());
        doseListViewAdaptorMeds = new CustomCardViewAdaptorMeds(this, medList);
        listView.setAdapter(doseListViewAdaptorMeds);
        doseListViewAdaptorMeds.notifyDataSetChanged();
    }

    /**
     * when back pressed send use to Main Activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
