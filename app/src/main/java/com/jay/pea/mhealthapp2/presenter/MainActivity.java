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

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.AlertManager;
import com.jay.pea.mhealthapp2.model.DbBitmapUtility;
import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.DoseComparator;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.model.PhotoDatabase;
import com.jay.pea.mhealthapp2.safetyMonitor.SafetyMonitor;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME_1 = "Alerts_Fired";
    public static final String PREFS_NAME_2 = "Alert_Times";
    private final Handler handler = new Handler();
    protected CustomCardViewAdaptor doseListViewAdaptor;
    private ArrayList<Medication> medList;
    private ArrayList<Dose> doseList;
    private ArrayList<Dose> todaysDoseList;
    private Toolbar mActionBarToolbar;
    private TextView toolBar;
    private Button addMedButton, futureMedButton, myMedButton;
    private ListView listView;
    private String TAG = "MAINACTIVITY";
    private MedDBOpenHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    private DateTime today;
    private DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    private boolean doseTakenChecked = false;
    private boolean doseTaken;
    private TextView timeTakenTV;
    private Dose dose;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private TextView safetyMonitorTV;
    private LinearLayout safetyMonLL;
    private int safetyProgress = 0;
    private boolean systemSafe = true;
    private Bitmap imageBitmap;
    private PhotoDatabase pdb;
    private CoordinatorLayout clView;
    private DateTimeFormatter dtfDayMonth = DateTimeFormat.forPattern("dd MMM");
    private SharedPreferences alertCount;
    private SharedPreferences.Editor editor;
    private SharedPreferences alertTimes;
    private SharedPreferences.Editor editorTimes;
    private int missedDoseCount = 0;

    /**
     * method to get the day name from a day array with ints
     *
     * @param i
     * @return
     */
    public String getDayName(int i) {
        String[] dayArray = new String[]{"Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"};
        return dayArray[i - 1];
    }

    /**
     * create Long click MENU
     */
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        this.getMenuInflater().inflate(R.menu.my_main_menu, menu);
    }

    /**
     * context lonk item selected menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        switch (item.getItemId()) {
            case R.id.viewmed:
                Intent i = new Intent(this, MyMedsActivity.class);
                startActivity(i);
                return true;

            case R.id.viewdose:
                createDoseDialog(listPosition);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Create dose dioaloge method for building the dose pop up for changing dose data such as alert on/off alert time and taken time
     *
     * @param position
     */
    private void createDoseDialog(int position) {
        clView = (CoordinatorLayout) findViewById(R.id.coordlayout);

        final Dose selDose = todaysDoseList.get(position);

        doseTaken = selDose.getTakenTime() == new DateTime(0);

        //build the dose dialogue for recording a dose taken
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        final AlertDialog dialog = bld.create();
        //bld.setIcon(R.mipmap.medminder_icon);
        bld.setTitle("Dose Details");

        final View layout = View.inflate(context, R.layout.dose_dialogue, null);
        bld.setView(layout);

        //assign views for the Alert Dialog
        ImageView doseIV = (ImageView) layout.findViewById(R.id.doseIV);
        TextView medName = (TextView) layout.findViewById(R.id.medNameTV);
        TextView doseName = (TextView) layout.findViewById(R.id.doseNameTV);
        //TextView freqTV = (TextView) layout.findViewById(R.id.freqTV);
        TextView doseTimeTV = (TextView) layout.findViewById(R.id.doseTimeTV);
        timeTakenTV = (TextView) layout.findViewById(R.id.takenTimeTV);
        TextView alertStateTV = (TextView) layout.findViewById(R.id.alertStateTV);
        checkBox = (CheckBox) layout.findViewById(R.id.checkBox);
        final ToggleButton alertTB = (ToggleButton) layout.findViewById(R.id.alertTB);
        alertTB.setChecked(selDose.getMedication().getDoseMap2().get(selDose.getDoseTime()) == 1);

        //get the dose taken value and
        doseTaken = selDose.getTakenTime() == new DateTime(0);
        doseTakenChecked = doseTaken;
        //set texts, checkbox and date to the dose details
        takenBoxChecked();
        if (selDose.getMedication().getImageRes().equals("photo")) {
            pdb = new PhotoDatabase(context);
            imageBitmap = DbBitmapUtility.getImage(pdb.getBitmapImage(selDose.getMedication().getMedName()));
            doseIV
                    .setImageBitmap(imageBitmap);
        } else {
            doseIV.setImageResource(getResources().getIdentifier(selDose.getMedication().getImageRes(), "drawable", "com.jay.pea.mhealthapp2"));
        }
        doseTimeTV.setText(selDose.getDoseTime().toString(dtfTime));
        if (!selDose.getMedication().getDoseMap1().get(selDose.getDoseTime()).equals(new DateTime(0))) {
            checkBox.setChecked(true);
            Log.d(TAG, selDose.getMedication().getDoseMap1().get(selDose.getDoseTime()) + " takenDose ");
            timeTakenTV.setText(selDose.getMedication().getDoseMap1().get(selDose.getDoseTime()).toString(dtfTime));
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doseTakenChecked = isChecked;
                Log.d(TAG, doseTakenChecked + "");
                takenBoxChecked();
                alertTB.setChecked(!isChecked);
                Log.d(TAG, doseTakenChecked + "");
            }
        });

        medName.setText(selDose.getMedication().getMedName());
        doseName.setText(selDose.getMedication().getDose());
        Resources res = getResources();
        String[] freqArray = res.getStringArray(R.array.freq_spinner_array);
        //freqTV.setText(freqArray[selDose.getMedication().getFreq() - 1]);

        //set up turn alert off/on
        alertTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) selDose.setAlertOn(1);
                else selDose.setAlertOn(1);
            }
        });

        //add timepicker to time taken TV to record time dose taken
        timeTakenTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog dpd = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hour,
                                                  int minute) {
                                //set up correct time format
                                Calendar datetime = Calendar.getInstance();
                                datetime.set(Calendar.HOUR_OF_DAY, hour);
                                datetime.set(Calendar.MINUTE, minute);
                                String minuteString = minute + "";
                                if (minute < 10) minuteString = "0" + minute;
                                String alertTime = hour + ":" + minuteString;
                                timeTakenTV.setText(alertTime);
                                Log.d(TAG, datetime + "");
                            }
                        }, hour, minute, false);
                dpd.show();
            }
        });
        alertStateTV.setText("Alert is ");
        if (selDose.getAlertOn() == 1) {
        } else {
            alertStateTV.setTextColor(Color.RED);
        }

        bld.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper = new MedDBOpenHelper(context);

                //set dose alert to the alert on/off
                selDose.getMedication().getDoseMap2().put(selDose.getDoseTime(), selDose.getAlertOn());

                //modify the med data for dose taken
                if (doseTakenChecked) {
                    DateTime dateString = dtfTime.parseDateTime(timeTakenTV.getText().toString());
                    selDose.setTakenTime(dateString);
                    //record dose taken time
                    selDose.getMedication().getDoseMap1().put(selDose.getDoseTime(), selDose.getTakenTime());
                    //turn alert off
                    selDose.getMedication().getDoseMap2().put(selDose.getDoseTime(), 0);
                } else {

                    selDose.setTakenTime(new DateTime(0));
                    //record dose taken time
                    selDose.getMedication().getDoseMap1().put(selDose.getDoseTime(), new DateTime(0));

                }
                //update medication
                dbHelper.editMed(selDose.getMedication());

                dialog.dismiss();
                //tell the user what happened set up dose taken String.
                String dosetakenString;
                if (doseTakenChecked)
                    dosetakenString = "taken at " + selDose.getTakenTime().toString(dtfTime);
                else {
                    dosetakenString = "not yet taken";
                }

                String alertStringOut = "The Alert for this dose is OFF.";
                if (alertTB.isChecked())
                    alertStringOut = "The Alert for this dose is ON.";

                String snackBarOut = "Your " + selDose.getMedication().getDose() + " of " + selDose.getMedication().getMedName() + " has been recorded as " + dosetakenString + ".";

                //send snackbar to tell the user
                Snackbar.make(clView, snackBarOut + " " + alertStringOut, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //update the display
                updateListView();
                runSafetyDisplay();

            }
        });
        bld.setNegativeButton("CLOSE", null);
        Log.d(TAG, "Showing alert dialog: ");
        bld.show();

        updateListView();
    }

    /**
     * method to change data and views bsed on checkbox for dose input from the user
     */
    public void takenBoxChecked() {
        if (doseTakenChecked) {
            checkBox.setChecked(true);
            timeTakenTV.setEnabled(true);
            timeTakenTV.setText(new DateTime().toString(dtfTime));
        } else {
            doseTakenChecked = false;
            timeTakenTV.setText("");
            timeTakenTV.setEnabled(false);
        }
    }

    /**
     * onCreate method, sets up views, onclick listeners and lists.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //context object
        context = this;

        // setup tool bar
        mActionBarToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        today = new DateTime().now();

        String dayName = getDayName(today.getDayOfWeek());

        toolBar = (TextView) findViewById(R.id.toolbar_title);
        toolBar.setText("Doses for " + dayName + " " + today.toString(dtfDayMonth));


        //setup buttons
        addMedButton = (Button) findViewById(R.id.addMedButton);
        futureMedButton = (Button) findViewById(R.id.futureMedButton);
        myMedButton = (Button) findViewById(R.id.myMedButton);
        addMedButton.setOnClickListener(this);
        futureMedButton.setOnClickListener(this);
        myMedButton.setOnClickListener(this);

        //setup listview
        listView = (ListView) findViewById(R.id.listView);

        //set up long click menu
        registerForContextMenu(listView);

        updateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, final View view,
                                                                    int position, long id) {
                                                createDoseDialog(position);
                                            }
                                        }
        );
        //get the system safe status
        systemSafe = new SafetyMonitor(context).runSafetyChecks();
        //run the progress bar and safety output
        runSafetyDisplay();
    }

    /**
     * method running the safety monitor display
     */
    public void runSafetyDisplay() {
        safetyMonLL = (LinearLayout) findViewById(R.id.safetyMonLL);
        safetyMonLL.setOnClickListener(this);
        safetyMonitorTV = (TextView) findViewById(R.id.safetyMonitorTV);

        missedDoseCount = 0;

        for (Dose missedDose : todaysDoseList) {

            if (missedDose.getDoseTime().isBefore(new DateTime().now()) && missedDose.getMedication().getDoseMap1().get(missedDose.getDoseTime()).equals(new DateTime(0))) {
                missedDoseCount++;
            }
        }

        if (missedDoseCount > 0) {
            safetyMonLL.setVisibility(View.VISIBLE);
            safetyMonitorTV.setText("You have " + missedDoseCount + " missed doses for today. Tap to view.");
            if (missedDoseCount == 1)
                safetyMonitorTV.setText("You have " + missedDoseCount + " missed dose for today. Tap to view.");

        } else {
            safetyMonLL.setVisibility(View.GONE);
        }

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.setProgress(safetyProgress);
//        progressBar.setMax(100);
//        safetyProgress += 1;
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//
//                        if (safetyProgress >= 100) {
//                            progressBar.setVisibility(View.GONE);
//                            safetyMonitorTV.setVisibility(View.VISIBLE);
//
//                            if (systemSafe) {
//                                safetyMonitorTV.setText("System Is Operating Correctly");
//                                safetyMonLL.setBackgroundResource(R.drawable.border_list_med_black_blue);
//                            } else {
//
//                                safetyMonitorTV.setText("System Has Detected An Error Tap To View Monitor Status");
//                                safetyMonLL.setBackgroundResource(R.drawable.border_list_med_black_red);
//                            }
//                        } else {
//                            runSafetyDisplay();
//                        }
//                    }
//                });
//            }
//        }, 15);
    }

    /**
     * onCreat options menu method
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Options selected method with if statement to test for id clicked by user
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Toast.makeText(context, "Please see www.", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.run_diagnostics) {
            Intent intent;
            intent = new Intent(context, SafetyMonitorDisplay.class);
            startActivity(intent);
            return true;
        }

//        if (id == R.id.test_safety_monitor) {
//            Toast.makeText(context, "Testing Safety Monitor", Toast.LENGTH_LONG).show();
//            testSafetyMonitor();
//            return true;
//        }
//        if (id == R.id.run_safety_monitor) {
//            Toast.makeText(context, "Running Safety Monitor", Toast.LENGTH_LONG).show();
//            runSafetyMonitor();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

//    /**
//     * method that runs the safety view for to alert the user to safety issues
//     */
//    public void runSafetyMonitor() {
//        progressBar.setVisibility(View.VISIBLE);
//        safetyMonitorTV.setVisibility(View.GONE);
//        systemSafe = true;
//        safetyProgress = 0;
//        runSafetyDisplay();
//    }
//
//    /**
//     * test method to test the safety monitor, non functional
//     */
//    public void testSafetyMonitor() {
//        progressBar.setVisibility(View.VISIBLE);
//        safetyMonitorTV.setVisibility(View.GONE);
//        systemSafe = !systemSafe;
//        safetyProgress = 0;
//        runSafetyDisplay();
//    }

    /**
     * onClick method to handle click event for the buttons and the empty list view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.about:
                break;

            case R.id.addMedButton:
                intent = new Intent(v.getContext(), NewMedication.class);
                startActivity(intent);
                break;

            case R.id.empty:
                intent = new Intent(v.getContext(), NewMedication.class);
                startActivity(intent);
                break;

            case R.id.myMedButton:
                intent = new Intent(v.getContext(), MyMedsActivity.class);
                startActivity(intent);
                break;
            case R.id.futureMedButton:
                intent = new Intent(v.getContext(), FutureMeds.class);
                startActivity(intent);
                break;

            case R.id.safetyMonLL:
                intent = new Intent(v.getContext(), MissedMedicationActivity.class);
                startActivity(intent);
                break;

            case R.id.alarmll:
                if (dose.getAlertOn() == 1) {
                    dose.setAlertOn(0);
                } else {
                    dose.setAlertOn(1);
                }
                break;
        }
    }

    /**
     * update Listview method for updating the list when the data set changes.
     */
    public void updateListView() {
        medList = new ArrayList<>();
        medList.clear();
        dbHelper = new MedDBOpenHelper(this);
        db = dbHelper.getReadableDatabase();
        medList = dbHelper.getAllMeds();
        doseList = new ArrayList<>();
        todaysDoseList = new ArrayList<>();
        doseList.clear();
        todaysDoseList.clear();

        //get all doses and add to doseList
        for (Medication med : medList) {
            HashMap<DateTime, DateTime> doseMap1 = med.getDoseMap1();
            HashMap<DateTime, Integer> doseMap2 = med.getDoseMap2();

            for (DateTime doseDate : doseMap1.keySet()) {
                //get takenDate and alertOn
                DateTime takenDate = doseMap1.get(doseDate);
                int alertOn = doseMap2.get(doseDate);
                Dose dose = new Dose(med, doseDate, takenDate, alertOn);
                doseList.add(dose);
            }
        }

        //get today's doses
        for (Dose dose : doseList) {
            DateTimeFormatter dayFormat = DateTimeFormat.forPattern("dd MMM yyyy");
            if (dose.getDoseTime().toString(dayFormat).equals(today.toString(dayFormat))) {
                todaysDoseList.add(dose);
            }
            //sort collect, add to adaptor
            Collections.sort(todaysDoseList, new DoseComparator());
            doseListViewAdaptor = new CustomCardViewAdaptor(this, todaysDoseList);
            listView.setAdapter(doseListViewAdaptor);
        }

        //clear the alert count and dose count prefs
        alertCount = context.getSharedPreferences(PREFS_NAME_1, 0);
        editor = alertCount.edit();
        for (Medication med : medList) {
            editor.putInt(med.getMedName(), 0);
            editor.putStringSet(med.getDose(), null);
            editor.apply();
        }

        //clear the alert time prefs
        alertTimes = context.getSharedPreferences(PREFS_NAME_2, 0);
        editorTimes = alertTimes.edit();
        editorTimes.clear();
        editorTimes.apply();

        //set today's dose alerts
        for (Dose dose : todaysDoseList) {

            String doseMedName = dose.getMedication().getMedName();
            AlertManager am = new AlertManager();
            Log.d("AlertManagerMain", dose.getMedication().getMedName() + "  " + dose.getDoseTime().toString(dtfDate) + "  " + dose.getDoseTime().toString(dtfTime));
            am.setAlerts(context, dose);

            //record the number of alerts and save to preferences to allow safety monitor to check the number of alerts fired.
            if (alertCount.contains(doseMedName)) {
                editor.putInt(doseMedName, alertCount.getInt(doseMedName, 0) + 1);
            } else {
                editor.putInt(doseMedName, 1);
            }
            editor.apply();
            Log.d(TAG, alertCount.getInt(doseMedName, 0) + " " + doseMedName + " " + dose.getDoseTime().toString(dtfTime));

            String doseTime = dose.getDoseTime().toString(dtfTime);
            String doseName = dose.getMedication().getDose();

            alertTimes = context.getSharedPreferences(PREFS_NAME_2, 0);

            SharedPreferences ss = getSharedPreferences("db", 0);
            Set<String> hs = ss.getStringSet("set", new HashSet<String>());
            Set<String> in = new HashSet<String>(hs);
            in.add(String.valueOf(hs.size() + 1));
            ss.edit().putStringSet("set", in).apply(); // brevity

            //record the times of alerts and save to preferences to allow safety monitor to check the times of alerts fired.
            if (alertTimes.contains(doseName)) {
                Set<String> alertTimesHS = alertTimes.getStringSet(doseName, new HashSet<String>()); //get the existing
                Set<String> inAlertTimesHS = new HashSet<String>(alertTimesHS); //create a new set with the preferences set as the constructor
                inAlertTimesHS.add(dose.getDoseTime().toString(dtfTime)); // add to the new set this dose time
                alertTimes.edit().putStringSet(doseName, inAlertTimesHS).apply(); //save the addition to the Stringset and commit
            } else {
                Set<String> inAlertTimesHS = new HashSet<String>();
                inAlertTimesHS.add(dose.getDoseTime().toString(dtfTime));
                alertTimes.edit().putStringSet(doseName, inAlertTimesHS).apply();
            }
            editor.apply();
            Log.d(TAG, " " + doseName);
        }
    }

    /**
     * method to handle the view when the list is empty
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.empty);
        empty.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(empty);
    }

    /**
     * method to handle when the app is resumed or unpaused
     */
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //Refresh view
        updateListView();
        runSafetyDisplay();
    }

    /**
     * method to handle when the app is restarted
     */
    @Override
    public void onRestart() {
        super.onRestart();
        //Refresh view
        updateListView();
        runSafetyDisplay();
    }

}
