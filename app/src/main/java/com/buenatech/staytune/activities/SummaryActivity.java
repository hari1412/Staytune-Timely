package com.buenatech.staytune.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.buenatech.staytune.R;
import com.buenatech.staytune.fragments.WeekdayFragment;
import com.buenatech.staytune.model.Week;
import com.buenatech.staytune.profiles.ProfileManagement;
import com.buenatech.staytune.utils.AlertDialogsHelper;
import com.buenatech.staytune.utils.DbHelper;
import com.buenatech.staytune.utils.PreferenceUtil;
import com.buenatech.staytune.utils.WeekUtils;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yaoandy107.ntut_timetable.CourseTableLayout;
import me.yaoandy107.ntut_timetable.model.CourseInfo;
import me.yaoandy107.ntut_timetable.model.StudentCourse;

public class SummaryActivity extends AppCompatActivity {

    public static final String ACTION_SHOW = "showSummary";

    private String schoolStart;
    private ArrayList<ArrayList<Week>> weeks = new ArrayList<>();
    private DbHelper dbHelper;
    private String[] header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        int[] oldTimes = PreferenceUtil.getStartTime(this);
        schoolStart = oldTimes[0] + ":" + oldTimes[1];

        findViewById(R.id.courseTable).setVisibility(View.GONE);

        if (ACTION_SHOW.equalsIgnoreCase(getIntent().getAction())) {
            dbHelper = new DbHelper(this, ProfileManagement.loadPreferredProfilePosition());
        } else {
            dbHelper = new DbHelper(this);
        }
        weeks = new ArrayList<>();

        boolean startOnSunday = PreferenceUtil.isWeekStartOnSunday(this);
        if (!startOnSunday) {
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_MONDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_TUESDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_WEDNESDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_THURSDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_FRIDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_SATURDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_SUNDAY_FRAGMENT));
        } else {
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_SUNDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_MONDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_TUESDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_WEDNESDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_THURSDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_FRIDAY_FRAGMENT));
            weeks.add(dbHelper.getWeek(WeekdayFragment.KEY_SATURDAY_FRAGMENT));
        }

        header = getResources().getStringArray(R.array.timetable_header);

        if (startOnSunday) {
            List<String> headerList = Arrays.asList(header);
            String sunday = headerList.get(headerList.size() - 1);
            List<String> newHeader = new ArrayList<>();
            newHeader.add(sunday);
            for (int i = 0; i < headerList.size() - 1; i++) {
                newHeader.add(headerList.get(i));
            }
            header = newHeader.toArray(new String[]{});
        }

        if (PreferenceUtil.isSummaryLibrary1(this))
            setupCourseTableLibrary1();
        else
            setupTimetableLibrary2();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, TimeSettingsActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupCourseTableLibrary1() {
        CourseTableLayout courseTable = findViewById(R.id.courseTable);
        courseTable.setVisibility(View.VISIBLE);
        courseTable.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        courseTable.setAnimation(false);

        StudentCourse studentCourse = new StudentCourse();
        ArrayList<CourseInfo> courseInfoList = new ArrayList<>();

        List<List<String>> durationStrings = new ArrayList<>();
        for (int i = 0; i < 7; i++) {

            durationStrings.add(new ArrayList<>());

        }

        for (int j = 0; j < weeks.size(); j++) {
            for (int i = 0; i < weeks.get(j).size(); i++) {
                Week w = weeks.get(j).get(i);
                int start = WeekUtils.getMatchingScheduleBegin(w.getFromTime(), this);
                int end = WeekUtils.getMatchingScheduleEnd(w.getToTime(), this);
                durationStrings.get(j).add(i, generateLessonsString(end - start + 1, start - 1));
            }
        }

        for (int j = 0; j < weeks.size(); j++) {
            for (int i = 0; i < weeks.get(j).size(); i++) {
                Week w = weeks.get(j).get(i);

                String[] courseTimes = new String[7];
                Arrays.fill(courseTimes, "");
                courseTimes[j] = "" + durationStrings.get(j).get(i);

                CustomCourseInfo courseInfo = new CustomCourseInfo(w);
                courseInfo.setCourseTime(courseTimes);
                courseInfoList.add(courseInfo);
            }
        }

        // Set timetable
        studentCourse.setCourseList(courseInfoList);
        courseTable.setHeader(header);
        courseTable.setTextSize(14);
        courseTable.setStudentCourse(studentCourse);

        courseTable.setOnCourseClickListener(view -> {
            CustomCourseInfo item = (CustomCourseInfo) view.getTag();
            final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
            AlertDialogsHelper.getEditSubjectDialog(dbHelper, this, alertLayout, this::recreate, item.getWeek());
        });
    }

    @NonNull
    private static String generateLessonsString(int duration, int hoursBefore) {
        StringBuilder durationString = new StringBuilder();
        for (int i = 1; i <= duration; i++) {
            durationString.append(i + hoursBefore).append(" ");
        }

        return durationString.toString();
    }

    private static class CustomCourseInfo extends CourseInfo {
        @NonNull
        private final Week week;

        public CustomCourseInfo(@NonNull Week w) {
            super();
            week = w;

            StringBuilder name = new StringBuilder(w.getSubject());
            if (w.getTeacher() != null && !w.getTeacher().trim().isEmpty())
                name.append("\n").append(w.getTeacher());
            if (w.getRoom() != null && !w.getTeacher().trim().isEmpty())
                name.append("\n").append(w.getRoom());

            setName(name.toString());

            setColor(w.getColor());
        }

        @NonNull
        public Week getWeek() {
            return week;
        }
    }

    private void setupTimetableLibrary2() {
        List<String> done = new ArrayList<>();
        ArrayList<String> colors = new ArrayList<>();
        List<ArrayList<Schedule>> timetableContent = new ArrayList<>();

        int rows = 0;

        for (int j = 0; j < weeks.size(); j++) {
            for (int i = 0; i < weeks.get(j).size(); i++) {
                ArrayList<Schedule> schedules = new ArrayList<>();
                Week w = weeks.get(j).get(i);
                String subject = w.getSubject();

                if (done.contains(subject))
                    continue;

                int i1 = i + 1;
                for (int j1 = j; j1 < weeks.size(); j1++) {
                    for (; i1 < weeks.get(j1).size(); i1++) {
                        if (weeks.get(j1).get(i1).getSubject().equalsIgnoreCase(subject)) {
                            CustomSchedule schedule = new CustomSchedule(weeks.get(j1).get(i1), j1);
                            schedules.add(schedule);
                            if (schedule.getStartTime().getHour() > rows)
                                rows = schedule.getStartTime().getHour();
                        }
                    }
                    i1 = 0;
                }


                CustomSchedule schedule = new CustomSchedule(w, j);
                schedules.add(schedule);
                if (w.getColor() != -1)
                    colors.add(String.format("#%06X", (0xFFFFFF & w.getColor())));
                else
                    colors.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(this, R.color.grey))));

                if (schedule.getStartTime().getHour() > rows)
                    rows = schedule.getStartTime().getHour();

                done.add(subject);
                timetableContent.add(schedules);
            }
        }

        int startHour = Integer.parseInt(schoolStart.substring(0, schoolStart.indexOf(":")));

        String[] newHeader = new String[8];
        System.arraycopy(header, 0, newHeader, 1, newHeader.length - 1);

        TimetableView timetable = new TimetableView.Builder(this)
                .setColumnCount(6 + (PreferenceUtil.isSevenDays(this) ? 2 : 0))
                .setRowCount(10)
                .setStartTime(startHour)
                .setHeaderTitle(newHeader)
                .setStickerColors(colors.toArray(new String[]{}))
                .build();
        timetable.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        for (ArrayList<Schedule> schedules : timetableContent) {
            timetable.add(schedules);
        }

        ((LinearLayout) findViewById(R.id.summary_linear)).addView(timetable);

