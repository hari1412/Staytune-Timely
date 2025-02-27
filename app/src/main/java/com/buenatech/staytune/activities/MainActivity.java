package com.buenatech.staytune.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.buenatech.staytune.BuildConfig;
import com.buenatech.staytune.R;
import com.buenatech.staytune.adapters.FragmentsTabAdapter;
import com.buenatech.staytune.fragments.WeekdayFragment;
import com.buenatech.staytune.receivers.DoNotDisturbReceiversKt;
import com.buenatech.staytune.utils.AlertDialogsHelper;
import com.buenatech.staytune.utils.CubeOutPageTransformer;
import com.buenatech.staytune.utils.CustomTabLayout;
import com.buenatech.staytune.utils.DbHelper;
import com.buenatech.staytune.utils.NotificationUtil;
import com.buenatech.staytune.utils.PrefHelper;
import com.buenatech.staytune.utils.PreferenceUtil;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentsTabAdapter adapter;
    private ViewPager viewPager;
    CustomTabLayout tabLayout;
    private static final int showNextDayAfterSpecificHour = 24;
    View view;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    public static final int REQUEST_CODE_INTRO = 1;
    public static final String PREF_KEY_FIRST_START = "PREF_KEY_FIRST_START";
    private TextView dateTimeDisplay;
    private SimpleDateFormat dateFormat;
    private String date;
    PrefHelper prefHelper;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    FirebaseUser firebaseUser;
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralThemeNoActionBar(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        prefHelper = new PrefHelper(this);
        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this
                , GoogleSignInOptions.DEFAULT_SIGN_IN);

