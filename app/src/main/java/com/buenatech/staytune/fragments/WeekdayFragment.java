/*
 * Copyright (c) 2020 Felix Hollederer
 *     This file is part of GymWenApp.
 *
 *     GymWenApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GymWenApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with GymWenApp.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.buenatech.staytune.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.buenatech.staytune.R;
import com.buenatech.staytune.adapters.WeekAdapter;
import com.buenatech.staytune.model.Week;
import com.buenatech.staytune.utils.DbHelper;
import com.buenatech.staytune.utils.FragmentHelper;

import java.util.ArrayList;

public class WeekdayFragment extends Fragment {
    public static final String KEY_MONDAY_FRAGMENT = "Monday";
    public static final String KEY_TUESDAY_FRAGMENT = "Tuesday";
    public static final String KEY_WEDNESDAY_FRAGMENT = "Wednesday";
    public static final String KEY_THURSDAY_FRAGMENT = "Thursday";
    public static final String KEY_FRIDAY_FRAGMENT = "Friday";
    public static final String KEY_SATURDAY_FRAGMENT = "Saturday";
    public static final String KEY_SUNDAY_FRAGMENT = "Sunday";

    @Nullable
    private DbHelper db;
    private ListView listView;
    private LinearLayout empty_view;

    @Nullable
    private WeekAdapter adapter;
    private View view;

    private final String key;
    private Animation animation;

    public WeekdayFragment(String key) {
        super();
        this.key = key;
    }

    public WeekdayFragment() {
        super();
        this.key = KEY_MONDAY_FRAGMENT;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekday, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupAdapter(view);
        setupListViewMultiSelect();
    }

    private void setupAdapter(@NonNull View view) {
        db = new DbHelper(getActivity());
        listView = view.findViewById(R.id.daylist);
        empty_view = view.findViewById(R.id.empty_view);
        ArrayList<Week> weeks = db.getWeek(key);
        adapter = new WeekAdapter(db, (AppCompatActivity) requireActivity(), listView, 0, weeks);
        listView.setEmptyView(empty_view);
        listView.setAdapter(adapter);
    }

    private void setupListViewMultiSelect() {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(FragmentHelper.setupListViewMultiSelect((AppCompatActivity) getActivity(), listView, adapter, db));
    }

    public String getKey() {
        return key;
    }
}
