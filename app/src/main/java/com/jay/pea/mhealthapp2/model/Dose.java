/*
 * Copyright (c) 2016.
 *
 * The source code contained in this file remains the intellectual property of Paul Wright (PeaJay).
 * Any reuse, adaption or replication of this code, without express permission, is prohibited.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 *  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied.
 *
 *
 */

package com.jay.pea.mhealthapp2.model;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Class for creating dose objects for displaying a dose. POJO.
 *
 * Created by Paul on 15/07/2016.
 */
public class Dose implements Serializable {

    DateTime doseTime;
    DateTime takenTime;
    Medication medication;
    boolean doseTaken = false;
    int alertOn;

    public Dose(Medication medication, DateTime doseTime, DateTime takenTime, int alertOn) {
        this.doseTime = doseTime;
        this.takenTime = takenTime;
        this.medication = medication;
        this.alertOn = alertOn;
    }

    public boolean isDoseTaken() {
        return doseTaken;
    }

    public void setDoseTaken(boolean doseTaken) {
        this.doseTaken = doseTaken;
        this.medication.getDoseMap1().put(doseTime, this.takenTime);
    }

    public int getAlertOn() {
        return alertOn;
    }

    public void setAlertOn(int alertOn) {
        this.alertOn = alertOn;
    }

    public DateTime getDoseTime() {
        return doseTime;
    }

    public void setDoseTime(DateTime doseTime) {
        this.doseTime = doseTime;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public DateTime getTakenTime() {
        return takenTime;
    }

    public void setTakenTime(DateTime takenTime) {

        this.takenTime = new DateTime().withHourOfDay(takenTime.getHourOfDay()).withMinuteOfHour(takenTime.getMinuteOfHour());
        if (takenTime != new DateTime(0)) this.doseTaken = true;
        else this.doseTaken = false;
    }
}
