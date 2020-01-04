package com.example.msiprojectv2.sets;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.msiprojectv2.R;
import com.example.msiprojectv2.utils.SetItem;
import com.example.msiprojectv2.utils.SetModel;

import java.util.ArrayList;
import java.util.List;

public class SetsAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private List<SetModel> sets;

    public SetsAdapter(Activity activity, List<SetModel> sets) {
        super(activity, R.layout.set_list_item);
        this.activity = activity;
        this.sets = sets;
        prefetch();
    }

    private List<Float> prices = new ArrayList<>();
    private List<SetItemsAdapter> adapters = new ArrayList<>();

    private void prefetch() {
        for(SetModel set: sets) {
            prices.add(set.getPrice(activity));
            adapters.add(new SetItemsAdapter(activity, set.getItemCommonNames(activity)));
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RelativeLayout l = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.set_list_item, null);
        if(l != null) {
            TextView setPrice = l.findViewById(R.id.setPrice);
            TextView setName = l.findViewById(R.id.setName);
            ListView setItems = l.findViewById(R.id.setItems);
            Button viewBtn = l.findViewById(R.id.viewBtn);

            if(setPrice != null)
                setPrice.setText("Price: " + prices.get(position));

            if(setName != null)
                setName.setText(sets.get(position).name);

            if(setItems != null)
                setItems.setAdapter(adapters.get(position));

            if(viewBtn != null) {
                final int position2 = position;
                viewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout l2 = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_setmodel, null);
                        TextView setName2 = l2.findViewById(R.id.setName);
                        TextView setPrice2 = l2.findViewById(R.id.setPrice);
                        ListView setItems2 = l2.findViewById(R.id.setItems);
                        if(setName2 != null) setName2.setText(sets.get(position2).name);
                        if(setPrice2 != null) setPrice2.setText("Price: " + prices.get(position2));
                        if(setItems2 != null) setItems2.setAdapter(adapters.get(position2));
                        new AlertDialog.Builder(activity)
                                .setView(l2)
                                .show();
                    }
                });
            }

            return l;
        }

        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return sets.size();
    }
}
