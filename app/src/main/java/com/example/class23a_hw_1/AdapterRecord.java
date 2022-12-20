package com.example.class23a_hw_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;


import java.util.ArrayList;

public class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.RecordViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private ArrayList<Record> records;

    public AdapterRecord(ArrayList<Record> records) {
        this.records = records;
    }

    @Override
    public int getItemCount() {
        return records == null ? 0 : records.size();
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record, parent, false);
        RecordViewHolder recordViewHolder = new RecordViewHolder(view);

        return recordViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecordViewHolder holder, final int position) {
        // Get the current position of the view holder
        final int currentPosition = holder.getAdapterPosition();
        Record record = records.get(currentPosition);

        holder.score_TXT.setText("Name: " +record.getName() +" Score: " + record.getScore());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, currentPosition);
            }
        });
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView score_TXT;

        public RecordViewHolder(View itemView) {
            super(itemView);
            score_TXT = itemView.findViewById(R.id.score_TXT);
        }

    }
}