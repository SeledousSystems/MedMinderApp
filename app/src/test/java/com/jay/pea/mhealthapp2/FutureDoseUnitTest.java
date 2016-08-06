package com.jay.pea.mhealthapp2;

import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.FutureDoses;
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
public class FutureDoseUnitTest {

    private Medication testMed;
    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    Dose dose;
    FutureDoses fd;

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
        fd = new FutureDoses("testDose", new DateTime(), 1,"testMed", new DateTime());
    }

    @Test
    public void getFutureDoseDetails1() throws Exception {
        assertEquals("testDose", fd.getDose());
    }

    @Test
    public void getFutureDoseDetails2() throws Exception {
        assertEquals("testMed", fd.getMedName());
    }

    @Test
    public void getFutureDoseDetails3() throws Exception {
        assertEquals(new DateTime().toString(dtfDate), fd.getStartDate().toString(dtfDate));
    }

    @Test
    public void getFutureDoseDetails4() throws Exception {
        assertEquals(new DateTime(), fd.getEndDate());
    }

    @Test
    public void getFutureDoseDetails5() throws Exception {
        assertEquals(1, fd.getFreq());
    }






}