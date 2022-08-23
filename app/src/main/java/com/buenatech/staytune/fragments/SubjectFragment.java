package com.buenatech.staytune.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.buenatech.staytune.R;

public class SubjectFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject, container, false);


        return view;

    }

    public static SubjectFragment newInstance(String text) {

        SubjectFragment subjectFragment = new SubjectFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        subjectFragment.setArguments(b);

        return subjectFragment;
    }
}