//        if (prefHelper.getFirstStart(this).equals("")) {
//            Intent intent = new Intent(this, MainIntroActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_INTRO);
//        }
        // Initialize firebase auth
        // Check condition


        initAll();
        inAppReview();
        userData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this
                , GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    private void initAll() {
        view = findViewById(R.id.fab);
        // Initialize firebase user
        NotificationUtil.sendNotificationCurrentLesson(this, false);
        PreferenceUtil.setDoNotDisturb(this, PreferenceUtil.doNotDisturbDontAskAgain(this));
        setupWeeksTV();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        headerview.findViewById(R.id.nav_header_main_settings).setOnClickListener((View v) -> startActivity(new Intent(this, SettingsActivity.class)));
        TextView title = headerview.findViewById(R.id.nav_header_main_title);
        CircleImageView ivImage = headerview.findViewById(R.id.nav_header_main_icon);
        //TextView userEmail  = headerview.findViewById(R.id.nav_header_main_desc);
        LinearLayout holder = findViewById(R.id.holder);
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            title.setText("Good Morning, " + firebaseUser.getDisplayName());
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            title.setText("Good Afternoon, " + firebaseUser.getDisplayName());

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            title.setText("Good Evening, " + firebaseUser.getDisplayName());
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            title.setText("Good Night, " + firebaseUser.getDisplayName());
        }

        if (firebaseUser != null) {
            Glide.with(MainActivity.this)
                    .load(firebaseUser.getPhotoUrl())
                    .into(ivImage);
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float scaleFactor = 7f;
                float slideX = drawerView.getWidth() * slideOffset;
                holder.setTranslationX(slideX);
                holder.setScaleX(1 - (slideOffset / scaleFactor));
                holder.setScaleY(1 - (slideOffset / scaleFactor));
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        setupFragments();


        setupCustomDialog();

    }

    public void userData() {

    }

    private void setupWeeksTV() {
        TextView weekView = findViewById(R.id.main_week_tV);
        if (PreferenceUtil.isTwoWeeksEnabled(this)) {
            weekView.setVisibility(View.VISIBLE);
            if (PreferenceUtil.isEvenWeek(this, Calendar.getInstance()))
                weekView.setText(R.string.even_week);
            else
                weekView.setText(R.string.odd_week);
        } else
            weekView.setVisibility(View.GONE);
    }

    private void setupFragments() {
        adapter = new FragmentsTabAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
//        viewPager.setPageTransformer(true, new CubeOutPageTransformer());
        tabLayout = findViewById(R.id.tabLayout);

        WeekdayFragment mondayFragment = new WeekdayFragment(WeekdayFragment.KEY_MONDAY_FRAGMENT);
        WeekdayFragment tuesdayFragment = new WeekdayFragment(WeekdayFragment.KEY_TUESDAY_FRAGMENT);
        WeekdayFragment wednesdayFragment = new WeekdayFragment(WeekdayFragment.KEY_WEDNESDAY_FRAGMENT);
        WeekdayFragment thursdayFragment = new WeekdayFragment(WeekdayFragment.KEY_THURSDAY_FRAGMENT);
        WeekdayFragment fridayFragment = new WeekdayFragment(WeekdayFragment.KEY_FRIDAY_FRAGMENT);
        WeekdayFragment saturdayFragment = new WeekdayFragment(WeekdayFragment.KEY_SATURDAY_FRAGMENT);
        WeekdayFragment sundayFragment = new WeekdayFragment(WeekdayFragment.KEY_SUNDAY_FRAGMENT);

        boolean startOnSunday = PreferenceUtil.isWeekStartOnSunday(this);
        boolean showWeekend = PreferenceUtil.isSevenDays(this);

        if (!startOnSunday) {
            adapter.addFragment(mondayFragment, getResources().getString(R.string.monday));
            adapter.addFragment(tuesdayFragment, getResources().getString(R.string.tuesday));
            adapter.addFragment(wednesdayFragment, getResources().getString(R.string.wednesday));
            adapter.addFragment(thursdayFragment, getResources().getString(R.string.thursday));
            adapter.addFragment(fridayFragment, getResources().getString(R.string.friday));

            if (showWeekend) {
                adapter.addFragment(saturdayFragment, getResources().getString(R.string.saturday));
                adapter.addFragment(sundayFragment, getResources().getString(R.string.sunday));
            }
        } else {
            adapter.addFragment(sundayFragment, getResources().getString(R.string.sunday));
            adapter.addFragment(mondayFragment, getResources().getString(R.string.monday));
            adapter.addFragment(tuesdayFragment, getResources().getString(R.string.tuesday));
            adapter.addFragment(wednesdayFragment, getResources().getString(R.string.wednesday));
            adapter.addFragment(thursdayFragment, getResources().getString(R.string.thursday));

            if (showWeekend) {
                adapter.addFragment(fridayFragment, getResources().getString(R.string.friday));
                adapter.addFragment(saturdayFragment, getResources().getString(R.string.saturday));
            }
        }

        viewPager.setAdapter(adapter);

        int day = getFragmentChoosingDay();
        if (startOnSunday) {
            viewPager.setCurrentItem(day - 1, true);
        } else {
            viewPager.setCurrentItem(day == 1 ? 6 : day - 2, true);
        }
        tabLayout.setViewPager(viewPager);

    }

    private int getFragmentChoosingDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        //If its after 20 o'clock, show the next day
        if (hour >= showNextDayAfterSpecificHour) {
            day++;
        }

        if (day > 7) { //Calender.Saturday
            day = day - 7; //1 = Calendar.Sunday, 2 = Calendar.Monday etc.
        }

        boolean startOnSunday = PreferenceUtil.isWeekStartOnSunday(this);
        boolean showWeekend = PreferenceUtil.isSevenDays(this);

        //If Saturday/Sunday are hidden, switch to Monday
        if ((!startOnSunday && !showWeekend) && (day == Calendar.SATURDAY || day == Calendar.SUNDAY)) {
            day = Calendar.MONDAY;
        } else if ((startOnSunday && !showWeekend) && (day == Calendar.FRIDAY || day == Calendar.SATURDAY)) {
            day = Calendar.SUNDAY;
        }

        return day;
    }


    private void setupCustomDialog() {

        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
        AlertDialogsHelper.getAddSubjectDialog(new DbHelper(this), MainActivity.this, alertLayout, adapter, viewPager);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        finishAffinity();
    }

    @Override
    public void onStart() {
        super.onStart();
        DoNotDisturbReceiversKt.setDoNotDisturbReceivers(this, false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings) {
            Intent exams = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(exams);
        } else if (itemId == R.id.summary) {
            Intent teacher = new Intent(MainActivity.this, SummaryActivity.class);
            startActivity(teacher);
        } else if (itemId == R.id.about) {
            Intent about = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(about);
        } else if (itemId == R.id.logout) {
            new MaterialDialog.Builder(this)
                    .title(this.getString(R.string.are_you_sure))
                    .content(this.getString(R.string.logout_content, firebaseUser.getDisplayName()))
                    .positiveText(this.getString(R.string.yes))
                    .onPositive((dialog, which) -> {
                        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    firebaseAuth.signOut();
                                    // Display Toast
                                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                                    // Finish activity
                                    finish();
                                }
                            }
                        });
                        dialog.dismiss();
                    })
                    .onNegative((dialog, which) -> dialog.dismiss())
                    .negativeText(this.getString(R.string.no))
                    .show();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void aboutDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_about, null);

        TextView txt_app_version = view.findViewById(R.id.txt_app_version);
        txt_app_version.setText(getString(R.string.msg_about_version) + " " + BuildConfig.VERSION_NAME);

        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(view);
        alert.setPositiveButton(R.string.dialog_option_ok, (dialog, which) -> dialog.dismiss());
        alert.show();
    }


    private void inAppReview() {
        if (prefHelper.getInAppReviewToken() < 1) {
            prefHelper.updateInAppReviewToken(prefHelper.getInAppReviewToken() + 1);
            Log.d(TAG, "in app update token");
            Log.e(TAG, "token updated");
        } else {
            ReviewManager manager = ReviewManagerFactory.create(this);
            com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    manager.launchReviewFlow(MainActivity.this, reviewInfo).addOnFailureListener(e -> {
                    }).addOnCompleteListener(complete -> {
                                Log.d(TAG, "Success");
                            }
                    ).addOnFailureListener(failure -> {
                        Log.d(TAG, "Rating Failed");
                    });
                }
            }).addOnFailureListener(failure -> Log.d(TAG, "In-App Request Failed " + failure));
            Log.d(TAG, "in app token complete, show in app review if available");
        }
        Log.d(TAG, "in app review token : " + prefHelper.getInAppReviewToken());
    }
}
