package com.example.msiprojectv2.sets;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.msiprojectv2.R;
import com.example.msiprojectv2.utils.SetItem;

import java.util.List;

public class SetItemsAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private List<String> items;

    public SetItemsAdapter(Activity activity, List<String> items) {
        super(activity, android.R.layout.simple_list_item_1);

        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String item = items.get(position);

        if(item != null) {
            TextView view = (TextView) activity.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            view.setText(item);
            return view;
        }

        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
