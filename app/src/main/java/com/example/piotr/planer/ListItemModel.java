package com.example.piotr.planer;

import android.app.AlarmManager;
import android.app.PendingIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ListItemModel implements Comparable<ListItemModel> {

    public String eventName, formattedEventDate;
    public Calendar date;

    public ListItemModel(String eventName, Calendar date) {
        this.eventName = eventName;
        this.date = date;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy     HH:mm");
        this.formattedEventDate = sdf.format(date.getTime());
    }

    @Override
    public int compareTo(ListItemModel o) {
        return date.compareTo(o.date);
    }

}
