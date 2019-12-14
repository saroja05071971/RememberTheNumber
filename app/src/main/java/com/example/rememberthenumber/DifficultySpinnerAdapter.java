package com.example.rememberthenumber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DifficultySpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] difficultyLevels;

    public DifficultySpinnerAdapter(Context context, int resource,String[] objects) {
        super(context, resource, objects);
        this.context=context;
        this.difficultyLevels=objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getDropDownView(position, convertView, parent);
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.difficulty_spinner_value,parent,false);

        TextView textView = row.findViewById(R.id.difficultyLevel);
        textView.setText(difficultyLevels[position]);
        return row;
    }
}
