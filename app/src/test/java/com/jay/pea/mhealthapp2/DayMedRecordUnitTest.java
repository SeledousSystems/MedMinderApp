package com.jay.pea.mhealthapp2;

import com.jay.pea.mhealthapp2.model.DayMedRecord;
import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.Medication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class DayMedRecordUnitTest {

    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    DayMedRecord dmr;
    Dose dose;
    ArrayList<Dose> doseAL;
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
        testMed.setDbID(1);

        dose = new Dose(testMed, new DateTime(), new DateTime(0), 1);

        doseAL = new ArrayList<>();
        doseAL.add(dose);

        dmr = new DayMedRecord(new DateTime(), doseAL, testMed);
    }

    @Test
    public void geDayMedRecordDetails2() throws Exception {
        assertEquals(dose, dmr.getDoseArray().get(0));
    }

    @Test
    public void geDayMedRecordDetails3() throws Exception {
        assertEquals(testMed, dmr.getMed());
    }

    @Test
    public void geDayMedRecordDetails4() throws Exception {
        assertEquals("testMed", dmr.getMed().getMedName());
    }

    @Test
    public void geDayMedRecordDetails5() throws Exception {
        assertEquals("testDose", dmr.getMed().getDose());
    }

    @Test
    public void geDayMedRecordDetails6() throws Exception {
        assertEquals(1, dmr.getMed().getFreq());
    }

    @Test
    public void geDayMedRecordDetails7() throws Exception {
        assertEquals(new DateTime().getMillis() / 1000l, dmr.getMed().getMedStart());
    }

    @Test
    public void geDayMedRecordDetails8() throws Exception {
        assertEquals(new DateTime().getMillis() / 1000l, dmr.getMed().getMedEnd());
    }
}