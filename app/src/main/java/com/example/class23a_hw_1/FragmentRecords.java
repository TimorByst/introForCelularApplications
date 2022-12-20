package com.example.class23a_hw_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class FragmentRecords extends Fragment {

    private Callback_Map callback_map;
    private RecyclerView record_lst;
    private AdapterRecord adapterRecord;
    private ArrayList<Record> records;


    public void setCallback_Map(Callback_Map callback_map) {
        this.callback_map = callback_map;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        records = DataManager.getRecords();
        adapterRecord = new AdapterRecord(records);
        adapterRecord.setOnItemClickListener(onItemClickListener);
        record_lst = view.findViewById(R.id.record_list);
        record_lst.setLayoutManager(new LinearLayoutManager(view.getContext()));
        record_lst.setAdapter(adapterRecord);
    }

    private AdapterRecord.OnItemClickListener onItemClickListener = new AdapterRecord.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Record record = records.get(position);
            callback_map.zoomOnRecord(record.getName(), record.getLongitude(), record.getLatitude());
        }
    };
}
