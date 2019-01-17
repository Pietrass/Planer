package com.example.piotr.planer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static com.example.piotr.planer.BroadcastEvent.CHANNEL_ID;
import static com.example.piotr.planer.EventListFragment.myAdapter;
import static com.example.piotr.planer.EventListFragment.textViewEmpty;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;
    public static Activity activity;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channelName";
            String description = "channelDescription";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static ArrayList<ListItemModel> planList;
    NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        mContext = this.getApplicationContext();
        createNotificationChannel();

        planList = getFromSharPrefs(this);

        if (planList == null) {
            planList = new ArrayList<>();
        }

        int requestCode = getIntent().getIntExtra("requestCode", -1);
        if (requestCode == 0) {
            final Calendar calendar = Calendar.getInstance();
            final int hours = calendar.get(Calendar.HOUR_OF_DAY);
            final int minutes = calendar.get(Calendar.MINUTE);

            this.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Toast.makeText(getApplicationContext(), "Ustawiono przypomnienie", Toast.LENGTH_SHORT).show();
                    String eventName = getIntent().getStringExtra("eventName");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    ListItemModel plan = new ListItemModel(eventName, calendar, 0);
                    planList.add(plan);
                    myAdapter.notifyDataSetChanged();
                    textViewEmpty.setVisibility(View.GONE);
                    setAlarm(calendar, eventName);
                    saveSharPrefs(getApplicationContext());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }, hours, minutes, true);

            timePickerDialog.show();
        }
        Collections.sort(planList);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Zaplanowane"));
        tabLayout.addTab(tabLayout.newTab().setText("Kalendarz"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        FragmentManager fragmentManager = getSupportFragmentManager();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static void saveSharPrefs(Context context) {
        SharedPreferences sharPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(planList);
        editor.putString("planList", json);
        editor.commit();
    }

    public ArrayList<ListItemModel> getFromSharPrefs(Context context) {
        SharedPreferences sharPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = sharPrefs.getString("planList", "");
        return gson.fromJson(json, new TypeToken<ArrayList<ListItemModel>>(){}.getType());
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSharPrefs(this);
    }


    private void setAlarm(Calendar eventDate, String eventName) {
        String idString = "" + eventDate.get(Calendar.DATE) + eventDate.get(Calendar.MONTH) + eventDate.get(Calendar.HOUR_OF_DAY) + eventDate.get(Calendar.MINUTE);
        int id = Integer.parseInt(idString);

        Intent intent = new Intent(getApplicationContext(), BroadcastEvent.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, eventDate.getTimeInMillis(), pendingIntent);
    }
}
