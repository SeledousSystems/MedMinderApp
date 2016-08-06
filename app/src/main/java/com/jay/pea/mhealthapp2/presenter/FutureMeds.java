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

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.FutureDoses;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;

public class FutureMeds extends AppCompatActivity {

    private ArrayList<Medication> medList;
    private ListView listView;
    private ArrayList<FutureDoses> fdal = new ArrayList<>();
    private CustomCardViewAdaptorMedFuture listViewAdaptorMedFut;
    private MedDBOpenHelper dbHelper;
    private TextView startTV, endTV;
    private Context context;
    private Calendar startDate, endDate;
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yyyy");
    private CoordinatorLayout cl;
    private int mYear, mMonth, mDay;

    /**
     * method to get month name
     *
     * @param month
     * @return
     */
    public static String monthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }

    /**
     * onCreate method to setup views and click listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_meds);
        context = this;


        cl = (CoordinatorLayout) findViewById(R.id.coordlayout);

        listView = (ListView) findViewById(R.id.listView);
        startTV = (TextView) findViewById(R.id.startET);

        startTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                startTV.setText(dayOfMonth + "-"
                                        + monthName(monthOfYear) + "-" + year);
                                startDate = Calendar.getInstance();
                                startDate.set(Calendar.YEAR, year);
                                startDate.set(Calendar.MONTH, monthOfYear);
                                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                listView.invalidate();
                                if (!endTV.getText().toString().isEmpty()) updateListView();
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();

            }
        });

        endTV = (TextView) findViewById(R.id.endET);
        endTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                endTV.setText(dayOfMonth + "-"
                                        + monthName(monthOfYear) + "-" + year);
                                endDate = Calendar.getInstance();
                                endDate.set(Calendar.YEAR, year);
                                endDate.set(Calendar.MONTH, monthOfYear);
                                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                listView.invalidate();
                                if (!startTV.getText().toString().isEmpty()) updateListView();
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();

            }
        });

    }

    /**
     * method to update the listview when the data changes
     */
    private void updateListView() {

        dbHelper = new MedDBOpenHelper(this);
        medList = dbHelper.getAllMeds();
        fdal = new ArrayList<>();

        DateTime startDate = dtfDate.parseDateTime(startTV.getText().toString());
        DateTime endDate = dtfDate.parseDateTime(endTV.getText().toString());

        int diffDays = Days.daysBetween(startDate.toLocalDate(), endDate.toLocalDate()).getDays();
        if (diffDays < 0) {
            Snackbar.make(cl, "Please ensure the end date is after the start date", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            for (Medication med : medList) {
                FutureDoses fd = new FutureDoses(med.getDose(), endDate, med.getFreq(), med.getMedName(), startDate);
                fdal.add(fd);
            }
            listViewAdaptorMedFut = new CustomCardViewAdaptorMedFuture(this, fdal);
            listView.setAdapter(listViewAdaptorMedFut);
        }

    }
}
