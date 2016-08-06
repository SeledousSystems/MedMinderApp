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

import java.util.ArrayList;

/**
 * Class for creating day medical record objects for displaying all doses by day and the doses
 * status (missed, taken, etc). POJO.
 *
 * Created by Paul on 15/07/2016.
 */
public class DayMedRecord {

    Medication med;
    DateTime date;
    ArrayList<Dose> doseArrayList;

    public DayMedRecord(DateTime date, ArrayList<Dose> doseArrayList, Medication med) {
        this.date = date;
        this.doseArrayList = doseArrayList;
        this.med = med;
    }

    public DateTime getDate() {
        return date;
    }

    public Medication getMed() {
        return med;
    }

    public void setMed(Medication med) {
        this.med = med;
    }

    public ArrayList<Dose> getDoseArray() {
        return doseArrayList;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public ArrayList<Dose> getDoseArrayList() {
        return doseArrayList;
    }

    public void setDoseArrayList(ArrayList<Dose> doseArrayList) {
        this.doseArrayList = doseArrayList;
    }
}
