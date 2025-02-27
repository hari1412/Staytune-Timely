package com.buenatech.staytune.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.buenatech.staytune.fragments.WeekdayFragment;
import com.buenatech.staytune.model.Exam;
import com.buenatech.staytune.model.Homework;
import com.buenatech.staytune.model.Note;
import com.buenatech.staytune.model.Teacher;
import com.buenatech.staytune.model.Week;
import com.buenatech.staytune.profiles.ProfileManagement;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;
import java.util.Calendar;

public class DbHelper extends SQLiteOpenHelper {
    private Context context;
    private static final int DB_VERSION = 7;
    private static final String DB_NAME = "timetabledb";
    private static final String TIMETABLE = "timetable";
    private static final String TIMETABLE_ODD = "timetable_odd";
    private static final String WEEK_ID = "id";
    private static final String WEEK_SUBJECT = "subject";
    private static final String WEEK_FRAGMENT = "fragment";
    private static final String WEEK_TEACHER = "teacher";
    private static final String WEEK_ROOM = "room";
    private static final String WEEK_FROM_TIME = "fromtime";
    private static final String WEEK_TO_TIME = "totime";
    private static final String WEEK_COLOR = "color";

    private static final String HOMEWORKS = "homeworks";
    private static final String HOMEWORKS_ID = "id";
    private static final String HOMEWORKS_SUBJECT = "subject";
    private static final String HOMEWORKS_DESCRIPTION = "description";
    private static final String HOMEWORKS_DATE = "date";
    private static final String HOMEWORKS_COLOR = "color";

    private static final String NOTES = "notes";
    private static final String NOTES_ID = "id";
    private static final String NOTES_TITLE = "title";
    private static final String NOTES_TEXT = "text";
    private static final String NOTES_COLOR = "color";

    private static final String TEACHERS = "teachers";
    private static final String TEACHERS_ID = "id";
    private static final String TEACHERS_NAME = "name";
    private static final String TEACHERS_POST = "post";
    private static final String TEACHERS_PHONE_NUMBER = "phonenumber";
    private static final String TEACHERS_EMAIL = "email";
    private static final String TEACHERS_COLOR = "color";

    private static final String EXAMS = "exams";
    private static final String EXAMS_ID = "id";
    private static final String EXAMS_SUBJECT = "subject";
    private static final String EXAMS_TEACHER = "teacher";
    private static final String EXAMS_ROOM = "room";
    private static final String EXAMS_DATE = "date";
    private static final String EXAMS_TIME = "time";
    private static final String EXAMS_COLOR = "color";

    public DbHelper(Context context) {
        super(context, getDBName(ProfileManagement.getSelectedProfilePosition()), null, DB_VERSION);
        this.context = context;
    }

    public DbHelper(Context context, int selectedProfile) {
        super(context, getDBName(selectedProfile), null, DB_VERSION);
        this.context = context;
    }

    private DbHelper(Context context, boolean odd) {
        super(context, DB_NAME + "_odd", null, 6);
        this.context = context;
    }

    @NonNull
    public static String getDBName(int selectedProfile) {
        String dbName;
        if (selectedProfile == 0)
            dbName = DB_NAME; //If the app was installed before the profiles were added
        else
            dbName = DB_NAME + "_" + selectedProfile;
        return dbName;
    }

