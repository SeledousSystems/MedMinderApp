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

import java.util.Comparator;

/**
 * Created by Paul on 11/07/2016.
 *
 * Class to compare to DayMedRecord objects for sorting a list
 */
public class DMRComparator implements Comparator<DayMedRecord> {
    @Override
    public int compare(DayMedRecord d1, DayMedRecord d2) {
        return d1.getDate().compareTo(d2.getDate());
    }
}