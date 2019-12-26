package com.example.msiprojectv2.local;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.msiprojectv2.R;

import java.util.List;

public class ActionsAdapter extends ArrayAdapter<Action> {


    private Activity context;
    private List<Action> actions;

    public ActionsAdapter(@NonNull Activity context, int resource, @NonNull List<Action> actionList) {
        super(context, R.layout.action_item, actionList);
        this.context =context;
        this.actions =actionList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Action a = actions.get(position);
        if(a == null)
            return super.getView(position, convertView, parent);

        LinearLayout l = (LinearLayout)context.getLayoutInflater().inflate(R.layout.action_item, null);
        populate(l, a);
        return l;
    }

    private void populate(LinearLayout l, Action action) {
        TextView id = l.findViewById(R.id.action_id);
        TextView name = l.findViewById(R.id.name);
        TextView prod = l.findViewById(R.id.prod);
        TextView price = l.findViewById(R.id.price);
        TextView quantity = l.findViewById(R.id.quantity);
        TextView actionT = l.findViewById(R.id.action);

        if(id != null) id.setText(""+action.item);
        if(name != null) name.setText(action.name);
        if(prod != null) prod.setText(action.prod);
        if(price != null) price.setText(""+action.price);
        if(actionT != null) actionT.setText(action.actionTaken.toString());
        if(quantity != null) quantity.setText(""+action.quantity);
    }
}
