package com.example.piotr.planer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static com.example.piotr.planer.BroadcastEvent.CHANNEL_ID;
import static com.example.piotr.planer.EventListFragment.myAdapter;

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

    public void showNotification(Calendar eventDate) {
        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        
    }
}