    public void onCreate(@NonNull SQLiteDatabase db) {
        String CREATE_TIMETABLE = "CREATE TABLE IF NOT EXISTS " + TIMETABLE + "("
                + WEEK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WEEK_SUBJECT + " TEXT,"
                + WEEK_FRAGMENT + " TEXT,"
                + WEEK_TEACHER + " TEXT,"
                + WEEK_ROOM + " TEXT,"
                + WEEK_FROM_TIME + " TEXT,"
                + WEEK_TO_TIME + " TEXT,"
                + WEEK_COLOR + " INTEGER" + ")";

        String CREATE_TIMETABLE_ODD = "CREATE TABLE IF NOT EXISTS " + TIMETABLE_ODD + "("
                + WEEK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WEEK_SUBJECT + " TEXT,"
                + WEEK_FRAGMENT + " TEXT,"
                + WEEK_TEACHER + " TEXT,"
                + WEEK_ROOM + " TEXT,"
                + WEEK_FROM_TIME + " TEXT,"
                + WEEK_TO_TIME + " TEXT,"
                + WEEK_COLOR + " INTEGER" + ")";

        String CREATE_HOMEWORKS = "CREATE TABLE IF NOT EXISTS " + HOMEWORKS + "("
                + HOMEWORKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HOMEWORKS_SUBJECT + " TEXT,"
                + HOMEWORKS_DESCRIPTION + " TEXT,"
                + HOMEWORKS_DATE + " TEXT,"
                + HOMEWORKS_COLOR + " INTEGER" + ")";

        String CREATE_NOTES = "CREATE TABLE IF NOT EXISTS " + NOTES + "("
                + NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NOTES_TITLE + " TEXT,"
                + NOTES_TEXT + " TEXT,"
                + NOTES_COLOR + " INTEGER" + ")";

        String CREATE_TEACHERS = "CREATE TABLE IF NOT EXISTS " + TEACHERS + "("
                + TEACHERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TEACHERS_NAME + " TEXT,"
                + TEACHERS_POST + " TEXT,"
                + TEACHERS_PHONE_NUMBER + " TEXT,"
                + TEACHERS_EMAIL + " TEXT,"
                + TEACHERS_COLOR + " INTEGER" + ")";

        String CREATE_EXAMS = "CREATE TABLE IF NOT EXISTS " + EXAMS + "("
                + EXAMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EXAMS_SUBJECT + " TEXT,"
                + EXAMS_TEACHER + " TEXT,"
                + EXAMS_ROOM + " TEXT,"
                + EXAMS_DATE + " TEXT,"
                + EXAMS_TIME + " TEXT,"
                + EXAMS_COLOR + " INTEGER" + ")";

        db.execSQL(CREATE_TIMETABLE);
        db.execSQL(CREATE_TIMETABLE_ODD);
        db.execSQL(CREATE_HOMEWORKS);
        db.execSQL(CREATE_NOTES);
        db.execSQL(CREATE_TEACHERS);
        db.execSQL(CREATE_EXAMS);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        switch (oldVersion) {
            default:
            case 6:
                migrateEvenOddWeeks(db);
                break;
        }
    }

    private void migrateEvenOddWeeks(SQLiteDatabase db) {
        String[] keys = new String[]{WeekdayFragment.KEY_MONDAY_FRAGMENT,
                WeekdayFragment.KEY_TUESDAY_FRAGMENT,
                WeekdayFragment.KEY_WEDNESDAY_FRAGMENT,
                WeekdayFragment.KEY_THURSDAY_FRAGMENT,
                WeekdayFragment.KEY_FRIDAY_FRAGMENT,
                WeekdayFragment.KEY_SATURDAY_FRAGMENT,
                WeekdayFragment.KEY_SUNDAY_FRAGMENT};

        ArrayList<Week> oldOddWeeks = new ArrayList<>();
        DbHelper oldDbHelper = new DbHelper(context, true);
        for (String key : keys) {
            oldOddWeeks.addAll(oldDbHelper.getWeek(key, TIMETABLE));
        }

        for (Week week : oldOddWeeks) {
            insertWeek(week, TIMETABLE_ODD, db);
        }
    }

    /**
     * Methods for Week fragments
     **/
    private String getTimetableTable() {
        return getTimetableTable(Calendar.getInstance());
    }

    private String getTimetableTable(Calendar now) {
        if (PreferenceUtil.isEvenWeek(context, now))
            return TIMETABLE;
        else
            return TIMETABLE_ODD;
    }

    public void insertWeek(Week week) {
        insertWeek(week, getTimetableTable(), this.getWritableDatabase());
    }

