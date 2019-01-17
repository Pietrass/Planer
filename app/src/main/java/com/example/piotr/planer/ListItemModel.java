package com.example.piotr.planer;

import android.app.AlarmManager;
import android.app.PendingIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ListItemModel implements Comparable<ListItemModel> {

    public String eventName, formattedEventDate;
    public Calendar date;
    public int repeatInterval;

    public ListItemModel(String eventName, Calendar date, int repeatInterval) {
        this.eventName = eventName;
        this.date = date;
        this.repeatInterval = repeatInterval;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy     HH:mm");
        this.formattedEventDate = sdf.format(date.getTime());
    }

    @Override
    public int compareTo(ListItemModel o) {
        return date.compareTo(o.date);
    }

    public void formatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy     HH:mm");
        this.formattedEventDate = sdf.format(date.getTime());
    }

}
