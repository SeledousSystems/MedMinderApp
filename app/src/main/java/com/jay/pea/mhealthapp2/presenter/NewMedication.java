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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.DbBitmapUtility;
import com.jay.pea.mhealthapp2.model.MedDBOpenHelper;
import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.model.MedicationManager;
import com.jay.pea.mhealthapp2.model.PhotoDatabase;
import com.jay.pea.mhealthapp2.safetyMonitor.SafetyMonitorDBOpenHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;


public class NewMedication extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button createMedButton;
    private ImageButton imageButton;
    private Spinner freqSpinner;
    private Medication editMed, fromMed, newMed;
    private TextView medicationET, medNotesET, startET, endET, doseET, alert1, alert2, alert3, alert4, alert5, alert6, toolbarTitle;
    private String medImageString = "med_kit";
    private int freq;
    private MedDBOpenHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    private boolean EDITMODE = false;
    private String TAG = "NEWMEDACTIVITYXX";
    private TextView[] alertArray;
    private boolean alertsOn = true;
    private Calendar startDate, endDate;
    private DateTime today;
    private ToggleButton toggleButton;
    private DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    private String PHOTOSTRING = "photo";
    private Bitmap imageBitmap;
    private SafetyMonitorDBOpenHelper smDBHelper;
    private SQLiteDatabase sdb;
    private int mYear, mMonth, mDay;

    /**
     *     method to get month name from the int passed froma DateTime object
     */
    public static String monthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }

    /**
     * get the data from an intent if data is passed with the intent. If so the activity is set to edit mode
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageButton.setImageBitmap(imageBitmap);
            medImageString = PHOTOSTRING;

        }
    }

    /**
     * method for getting a photo when the user requests
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * onCreate method fo setting up views and listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medication);
        context = this;

        //toolbar title
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        //get today's date at 0:00:00:001
        today = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfDay(1);
        Log.d("TODAY", today.toString());

        //set up text views
        medicationET = (TextView) findViewById(R.id.medicationET);
        medNotesET = (TextView) findViewById(R.id.medNotesET);
        startET = (TextView) findViewById(R.id.startET);
        endET = (TextView) findViewById(R.id.endET);
        doseET = (TextView) findViewById(R.id.doseET);
        alert1 = (TextView) findViewById(R.id.alert1);
        alert2 = (TextView) findViewById(R.id.alert2);
        alert3 = (TextView) findViewById(R.id.alert3);
        alert4 = (TextView) findViewById(R.id.alert4);
        alert5 = (TextView) findViewById(R.id.alert5);
        alert6 = (TextView) findViewById(R.id.alert6);

        //setup default dates and assign values cal is today's date, cal6W is today plus 6weeks (standard prescription length)
        Calendar cal = Calendar.getInstance();
        String startDateString = cal.get(Calendar.DAY_OF_MONTH) + "-" + monthName(cal.get(Calendar.MONTH)) + "-" + cal.get(Calendar.YEAR) % 100;
        // startET.setText(startDateString);
        startDate = cal;
        Log.d(TAG, cal.getTimeInMillis() / 1000 + "");

        //get 6 weeks later inclusive
        Calendar cal6W = Calendar.getInstance();
        cal6W.add(Calendar.DAY_OF_YEAR, 41);

        String endDateString = cal6W.get(Calendar.DAY_OF_MONTH) + "-" + monthName(cal6W.get(Calendar.MONTH)) + "-" + cal6W.get(Calendar.YEAR) % 100;
        //endET.setText(endDateString);
        endDate = cal6W;
        Log.d(TAG, cal6W.getTimeInMillis() / 1000 + "");

        //setup alerts toggle Switch
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setChecked(alertsOn);
        toggleButton.forceLayout();
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertsOn = toggleButton.isChecked();
                setAlertTimesFont(alertsOn);
            }
        });

        //set up frequency spinner
        freqSpinner = (Spinner) findViewById(R.id.freqSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.freq_spinner_array, R.layout.spinner_item);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(adapter);

        freqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setUpAlertTimes(position);
                freq = position + 1;
                setAlertEditable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dialog = new AlertDialog.Builder(NewMedication.this).create();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = View.inflate(context, R.layout.image_dialogue, null);

                dialog.setView(layout);

                dialog.setTitle("Select Icon");

                final ImageView image1 = (ImageView) layout.findViewById(R.id.imageView1);
                final ImageView image2 = (ImageView) layout.findViewById(R.id.imageView2);
                final ImageView image3 = (ImageView) layout.findViewById(R.id.imageView3);
                final ImageView image4 = (ImageView) layout.findViewById(R.id.imageView4);
                final ImageView image5 = (ImageView) layout.findViewById(R.id.imageView5);
                final ImageView image6 = (ImageView) layout.findViewById(R.id.imageView6);

                dialog.setButton("Take Photo",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dispatchTakePictureIntent();
                                dialog.dismiss();
                            }
                        });
                dialog.show();

                final ImageView[] imageViewArray = new ImageView[]{image1, image2, image3, image4, image5, image6};
                for (int i = 0; i < imageViewArray.length; i++) {

                    final ImageView image = imageViewArray[i];

                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, v.getId() + "   " + v.getBackground());
                            int resID = getResources().getIdentifier(v.getTag().toString(), "drawable", "com.jay.pea.mhealthapp2");
                            Drawable drawable = getResources().getDrawable(resID);
                            imageButton.setBackground(drawable);
                            medImageString = v.getTag().toString();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        //set up alarms
        alertArray = new TextView[]{alert1, alert2, alert3, alert4, alert5, alert6};

        for (int i = 0; i < alertArray.length; i++) {
            final TextView alert = alertArray[i];

            alert.setOnClickListener(new View.OnClickListener() {
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
                                    String am_pm = "";
                                    Calendar datetime = Calendar.getInstance();
                                    datetime.set(Calendar.HOUR_OF_DAY, hour);
                                    datetime.set(Calendar.MINUTE, minute);
                                    String minuteString = minute + "";
                                    if (minute < 10) minuteString = "0" + minute;
                                    String alertTime = hour + ":" + minuteString;
                                    alert.setText(alertTime);
                                    Log.d(TAG, datetime + "");
                                }
                            }, hour, minute, false);
                    dpd.show();
                }
            });
        }

        //set alert texts as editable / uneditable  depending on freq selected
        setAlertEditable();

        //set datepicker for start date
        startET.setOnClickListener(new View.OnClickListener() {
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

                                startET.setText(dayOfMonth + "-"
                                        + monthName(monthOfYear) + "-" + year);
                                startDate = Calendar.getInstance();
                                startDate.set(Calendar.YEAR, year);
                                startDate.set(Calendar.MONTH, monthOfYear);
                                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        //set datepicker for start and end date
        endET.setOnClickListener(new View.OnClickListener() {
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
                                endET.setText(dayOfMonth + "-"
                                        + monthName(monthOfYear) + "-" + year);
                                endDate = Calendar.getInstance();
                                endDate.set(Calendar.YEAR, year);
                                endDate.set(Calendar.MONTH, monthOfYear);
                                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        //setup button and onclick listener
        createMedButton = (Button) findViewById(R.id.newMedButton);
        if (editMed == null) createMedButton.setText("Save Medication Changes");
        createMedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emptyField = inputNull();
                if (!emptyField.isEmpty()) {
                    Snackbar.make(v, "Please complete the " + emptyField + " field.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (!EDITMODE && !checkDates().isEmpty()) {
                    Snackbar.make(v, checkDates(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (!EDITMODE && !medExists().isEmpty()) {
                    Snackbar.make(v, medExists(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    AlertDialog.Builder bld = new AlertDialog.Builder(context);
                    //bld.setIcon(R.mipmap.medminder_icon);
                    bld.setTitle("Confirm Medication Details");
                    if (EDITMODE) bld.setTitle("Update Medication Details");

                    final View layout = View.inflate(context, R.layout.confirm_dialogue, null);
                    bld.setView(layout);

                    Medication confirmMed = createMed();

                    if (confirmMed.getImageRes().equals(PHOTOSTRING)) {

                        PhotoDatabase pdb = new PhotoDatabase(context);
                        pdb.addEntry(confirmMed.getMedName(), DbBitmapUtility.getBytes(imageBitmap));

                    }

                    ImageView confirmImage = (ImageView) layout.findViewById(R.id.confirmImage);
                    TextView medName = (TextView) layout.findViewById(R.id.medNameTV);
                    TextView doseName = (TextView) layout.findViewById(R.id.doseNameTV);
                    TextView startDateTV = (TextView) layout.findViewById(R.id.startDateTV);
                    TextView endDateTV = (TextView) layout.findViewById(R.id.endDateTV);
                    TextView alert1TV = (TextView) layout.findViewById(R.id.alert1TV);
                    TextView alert2TV = (TextView) layout.findViewById(R.id.alert2TV);
                    TextView alert3TV = (TextView) layout.findViewById(R.id.alert3TV);
                    TextView alert4TV = (TextView) layout.findViewById(R.id.alert4TV);
                    TextView alert5TV = (TextView) layout.findViewById(R.id.alert5TV);
                    TextView alert6TV = (TextView) layout.findViewById(R.id.alert6TV);
                    TextView alertStateTV = (TextView) layout.findViewById(R.id.alertStateTV);

                    //set texts / images to the medication details

                    if (medImageString.equals(PHOTOSTRING)) {
                        confirmImage.setImageBitmap(imageBitmap);
                    } else {
                        int resID = getResources().getIdentifier(medImageString, "drawable", "com.jay.pea.mhealthapp2");
                        confirmImage.setImageResource(resID);
                    }


                    medName.setText(confirmMed.getMedName());
                    doseName.setText(confirmMed.getDose());
                    startDateTV.setText(new MedicationManager().convertSecsToDateTime(confirmMed.getMedStart()).toString(dtfDate));
                    endDateTV.setText(new MedicationManager().convertSecsToDateTime(confirmMed.getMedEnd()).toString(dtfDate));
                    DateTime[] alertTimesArray = new MedicationManager().getAlertTimes(confirmMed);
                    TextView[] confirmAlertArray = new TextView[]{alert1TV, alert2TV, alert3TV, alert4TV, alert5TV, alert6TV};
                    if (alertsOn) alertStateTV.setText("Alerts are ON");
                    else {
                        alertStateTV.setText("Alerts are OFF");
                        alertStateTV.setTextColor(Color.RED);
                    }

                    //clear the text of the alerts
                    for (int i = 0; i < confirmAlertArray.length; i++) {
                        confirmAlertArray[i].setText("");
                    }
                    //only set the text for the required alerts
                    for (int j = 0; j < confirmMed.getFreq(); j++) {
                        Log.d(TAG, confirmMed.getFreq() + "");
                        confirmAlertArray[j].setText(alertTimesArray[j].toString(dtfTime));
                    }
                    bld.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //                    //add the med to the database
                            dbHelper = new MedDBOpenHelper(context);
                            db = dbHelper.getReadableDatabase();


                            smDBHelper = new SafetyMonitorDBOpenHelper(context);
                            sdb = smDBHelper.getReadableDatabase();
                            if (EDITMODE) {
                                dbHelper.editMed(createMed());
                                smDBHelper.editMed(createMed());

                            } else {
                                dbHelper.addMed(db, createMed());
                                smDBHelper.addMed(sdb, createMed());
                            }

                            Log.d(TAG, dbHelper.getAllMeds().get(0).getMedName());
                            Log.d(TAG, smDBHelper.getAllMeds().get(0).getMedName());
                            Intent i = new Intent(context, MainActivity.class);
                            startActivity(i);
                        }
                    });

                    bld.setNegativeButton("CLOSE", null);
                    Log.d(TAG, "Showing alert dialog: ");
                    bld.create();
                    AlertDialog dialog = bld.show();
                }
            }
        });

        toolbarTitle.setText("Create Medication");
        createMedButton.setText("Save Medication");

        //get med intent info and fill in the form, lock the name and start date
        Intent intent = getIntent();
        if (intent.hasExtra(MyMedsActivity.MED)) {

            EDITMODE = true;
            //get med intent and fill form
            fromMed = (Medication) intent.getSerializableExtra(MyMedsActivity.MED);
            medicationET.setText(fromMed.getMedName());
            medicationET.setEnabled(false);
            medNotesET.setText(fromMed.getMedNotes());
            doseET.setText(fromMed.getDose());
            startET.setText(new DateTime(fromMed.getMedStart() * 1000l).toString(dtfDate));
            startET.setEnabled(false);
            endET.setText(new DateTime(fromMed.getMedEnd() * 1000l).toString(dtfDate));

            freqSpinner.setSelection(fromMed.getFreq() - 1);
            medImageString = fromMed.getImageRes();
            alertsOn = (fromMed.getAlertsOn() == 1);

            DateTime[] alertDTA = new MedicationManager().getAlertTimes(fromMed);

            for (int i = 0; i < alertArray.length; i++) {
                if (i < alertDTA.length)
                    alertArray[i].setText(new DateTime(alertDTA[i]).toString(dtfTime));
                else alertArray[i].setEnabled(false);
            }
            //change toolbar and button text
            toolbarTitle.setText("Edit Medication");
            createMedButton.setText("Save Medication Changes");
        }
    }

    /**
     * method to set alert times
     *
     * @param alertsOn
     */
    private void setAlertTimesFont(boolean alertsOn) {
        alertArray = new TextView[]{alert1, alert2, alert3, alert4, alert5, alert6};

        Log.d(TAG, alertsOn + "");
        if (alertsOn) {
            for (int i = 0; i < alertArray.length; i++) {
                final TextView alert = alertArray[i];
                alert.setTextColor(Color.parseColor("#000000"));
                Log.d(TAG, "false if" + "");
            }
        } else {
            for (int i = 0; i < alertArray.length; i++) {
                final TextView alert = alertArray[i];
                //alert.setTextColor(Color.parseColor("#d3d3d3"));
                Log.d(TAG, "true if" + "");
            }
        }
    }

    /**
     * method to alter the edibility of a alert time dependant
     * on the frequency selected by the user
     */
    private void setAlertEditable() {
        for (int i = 0; i < alertArray.length; i++) {
            alertArray[i].setEnabled(false);
        }

        int timesInt = freqSpinner.getSelectedItemPosition() + 1;
        for (int i = 0; i < timesInt; i++) {
            final TextView alert = alertArray[i];
            alert.setEnabled(true);
            alert.setOnClickListener(new View.OnClickListener() {
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
                                    alert.setText(alertTime);
                                    Log.d(TAG, datetime + "");
                                }
                            }, hour, minute, false);
                    dpd.show();
                }
            });
        }
    }

    /**
     * check if a medication exists or not
     *
     * @return
     */
    private String medExists() {
        dbHelper = new MedDBOpenHelper(context);
        db = dbHelper.getReadableDatabase();
        ArrayList<Medication> meds = dbHelper.getAllMeds();
        for (Medication med : meds) {
            if (medicationET.getText().toString().equals(med.getMedName())) {
                if (startDate.getTimeInMillis() < med.getMedEnd() * 1000l) {
                    return "This medication overlaps an existing medication record with the same name, please check and correct the details";
                }
            }
        }
        return "";
    }

    /**
     * method to check if dates are correct
     *
     * @return
     */
    private String checkDates() {
        if (startET.getText().toString().isEmpty()) {
            return "Please enter the start date of the Medication";
        }
        if (endET.getText().toString().isEmpty()) {
            return "Please enter the end date of the Medication";
        }
        long startDateLong = startDate.getTimeInMillis();
        long endDateLong = endDate.getTimeInMillis();
        if (endDateLong < startDateLong) {
            return "End date is before Start date, please correct.";
        }
        if (startDateLong < today.getMillis()) {
            return "Start date is in the past, please correct.";
        }
        return "";
    }


    /**
     * method to set up alert times
     *
     * @param pos
     */
    private void setUpAlertTimes(int pos) {
        //clear alert text fields prior to setting up alert times
        alertArray = new TextView[]{alert1, alert2, alert3, alert4, alert5, alert6};
        for (int i = 0; i < alertArray.length; i++) {
            final TextView alert = alertArray[i];
            alert.setText("");
            alert.setKeyListener((KeyListener) alert.getTag());
        }

        //fall through used (no break statements) to set alerts depending on freq requested by user
        DateTime now = new DateTime().now();
        DateTime today = now.withMinuteOfHour(0).withSecondOfMinute(0);

        DateTime[] times2 = new DateTime[]{today.withHourOfDay(20), today.withHourOfDay(14), today.withHourOfDay(12), today.withHourOfDay(11), today.withHourOfDay(10)};
        DateTime[] times3 = new DateTime[]{today.withHourOfDay(20), today.withHourOfDay(16), today.withHourOfDay(14), today.withHourOfDay(12)};
        DateTime[] times4 = new DateTime[]{today.withHourOfDay(20), today.withHourOfDay(17), today.withHourOfDay(14)};
        DateTime[] times5 = new DateTime[]{today.withHourOfDay(20), today.withHourOfDay(17)};

        switch (pos) {
            case 5:
                alert6.setText(today.withHourOfDay(20).toString(dtfTime));
            case 4:
                alert5.setText(times5[pos - 4].toString(dtfTime));
            case 3:
                alert4.setText(times4[pos - 3].toString(dtfTime));
            case 2:
                alert3.setText(times3[pos - 2].toString(dtfTime));
            case 1:
                alert2.setText(times2[pos - 1].toString(dtfTime));
            case 0:
                alert1.setText(today.withHourOfDay(8).toString(dtfTime));
        }

        for (int i = 0; i < alertArray.length; i++) {
            final TextView alert = alertArray[i];

            if (alert.getText().toString().isEmpty()) {
                alert.setFocusable(false);
                alert.setFocusableInTouchMode(false);
                alert.setClickable(false);
                Log.d(TAG, alert.getText().toString().equals("") + "");
            }
        }
    }

    /**
     * check for null input for fields
     *
     * @return
     */
    private String inputNull() {
        if (medicationET.getText().toString().isEmpty()) return "Medication Name";
        //if (medNotesET.getText().toString().isEmpty()) return "Medication Notes";
        if (doseET.getText().toString().isEmpty()) return "Dose";
        return "";
    }

    /**
     * method to create a medication from the user input data
     *
     * @return
     */
    private Medication createMed() {
        newMed = new Medication();
        newMed.setMedName(medicationET.getText().toString());
        newMed.setMedNotes(medNotesET.getText().toString());
        newMed.setDose(doseET.getText().toString());
        newMed.setFreq(freqSpinner.getSelectedItemPosition() + 1);
        Log.d(TAG, newMed.getFreq() + "");
        long startDateLong = startDate.getTimeInMillis() / 1000;
        int startDateInt = (int) startDateLong;
        newMed.setMedStart(startDateInt);
        Log.d(TAG, startDateInt + "");
        long endDateLong = endDate.getTimeInMillis() / 1000;
        int endDateInt = (int) endDateLong;
        newMed.setMedEnd(endDateInt);
        Log.d(TAG, endDateInt + "");

        if (medImageString.equals(PHOTOSTRING)) {
        }
        newMed.setImageRes(medImageString);

        int[] alertTimeArrayInt = new int[6];

        for (int i = 0; i < alertArray.length; i++) {
            DateTime dateString;
            DateTime alertTime = new DateTime();

            Log.d(TAG, alertArray[i].getText().toString() + " " + !alertArray[i].getText().toString().isEmpty());

            if (!alertArray[i].getText().toString().isEmpty()) {
                dateString = dtfTime.parseDateTime(alertArray[i].getText().toString());
            } else {
                dateString = dtfTime.parseDateTime(alert1.getText().toString());
            }
            long l = alertTime.withTime(dateString.toLocalTime()).getMillis() / 1000;
            alertTimeArrayInt[i] = (int) l;
        }

        newMed.setAlert1(alertTimeArrayInt[0]);
        newMed.setAlert2(alertTimeArrayInt[1]);
        newMed.setAlert3(alertTimeArrayInt[2]);
        newMed.setAlert4(alertTimeArrayInt[3]);
        newMed.setAlert5(alertTimeArrayInt[4]);
        newMed.setAlert6(alertTimeArrayInt[5]);

        int alertsOnInt = 0;
        if (alertsOn) alertsOnInt = 1;
        newMed.setAlertsOn(alertsOnInt);

        //set up the dose Map from the manager class
        newMed.setDoseMap1(new MedicationManager().buildDoseMap1(newMed, context));
        newMed.setDoseMap2(new MedicationManager().buildDoseMap2(newMed, context));

        return newMed;
    }
}





