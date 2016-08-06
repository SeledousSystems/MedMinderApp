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

package com.jay.pea.mhealthapp2.presenter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jay.pea.mhealthapp2.R;
import com.jay.pea.mhealthapp2.model.DayMedRecord;
import com.jay.pea.mhealthapp2.model.Dose;

import java.util.List;


/**
 * Created by Paul on 3/07/2016.
 *
 * Array adaptor for list view
 */
public class CustomCardViewAdaptorMedRecord extends ArrayAdapter<DayMedRecord> {

    public CustomCardViewAdaptorMedRecord(Context context, List<DayMedRecord> objects) {
        super(context, R.layout.custom_list_view_med_record, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomCardViewMedRecord view = new CustomCardViewMedRecord(getContext());
        view.setCard(getItem(position));
        return view;
    }
}