//     timetable.setOnStickerSelectEventListener((idx, schedules1) -> {
//            CustomSchedule schedule = (CustomSchedule) schedules1.get(idx);
//            Week week = schedule.getWeek();
//            final View alertLayout = this.getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
//            AlertDialogsHelper.getEditSubjectDialog(this, alertLayout, () -> setupTimetableLibrary2(), week);
//        });

    }

    private static void customSchedule() {
        int a = 10;
        String name, time, date;
        //need to convert the time and date


    }


    private static class CustomSchedule extends Schedule {
        @NonNull
        private final Week week;

        CustomSchedule(@NonNull Week w, int day) {
            super();
            this.week = w;

            int startHour = Integer.parseInt(w.getFromTime().substring(0, w.getFromTime().indexOf(":")));
            int startMinute = Integer.parseInt(w.getFromTime().substring(w.getFromTime().indexOf(":") + 1));

            int endHour = Integer.parseInt(w.getToTime().substring(0, w.getToTime().indexOf(":")));
            int endMinute = Integer.parseInt(w.getToTime().substring(w.getToTime().indexOf(":") + 1));

            setClassTitle(w.getSubject()); // sets subject
            setClassPlace(w.getRoom() + "\n" + w.getTeacher()); // sets place
            setProfessorName(""); // sets professor
            setStartTime(new Time(startHour, startMinute)); // sets the beginning of class time (hour,minute)
            setEndTime(new Time(endHour, endMinute)); // sets the end of class time (hour,minute)
            setDay(day);
        }

        @NonNull
        public Week getWeek() {
            return week;
        }
    }
}
