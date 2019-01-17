package com.example.piotr.planer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import static com.example.piotr.planer.MainActivity.mContext;
import static com.example.piotr.planer.MainActivity.planList;
import static com.example.piotr.planer.MainActivity.saveSharPrefs;

public class EventListFragment extends Fragment {

    public static MyAdapter myAdapter;
    public static TextView textViewEmpty;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textViewEmpty = (TextView) view.findViewById(R.id.text_emptylist);
        RecyclerView recyclerView = view.findViewById(R.id.list_fragment_recycler_view);
        removeOutDatedPlans();
        myAdapter = new MyAdapter(getActivity(), planList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        if (planList.size() < 1) {
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    private void removeOutDatedPlans() {
        int planListSize = planList.size();
        for (int i = (planListSize - 1); i >= 0; i--) {
            if (planList.get(i).date.getTimeInMillis() < System.currentTimeMillis()) {
                if (planList.get(i).repeatInterval > 0) {
                    planList.get(i).date.add(Calendar.DATE, planList.get(i).repeatInterval);
                    planList.get(i).formatDate();
                } else {
                    planList.remove(i);
                }
            }
        }
    }
}
