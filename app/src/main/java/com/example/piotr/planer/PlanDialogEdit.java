package com.example.piotr.planer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collections;

import static android.content.Context.ALARM_SERVICE;
import static com.example.piotr.planer.EventListFragment.myAdapter;
import static com.example.piotr.planer.MainActivity.activity;
import static com.example.piotr.planer.MainActivity.mContext;
import static com.example.piotr.planer.MainActivity.planList;
import static com.example.piotr.planer.MainActivity.saveSharPrefs;

public class PlanDialogEdit extends DialogFragment {

    CalendarView calendarView;
    Calendar mEventDate;
    EditText mEvent, mRepeatDays;
    AutoCompleteTextView hours, minutes;
    Button add;
    int mDay, mMonth, mYear, mHour, mMinute, mInterval;
    String eventName;
    CheckBox repeatCheckbox;

    public PlanDialogEdit() {}

    public static PlanDialogEdit newInstance(Calendar date, int ind, int day, int month, int year, int hour, int minute, String event, int interval) {
        PlanDialogEdit planDialogEdit = new PlanDialogEdit();
        Bundle args = new Bundle();
        args.putInt("ind", ind);
        args.putInt("day", day);
        args.putInt("month", month);
        args.putInt("year", year);
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        args.putString("event", event);
        args.putInt("interval", interval);
        planDialogEdit.setDate(date);
        planDialogEdit.setArguments(args);
        return planDialogEdit;
    }

    private void setDate(Calendar date) {
        mEventDate = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_dialog_edit, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        hours = view.findViewById(R.id.hours_edit);
        minutes = view.findViewById(R.id.minutes_edit);
        mEvent = view.findViewById(R.id.event_edit);
        add = view.findViewById(R.id.add_edit);
        repeatCheckbox = view.findViewById(R.id.repeat_days);
        mRepeatDays = view.findViewById(R.id.edit_repeat_days);

        mDay = getArguments().getInt("day");
        mMonth = getArguments().getInt("month");
        mYear = getArguments().getInt("year");
        mHour = getArguments().getInt("hour");
        mMinute = getArguments().getInt("minute");
        eventName = getArguments().getString("event");
        mInterval = getArguments().getInt("interval");

        hours.setText(String.format("%02d", mHour));
        minutes.setText(String.format("%02d", mMinute));
        mEvent.setText(eventName);

        Log.d("Planer: ", "Day: " + mDay + ", Month: " + mMonth + ", Year: " + mYear);

        final int ind = getArguments().getInt("ind");
        calendarView.setDate(mEventDate.getTimeInMillis());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                mDay = dayOfMonth;
                mMonth = month;
                mYear = year;
            }
        });

        if (mInterval > 0) {
            repeatCheckbox.setChecked(true);
            mRepeatDays.setText(String.valueOf(mInterval));
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar eventDate = Calendar.getInstance();

                if (hours.getText().toString().length() != 0)  {
                    mHour = Integer.parseInt(hours.getText().toString());
                }

                if (minutes.getText().toString().length() != 0) {
                    mMinute = Integer.parseInt(minutes.getText().toString());
                }

                if (mEvent.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "Brak opisu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mHour >= 24 || mMinute >= 60) {
                    Toast.makeText(getContext(), "Niepoprawna godzina", Toast.LENGTH_SHORT).show();
                } else {
                    eventDate.set(mYear, mMonth, mDay, mHour, mMinute);
                    if (eventDate.getTimeInMillis() < System.currentTimeMillis()) {
                        Toast.makeText(getContext(), "Data nieaktualna", Toast.LENGTH_SHORT).show();

                    } else {
                        ListItemModel plan = planList.get(ind);
                        cancelAlarm(plan.date, plan.eventName);
                        planList.remove(ind);

                        eventName = mEvent.getText().toString();
                        if (repeatCheckbox.isChecked()) {
                            mInterval = Integer.valueOf(mRepeatDays.getText().toString());
                        }
                        ListItemModel event = new ListItemModel(eventName, eventDate, mInterval);
                        Toast.makeText(getContext(), "Zmienione", Toast.LENGTH_SHORT).show();
                        setAlarm(eventDate, eventName);
                        planList.add(event);
                        Collections.sort(planList);
                        saveSharPrefs(getContext());
                        myAdapter.notifyDataSetChanged();
                        getDialog().dismiss();
                    }
                }
            }
        });
    }

    private void setAlarm(Calendar eventDate, String eventName) {
        String idString = "" + eventDate.get(Calendar.DATE) + eventDate.get(Calendar.MONTH) + eventDate.get(Calendar.HOUR_OF_DAY) + eventDate.get(Calendar.MINUTE);
        int id = Integer.parseInt(idString);

        Intent intent = new Intent(mContext, BroadcastEvent.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        if (mInterval > 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, eventDate.getTimeInMillis(),AlarmManager.INTERVAL_DAY * mInterval, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, eventDate.getTimeInMillis(), pendingIntent);
        }
        }


    private void cancelAlarm(Calendar eventDate, String eventName) {
        String idString = "" + eventDate.get(Calendar.DATE) + eventDate.get(Calendar.MONTH) + eventDate.get(Calendar.HOUR_OF_DAY) + eventDate.get(Calendar.MINUTE);
        int id = Integer.parseInt(idString);

        Intent intent = new Intent(mContext, BroadcastEvent.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
