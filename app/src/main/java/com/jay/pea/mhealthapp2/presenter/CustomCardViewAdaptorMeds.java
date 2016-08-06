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
import com.jay.pea.mhealthapp2.model.Medication;

import java.util.List;


/**
 * Created by Paul on 3/07/2016.
 *
 * Array adaptor for list view
 */
public class CustomCardViewAdaptorMeds extends ArrayAdapter<Medication> {

    public CustomCardViewAdaptorMeds(Context context, List<Medication> objects) {
        super(context, R.layout.custom_list_view_meds, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomCardViewMeds view = new CustomCardViewMeds(getContext());
        view.setCard(getItem(position));
        return view;
    }
}

