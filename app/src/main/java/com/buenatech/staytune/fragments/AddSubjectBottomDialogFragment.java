package com.buenatech.staytune.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.buenatech.staytune.R;
import com.buenatech.staytune.model.Week;
import com.buenatech.staytune.utils.PreferenceUtil;
import com.buenatech.staytune.utils.WeekUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddSubjectBottomDialogFragment extends BottomSheetDialogFragment {
    private Week week;

    public static AddSubjectBottomDialogFragment newInstance() {
        return new AddSubjectBottomDialogFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_item_schedule, container,
                false);
        TextView teachername = view.findViewById(R.id.titleView);
        TextView views = view.findViewById(R.id.menuTitleView);
        TextView time = view.findViewById(R.id.summaryView);
        TextView teacher = view.findViewById(R.id.teacher);
        Bundle mArgs = getArguments();
        String myValue = mArgs.getString("key");
        String timefrom = mArgs.getString("timefrom");
        String timeto = mArgs.getString("timeto");
        String teachera = mArgs.getString("teacher");

        week = new Week();
        teachername.setText(myValue);
        teacher.setText("Taken by " + teachera);

        if (PreferenceUtil.showTimes(getContext())) {
            time.setText(WeekUtils.localizeTime(getContext(), timefrom) + " - " + WeekUtils.localizeTime(getContext(), timeto));
            Log.e("time", String.valueOf(time));
        } else {
            int start = WeekUtils.getMatchingScheduleBegin(timefrom, getContext());
            int end = WeekUtils.getMatchingScheduleEnd(timeto, getContext());
            if (start == end) {
                time.setText(start + ". " + getContext().getString(R.string.lesson));
            } else {
                time.setText(start + ".-" + end + ". " + getContext().getString(R.string.lesson));
            }
        }

        // get the views and attach the listener

        return view;

    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}