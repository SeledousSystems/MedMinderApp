package com.jay.pea.mhealthapp2;

import com.jay.pea.mhealthapp2.model.Dose;
import com.jay.pea.mhealthapp2.model.Medication;
import com.jay.pea.mhealthapp2.presenter.FutureMeds;
import com.jay.pea.mhealthapp2.presenter.MainActivity;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class PresenterUnitTest {

    DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd-MMM-yy");
    Dose dose;
    private Medication testMed;

    @Before
    public void initObjects() {
    }

    @Test
    public void getMonthStringZero() throws Exception {
        assertEquals("Jan", new FutureMeds().monthName(0));
    }

    @Test
    public void getMonthStringEnd() throws Exception {
        assertEquals("Dec", new FutureMeds().monthName(11));
    }

    @Test
    public void getDayStringZero() throws Exception {
        assertEquals("Mon", new MainActivity().getDayName(1));
    }

    @Test
    public void getDayStringEnd() throws Exception {
        assertEquals("Sun", new MainActivity().getDayName(7));
    }



}