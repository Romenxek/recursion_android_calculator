package com.example.individualproject.calculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.individualproject.R;

import java.util.ArrayList;
import java.util.List;

public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.ViewHolder> {

    private final List<String> data = new ArrayList<>();

    public void setData(List<String> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView label, value;

        ViewHolder(View v) {
            super(v);
            label = v.findViewById(R.id.label);
            value = v.findViewById(R.id.value);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tree_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        // Строки в data идут от нижней к верхней, но RecyclerView покажет сверху вниз
        String s = data.get(pos);
        String[] parts = s.split(" = ");
        h.label.setText(parts[0]);
        h.value.setText(parts.length > 1 ? parts[1] : "");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
