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

package com.jay.pea.mhealthapp2.model;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Paul on 22/06/2016.
 *
 * Class for creating a medication object. POJO.
 *
 */
public class Medication implements Serializable {

    int freq;
    String medName, medNotes, dose;
    String imageRes = "med_kit";
    int medStart, medEnd;
    int alert1, alert2, alert3, alert4, alert5, alert6;
    int alertsOn = 1;
    int dbID;
    HashMap<DateTime, DateTime> doseMap1 = new HashMap();
    HashMap<DateTime, Integer> doseMap2 = new HashMap();

    public HashMap<DateTime, DateTime> getDoseMap1() {
        return doseMap1;
    }

    public void setDoseMap1(HashMap<DateTime, DateTime> doseMap1) {
        this.doseMap1 = doseMap1;
    }

    public HashMap<DateTime, Integer> getDoseMap2() {
        return doseMap2;
    }

    public void setDoseMap2(HashMap<DateTime, Integer> doseMap2) {
        this.doseMap2 = doseMap2;
    }

    public String getImageRes() {
        return imageRes;
    }

    public void setImageRes(String imageRes) {
        this.imageRes = imageRes;
    }

    public int getAlert1() {
        return alert1;
    }

    public void setAlert1(int alert1) {
        this.alert1 = alert1;
    }

    public int getAlert2() {
        return alert2;
    }

    public void setAlert2(int alert2) {
        this.alert2 = alert2;
    }

    public int getAlert3() {
        return alert3;
    }

    public void setAlert3(int alert3) {
        this.alert3 = alert3;
    }

    public int getAlert4() {
        return alert4;
    }

    public void setAlert4(int alert4) {
        this.alert4 = alert4;
    }

    public int getAlert5() {
        return alert5;
    }

    public void setAlert5(int alert5) {
        this.alert5 = alert5;
    }

    public int getAlert6() {
        return alert6;
    }

    public void setAlert6(int alert6) {
        this.alert6 = alert6;
    }

    public int getAlertsOn() {
        return alertsOn;
    }

    public void setAlertsOn(int alertsOn) {
        this.alertsOn = alertsOn;
    }

    public int getMedEnd() {
        return medEnd;
    }

    public void setMedEnd(int medEnd) {
        this.medEnd = medEnd;
    }

    public int getMedStart() {
        return medStart;
    }

    public void setMedStart(int medStart) {
        this.medStart = medStart;
    }

    public int getDbID() {

        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getMedNotes() {
        return medNotes;
    }

    public void setMedNotes(String medNotes) {
        this.medNotes = medNotes;
    }

    public String getMedString() {
        return medName;
    }


}