    private void insertWeek(@NonNull Week week, String tableName, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEEK_SUBJECT, week.getSubject());
        contentValues.put(WEEK_FRAGMENT, week.getFragment());
        contentValues.put(WEEK_TEACHER, week.getTeacher());
        contentValues.put(WEEK_ROOM, week.getRoom());
        contentValues.put(WEEK_FROM_TIME, week.getFromTime());
        contentValues.put(WEEK_TO_TIME, week.getToTime());
        contentValues.put(WEEK_COLOR, week.getColor());
        db.insert(tableName, null, contentValues);
        db.update(tableName, contentValues, WEEK_FRAGMENT, null);
//        db.close();
    }

    public void deleteWeekById(@NonNull Week week) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(getTimetableTable(), WEEK_ID + " = ? ", new String[]{String.valueOf(week.getId())});
        db.close();
    }

    public void updateWeek(@NonNull Week week) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEEK_SUBJECT, week.getSubject());
        contentValues.put(WEEK_TEACHER, week.getTeacher());
        contentValues.put(WEEK_ROOM, week.getRoom());
        contentValues.put(WEEK_FROM_TIME, week.getFromTime());
        contentValues.put(WEEK_TO_TIME, week.getToTime());
        contentValues.put(WEEK_COLOR, week.getColor());
        db.update(getTimetableTable(), contentValues, WEEK_ID + " = " + week.getId(), null);
        db.close();
    }

    public ArrayList<Week> getWeek(String fragment) {
        return getWeek(fragment, Calendar.getInstance());
    }

    public ArrayList<Week> getWeek(String fragment, Calendar now) {
        return getWeek(fragment, getTimetableTable(now));
    }

    @SuppressLint("Range")
    @NonNull
    private ArrayList<Week> getWeek(String fragment, String dbName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Week> weeklist = new ArrayList<>();
        Week week;
        Cursor cursor = db.rawQuery("SELECT * FROM ( SELECT * FROM " + dbName + " ORDER BY " + WEEK_FROM_TIME + " ) WHERE " + WEEK_FRAGMENT + " LIKE '" + fragment + "%'", null);
        while (cursor.moveToNext()) {
            week = new Week();
            week.setId(cursor.getInt(cursor.getColumnIndex(WEEK_ID)));
            week.setFragment(cursor.getString(cursor.getColumnIndex(WEEK_FRAGMENT)));
            week.setSubject(cursor.getString(cursor.getColumnIndex(WEEK_SUBJECT)));
            week.setTeacher(cursor.getString(cursor.getColumnIndex(WEEK_TEACHER)));
            week.setRoom(cursor.getString(cursor.getColumnIndex(WEEK_ROOM)));
            week.setFromTime(cursor.getString(cursor.getColumnIndex(WEEK_FROM_TIME)));
            week.setToTime(cursor.getString(cursor.getColumnIndex(WEEK_TO_TIME)));
            week.setColor(cursor.getInt(cursor.getColumnIndex(WEEK_COLOR)));
            weeklist.add(week);
        }

        return weeklist;
    }

    public void insertHomework(@NonNull Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOMEWORKS_SUBJECT, homework.getSubject());
        contentValues.put(HOMEWORKS_DESCRIPTION, homework.getDescription());
        contentValues.put(HOMEWORKS_DATE, homework.getDate());
        contentValues.put(HOMEWORKS_COLOR, homework.getColor());
        db.insert(HOMEWORKS, null, contentValues);
        db.close();
    }

    public void updateHomework(@NonNull Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOMEWORKS_SUBJECT, homework.getSubject());
        contentValues.put(HOMEWORKS_DESCRIPTION, homework.getDescription());
        contentValues.put(HOMEWORKS_DATE, homework.getDate());
        contentValues.put(HOMEWORKS_COLOR, homework.getColor());
        db.update(HOMEWORKS, contentValues, HOMEWORKS_ID + " = " + homework.getId(), null);
        db.close();
    }


    public void deleteHomeworkById(@NonNull Homework homework) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HOMEWORKS, HOMEWORKS_ID + " = ? ", new String[]{String.valueOf(homework.getId())});
        db.close();
    }


    @SuppressLint("Range")
    @NonNull
    public ArrayList<Homework> getHomework() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Homework> homelist = new ArrayList<>();
        Homework homework;
        Cursor cursor = db.rawQuery("SELECT * FROM " + HOMEWORKS + " ORDER BY datetime(" + HOMEWORKS_DATE + ") ASC", null);
        while (cursor.moveToNext()) {
            homework = new Homework();
            homework.setId(cursor.getInt(cursor.getColumnIndex(HOMEWORKS_ID)));
            homework.setSubject(cursor.getString(cursor.getColumnIndex(HOMEWORKS_SUBJECT)));
            homework.setDescription(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DESCRIPTION)));
            homework.setDate(cursor.getString(cursor.getColumnIndex(HOMEWORKS_DATE)));
            homework.setColor(cursor.getInt(cursor.getColumnIndex(HOMEWORKS_COLOR)));
            homelist.add(homework);
        }
        cursor.close();
        db.close();
        return homelist;
    }

    /**
     * Methods for Notes
     **/
    public void insertNote(@NonNull Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_TITLE, note.getTitle());
        contentValues.put(NOTES_TEXT, note.getText());
        contentValues.put(NOTES_COLOR, note.getColor());
        db.insert(NOTES, null, contentValues);
        db.close();
    }

    public void updateNote(@NonNull Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_TITLE, note.getTitle());
        contentValues.put(NOTES_TEXT, note.getText());
        contentValues.put(NOTES_COLOR, note.getColor());
        db.update(NOTES, contentValues, NOTES_ID + " = " + note.getId(), null);
        db.close();
    }

    public void deleteNoteById(@NonNull Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES, NOTES_ID + " =? ", new String[]{String.valueOf(note.getId())});
        db.close();
    }

    @SuppressLint("Range")
    @NonNull
    public ArrayList<Note> getNote() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> notelist = new ArrayList<>();
        Note note;
        Cursor cursor = db.rawQuery("SELECT * FROM " + NOTES, null);
        while (cursor.moveToNext()) {
            note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndex(NOTES_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndex(NOTES_TITLE)));
            note.setText(cursor.getString(cursor.getColumnIndex(NOTES_TEXT)));
            note.setColor(cursor.getInt(cursor.getColumnIndex(NOTES_COLOR)));
            notelist.add(note);
        }
        cursor.close();
        db.close();
        return notelist;
    }

    public void insertSubjectT(@NonNull Week week) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEEK_SUBJECT, week.getSubject());
        contentValues.put(WEEK_COLOR, week.getColor());
        contentValues.put(WEEK_FROM_TIME, week.getFromTime());
        contentValues.put(WEEK_TO_TIME, week.getToTime());
        sqLiteDatabase.insert(WEEK_ID, null, contentValues);
        sqLiteDatabase.close();


    }

    public void insertTeacher(@NonNull Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEACHERS_NAME, teacher.getName());
        contentValues.put(TEACHERS_POST, teacher.getPost());
        contentValues.put(TEACHERS_PHONE_NUMBER, teacher.getPhonenumber());
        contentValues.put(TEACHERS_EMAIL, teacher.getEmail());
        contentValues.put(TEACHERS_COLOR, teacher.getColor());
        db.insert(TEACHERS, null, contentValues);
        db.close();
    }

    public void updateTeacher(@NonNull Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEACHERS_NAME, teacher.getName());
        contentValues.put(TEACHERS_POST, teacher.getPost());
        contentValues.put(TEACHERS_PHONE_NUMBER, teacher.getPhonenumber());
        contentValues.put(TEACHERS_EMAIL, teacher.getEmail());
        contentValues.put(TEACHERS_COLOR, teacher.getColor());
        db.update(TEACHERS, contentValues, TEACHERS_ID + " = " + teacher.getId(), null);
        db.close();
    }

    public void deleteTeacherById(@NonNull Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEACHERS, TEACHERS_ID + " =? ", new String[]{String.valueOf(teacher.getId())});
        db.close();
    }

    @SuppressLint("Range")
    @NonNull
    public ArrayList<Teacher> getTeacher() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Teacher> teacherlist = new ArrayList<>();
        Teacher teacher;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TEACHERS, null);
        while (cursor.moveToNext()) {
            teacher = new Teacher();
            teacher.setId(cursor.getInt(cursor.getColumnIndex(TEACHERS_ID)));
            teacher.setName(cursor.getString(cursor.getColumnIndex(TEACHERS_NAME)));
            teacher.setPost(cursor.getString(cursor.getColumnIndex(TEACHERS_POST)));
            teacher.setPhonenumber(cursor.getString(cursor.getColumnIndex(TEACHERS_PHONE_NUMBER)));
            teacher.setEmail(cursor.getString(cursor.getColumnIndex(TEACHERS_EMAIL)));
            teacher.setColor(cursor.getInt(cursor.getColumnIndex(TEACHERS_COLOR)));
            teacherlist.add(teacher);
        }
        cursor.close();
        db.close();
        return teacherlist;
    }

    /**
     * Methods for Exams vice datasets
     **/
    public void insertExam(@NonNull Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAMS_SUBJECT, exam.getSubject());
        contentValues.put(EXAMS_TEACHER, exam.getTeacher());
        contentValues.put(EXAMS_ROOM, exam.getRoom());
        contentValues.put(EXAMS_DATE, exam.getDate());
        contentValues.put(EXAMS_TIME, exam.getTime());
        contentValues.put(EXAMS_COLOR, exam.getColor());
        db.insert(EXAMS, null, contentValues);
        db.close();
    }

    public void updateExam(@NonNull Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAMS_SUBJECT, exam.getSubject());
        contentValues.put(EXAMS_TEACHER, exam.getTeacher());
        contentValues.put(EXAMS_ROOM, exam.getRoom());
        contentValues.put(EXAMS_DATE, exam.getDate());
        contentValues.put(EXAMS_TIME, exam.getTime());
        contentValues.put(EXAMS_COLOR, exam.getColor());
        db.update(EXAMS, contentValues, EXAMS_ID + " = " + exam.getId(), null);
        db.close();
    }


    public void deleteExamById(@NonNull Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EXAMS, EXAMS_ID + " =? ", new String[]{String.valueOf(exam.getId())});
        db.close();
    }

    @SuppressLint("Range")
    @NonNull
    public ArrayList<Exam> getExam() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Exam> examslist = new ArrayList<>();
        Exam exam;
        Cursor cursor = db.rawQuery("SELECT * FROM " + EXAMS, null);
        while (cursor.moveToNext()) {
            exam = new Exam();
            exam.setId(cursor.getInt(cursor.getColumnIndex(EXAMS_ID)));
            exam.setSubject(cursor.getString(cursor.getColumnIndex(EXAMS_SUBJECT)));
            exam.setTeacher(cursor.getString(cursor.getColumnIndex(EXAMS_TEACHER)));
            exam.setRoom(cursor.getString(cursor.getColumnIndex(EXAMS_ROOM)));
            exam.setDate(cursor.getString(cursor.getColumnIndex(EXAMS_DATE)));
            exam.setTime(cursor.getString(cursor.getColumnIndex(EXAMS_TIME)));
            exam.setColor(cursor.getInt(cursor.getColumnIndex(EXAMS_COLOR)));
            examslist.add(exam);
        }
        cursor.close();
        db.close();
        return examslist;
    }


    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE_ODD);
        db.execSQL("DROP TABLE IF EXISTS " + HOMEWORKS);
        db.execSQL("DROP TABLE IF EXISTS " + NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TEACHERS);
        db.execSQL("DROP TABLE IF EXISTS " + EXAMS);
        db.close();
        onCreate(this.getWritableDatabase());
    }
}
