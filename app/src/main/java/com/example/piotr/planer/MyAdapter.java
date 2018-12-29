package com.example.piotr.planer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static com.example.piotr.planer.EventListFragment.myAdapter;
import static com.example.piotr.planer.EventListFragment.textViewEmpty;
import static com.example.piotr.planer.MainActivity.activity;
import static com.example.piotr.planer.MainActivity.mContext;
import static com.example.piotr.planer.MainActivity.planList;
import static com.example.piotr.planer.MainActivity.saveSharPrefs;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ListItemModel> events;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTextView;
        public TextView dateTextView;
        public Button button;

        public MyViewHolder(View itemView) {
            super(itemView);

            eventTextView = itemView.findViewById(R.id.text_event);
            dateTextView = itemView.findViewById(R.id.text_event_date);
            button = itemView.findViewById(R.id.event_menu);
        }


    }

    public MyAdapter(Context context, ArrayList<ListItemModel> planList) {
        events = planList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = inflater.inflate(R.layout.event_list_item, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final int ind = i;

        ListItemModel listItemModel = events.get(i);
        TextView eventNameText = myViewHolder.eventTextView;
        eventNameText.setText(listItemModel.eventName);

        TextView eventDateTest = myViewHolder.dateTextView;
        eventDateTest.setText(listItemModel.formattedEventDate);

        Button button = myViewHolder.button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, ind);
            }
        });
    }

    private void showPopup(View v, int i) {
        final int ind = i;
        final View view = v;
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.edit:
                        ListItemModel listItemModel = events.get(ind);
                        Calendar date = listItemModel.date;
                        String eventName = listItemModel.eventName;
                        FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                        PlanDialogEdit planDialog = PlanDialogEdit.newInstance(date, ind, date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), eventName);
                        planDialog.show(fragmentManager, "calendar_dialog_edit");
                        break;
                    case R.id.remove:
                        ListItemModel plan = planList.get(ind);
                        cancelAlarm(plan.date, plan.eventName);
                        planList.remove(ind);
                        if (planList.size() < 1) {
                            textViewEmpty.setVisibility(View.VISIBLE);
                        }
                        saveSharPrefs(view.getContext());
                        myAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.event_menu, popupMenu.getMenu());
        popupMenu.show();
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    private void setAlarm(Calendar eventDate, String eventName) {
        String idString = "" + eventDate.get(Calendar.DATE) + eventDate.get(Calendar.MONTH) + eventDate.get(Calendar.HOUR_OF_DAY) + eventDate.get(Calendar.MINUTE);
        int id = Integer.parseInt(idString);

        Intent intent = new Intent(mContext, BroadcastEvent.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, eventDate.getTimeInMillis(), pendingIntent);
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


