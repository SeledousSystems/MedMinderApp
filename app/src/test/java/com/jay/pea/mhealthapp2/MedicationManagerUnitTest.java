package com.jay.pea.mhealthapp2;

import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.model.MedicationManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MedicationManagerUnitTest {

    private Medication testMed;
    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");

    @Before
    public void initObjects() {
        testMed = new Medication();
        testMed.setMedName("testMed");
        testMed.setDose("testDose");
        testMed.setAlertsOn(1);
        testMed.setMedStart((int) (new DateTime().now().getMillis() / 1000));
        testMed.setMedEnd((int) (new DateTime().now().getMillis() / 1000));
        testMed.setAlert1(25200);
        testMed.setAlert2(25200);
        testMed.setAlert3(25200);
        testMed.setAlert4(25200);
        testMed.setAlert5(25200);
        testMed.setAlert6(25200);
        testMed.setFreq(1);
        testMed.setDbID(1);
    }

    @Test
    public void convertSecsToDateTimeTestZero() throws Exception {
        assertEquals(new DateTime(0), new MedicationManager().convertSecsToDateTime(0));
    }

    @Test
    public void convertSecsToDateTimeTestNow() throws Exception {
        assertEquals(new DateTime().now().getMillis() / 1000, (new MedicationManager().convertSecsToDateTime((int) (new DateTime().now().getMillis() / 1000))).getMillis() / 1000);
    }

    @Test
    public void convertSecsToDateTimeTestNeg() throws Exception {
        assertEquals(new DateTime(0), new MedicationManager().convertSecsToDateTime(-1000));
    }

    @Test
    public void getMedTimesTestValid() throws Exception {
        DateTime alertTime = new DateTime(0).withHourOfDay(8).withMinuteOfHour(00).withSecondOfMinute(0).withMillisOfSecond(0);
        assertEquals(alertTime, new MedicationManager().getAlertTimes(testMed)[0]);
    }

    @Test
    public void getMedTimesTestNeg() throws Exception {
        testMed.setAlert1(-28800);
        DateTime alertTime = new DateTime(0).withHourOfDay(1).withMinuteOfHour(00).withSecondOfMinute(0).withMillisOfSecond(0);
        assertEquals(alertTime, new MedicationManager().getAlertTimes(testMed)[0]);
    }

    @Test
    public void getMedTimesTestZero() throws Exception {
        testMed.setAlert1(0);
        DateTime alertTime = new DateTime(0).withHourOfDay(1).withMinuteOfHour(00).withSecondOfMinute(0).withMillisOfSecond(0);
        assertEquals(alertTime, new MedicationManager().getAlertTimes(testMed)[0]);
    }

    @Test
    public void getMedTimesTestOneMin() throws Exception {
        testMed.setAlert1(60);
        DateTime alertTime = new DateTime(0).withHourOfDay(1).withMinuteOfHour(1).withSecondOfMinute(0).withMillisOfSecond(0);
        assertEquals(alertTime, new MedicationManager().getAlertTimes(testMed)[0]);
    }

    @Test
    public void getMedTimesTestOneMidnight() throws Exception {
        testMed.setAlert1(82740);
        DateTime alertTime = new DateTime(0).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(0).withMillisOfSecond(0);
        assertEquals(alertTime, new MedicationManager().getAlertTimes(testMed)[0]);
    }

    @Test
    public void convertSecsToDateTimeZero() throws Exception {
        DateTime testTime = new DateTime(0).withHourOfDay(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        assertEquals(testTime, new MedicationManager().convertSecsToDateTime(0));
    }

    @Test
    public void convertSecsToDateTimeMaxInt() throws Exception {
        DateTime testTime = new DateTime(0).withYear(2038).withMonthOfYear(1).withDayOfMonth(19).withHourOfDay(3).withMinuteOfHour(14).withSecondOfMinute(7).withMillisOfSecond(0);
        assertEquals(testTime, new MedicationManager().convertSecsToDateTime(Integer.MAX_VALUE));
    }

    @Test
    public void convertSecsToDateTimeMinInt() throws Exception {
        DateTime testTime = new DateTime(0).withYear(0).withMonthOfYear(1).withDayOfMonth(1).withHourOfDay(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        assertEquals("01-Jan-70", new MedicationManager().convertSecsToDateTime(Integer.MIN_VALUE).toString(dtfDate));
    }


}