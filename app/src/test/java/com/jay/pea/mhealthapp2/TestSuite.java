package com.jay.pea.mhealthapp2;

/**
 * Test Suite to run all JUnit tests.
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {DoseUnitTest.class, FutureDoseUnitTest.class, MedicationManagerUnitTest.class, MedicationUnitTest.class, DayMedRecordUnitTest.class, PresenterUnitTest.class})
public class TestSuite {
}