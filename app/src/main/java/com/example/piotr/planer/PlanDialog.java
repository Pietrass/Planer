package com.example.piotr.planer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static com.example.piotr.planer.EventListFragment.textViewEmpty;
import static com.example.piotr.planer.MainActivity.mContext;
import static com.example.piotr.planer.MainActivity.planList;
import static com.example.piotr.planer.MainActivity.saveSharPrefs;


public class PlanDialog extends DialogFragment {

    TextView data;
    AutoCompleteTextView hours, minutes;
    EditText event;
    int hour, minute;
    boolean isHoursValid, isMinutesValid;
    Button add;

    public PlanDialog() {}

    public static PlanDialog newInstance(int day, int month, int year) {
        PlanDialog planDialog = new PlanDialog();
        Bundle args = new Bundle();
        args.putInt("mDay", day);
        args.putInt("month", month);
        args.putInt("year", year);
        planDialog.setArguments(args);
        return planDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = view.findViewById(R.id.data);
        hours = view.findViewById(R.id.hours);
        minutes = view.findViewById(R.id.minutes);
        event = view.findViewById(R.id.event);
        add = view.findViewById(R.id.add);

        final int day, month, year;
        day = getArguments().getInt("mDay");
        month = getArguments().getInt("month") + 1;
        year = getArguments().getInt("year");

        hour = 0;
        minute = 0;

        data.setText("" + day + "." + month + "." + year);

        add.setEnabled(false);
        hours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hours.length() == 2) {
                    hours.clearFocus();
                    minutes.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (minutes.length() == 2) {
                    minutes.clearFocus();
                    event.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        event.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setAddEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar eventDate = Calendar.getInstance();

                if (hours.getText().toString().length() != 0)  {
                    hour = Integer.parseInt(hours.getText().toString());
                }

                if (minutes.getText().toString().length() != 0) {
                    minute = Integer.parseInt(minutes.getText().toString());
                }


                if (hour >= 24 || minute >= 60) {
                    Toast.makeText(getContext(), "Niepoprawna godzina", Toast.LENGTH_SHORT).show();
                } else {
                    eventDate.set(year, month - 1, day, hour, minute);
                    if (eventDate.getTimeInMillis() < System.currentTimeMillis()) {
                        Toast.makeText(getContext(), "Data nieaktualna", Toast.LENGTH_SHORT).show();
                    } else {
                        String eventName = event.getText().toString();
                        ListItemModel plan = new ListItemModel(eventName, eventDate);
                        textViewEmpty.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Zaplanowane", Toast.LENGTH_SHORT).show();
                        setAlarm(eventDate, eventName);
                        planList.add(plan);
                        Collections.sort(planList);
                        saveSharPrefs(getContext());
                        getDialog().dismiss();


                    }
                }
            }
        });
    }

    private void setAlarm(Calendar eventDate, String eventName) {
        String idString = "" + eventDate.get(Calendar.DATE) + eventDate.get(Calendar.MONTH) + eventDate.get(Calendar.HOUR_OF_DAY) + eventDate.get(Calendar.MINUTE);
        int id = Integer.parseInt(idString);

        Intent intent = new Intent(getContext(), BroadcastEvent.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, eventDate.getTimeInMillis(), pendingIntent);
    }


    private void setAddEnabled() {
        if (event.getText().toString().length() > 0) {
            add.setEnabled(true);
        } else {
            add.setEnabled(false);
        }
    }
}
