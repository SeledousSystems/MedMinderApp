package com.jay.pea.mhealthapp2;

import com.jay.pea.mhealthapp2.model.Dose;
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
public class DoseUnitTest {

    private Medication testMed;
    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    Dose dose;

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

        dose = new Dose(testMed, new DateTime(), new DateTime(0), 1);
    }

    @Test
    public void getDose() throws Exception {
        assertEquals("testDose", dose.getMedication().getDose());
    }

    @Test
    public void getDoseName() throws Exception {
        assertEquals("testMed", dose.getMedication().getMedName());
    }

    @Test
    public void getDoseAlertOn() throws Exception {
        assertEquals(1, dose.getAlertOn());
    }

    @Test
    public void getDoseFreq() throws Exception {
        assertEquals(1, dose.getMedication().getFreq());
    }

    @Test
    public void getDoseTakenStateFalse() throws Exception {
        assertEquals(false, dose.isDoseTaken());
    }

    @Test
    public void getDoseTakenStateTrue() throws Exception {
        dose.setTakenTime(new DateTime());
        assertEquals(true, dose.isDoseTaken());
    }

    @Test
    public void getDoseTakenTime() throws Exception {
        dose.setTakenTime(new DateTime());
        assertEquals(new DateTime(), dose.getTakenTime());
    }





}