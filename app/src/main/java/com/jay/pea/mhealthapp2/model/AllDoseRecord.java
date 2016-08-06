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

/**
 * Created by Paul on 26/07/2016.
 */
public class AllDoseRecord {

    String dateString;
    int missedDoses = 0;
    int takenDoses = 0;

    public AllDoseRecord(String dateString, int missedDoses, int takenDoses) {
        this.dateString = dateString;
        this.missedDoses = missedDoses;
        this.takenDoses = takenDoses;
    }

    public String getDate() {
        return dateString;
    }

    public void setDate(String dateString) {
        this.dateString = dateString;
    }

    public int getMissedDoses() {
        return missedDoses;
    }

    public void setMissedDoses(int missedDoses) {
        this.missedDoses = missedDoses;
    }

    public int getTakenDoses() {
        return takenDoses;
    }

    public void setTakenDoses(int takenDoses) {
        this.takenDoses = takenDoses;
    }
}
