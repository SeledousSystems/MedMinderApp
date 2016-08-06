package com.jay.pea.mhealthapp2;

import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MedicationUnitTest {

    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    private Medication testMed;

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
           }

    @Test
    public void getMedDetails1() throws Exception {
        assertEquals("testDose", testMed.getDose());
    }

    @Test
    public void getMedDetails2() throws Exception {
        assertEquals("testMed", testMed.getMedName());
    }

    @Test
    public void getMedDetails3() throws Exception {
        assertEquals(new DateTime().getMillis() / 1000l, testMed.getMedStart());
    }

    @Test
    public void getMedDetails4() throws Exception {
        assertEquals(new DateTime().getMillis() / 1000l, testMed.getMedEnd());
    }

    @Test
    public void getMedDetails5() throws Exception {
        assertEquals(25200, testMed.getAlert1());
    }

    @Test
    public void getMedDetails6() throws Exception {
        assertEquals(25200, testMed.getAlert2());
    }

    @Test
    public void getMedDetails7() throws Exception {
        assertEquals(25200, testMed.getAlert3());
    }

    @Test
    public void getMedDetails8() throws Exception {
        assertEquals(25200, testMed.getAlert4());
    }

    @Test
    public void getMedDetails9() throws Exception {
        assertEquals(25200, testMed.getAlert5());
    }

    @Test
    public void getMedDetails10() throws Exception {
        assertEquals(25200, testMed.getAlert6());
    }


    @Test
    public void getMedDetails11() throws Exception {
        assertEquals("med_kit", testMed.getImageRes());
    }

    @Test
    public void getMedDetails12() throws Exception {
        assertEquals(null, testMed.getMedNotes());
    }

    @Test
    public void getMedDetails13() throws Exception {
        assertEquals(0, testMed.getDbID());
    }


}