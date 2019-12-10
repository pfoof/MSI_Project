package com.example.msiproject.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.msiproject.R;

import java.util.List;

public class ItemsAdapter extends ArrayAdapter<ItemModel> {

    private final Activity context;
    private final ItemModel[] objects;
    private final IStockItemAction action;

    public ItemsAdapter(@NonNull Activity context, int resource, @NonNull ItemModel[] objects, IStockItemAction action) {
        super(context, R.layout.stock_item, objects);
        this.context = context;
        this.objects = objects;
        this.action = action;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemModel l = objects[position];

        if(l == null)
            return super.getView(position, convertView, parent);

        LayoutInflater inflater = context.getLayoutInflater();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.stock_item, null, true);

        populate(layout, l, action);

        return layout;
    }

    private void populate(LinearLayout l, final ItemModel o, final IStockItemAction action) {
        TextView text_name = (TextView) l.findViewById(R.id.text_name);
        TextView text_prod = (TextView) l.findViewById(R.id.text_prod);
        TextView text_price = (TextView) l.findViewById(R.id.text_price);
        TextView text_quantity = (TextView) l.findViewById(R.id.text_quantity);

        ImageButton btnEdit = (ImageButton) l.findViewById(R.id.btnEdit);
        ImageButton btnDelete = (ImageButton) l.findViewById(R.id.btnDelete);
        ImageButton btnAdd = (ImageButton) l.findViewById(R.id.btnAdd);
        ImageButton btnRemove = (ImageButton) l.findViewById(R.id.btnRemove);

        final int idCopy = o.id;

        if(text_name != null)
            text_name.setText(o.name);

        if(text_prod != null)
            text_prod.setText(o.prod);

        if(text_price != null)
            text_price.setText(String.format("Price: %s", o.price));
            //text_price.setText(String.format("Price: %.2f", o.price));

        if(text_quantity != null)
            text_quantity.setText(String.format("Quantity: %s", o.quantity));
            //text_quantity.setText(String.format("Quantity: %d", o.quantity));

        if(btnEdit != null) {
            btnEdit.setEnabled(action != null && action.canEdit());
            if(btnEdit.isEnabled())
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        action.editItem(idCopy, o);
                    }
                });
        }

        if(btnDelete != null) {
            btnDelete.setEnabled(action != null && action.canDelete());
            if(btnDelete.isEnabled())
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        action.deleteItem(idCopy);
                    }
                });
        }

        if(btnAdd != null) {
            btnAdd.setEnabled(action != null && action.canQuantity());
            if(btnAdd.isEnabled())
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        action.quantity(idCopy, 1);
                    }
                });
        }

        if(btnRemove != null) {
            btnRemove.setEnabled(action != null && action.canQuantity());
            if(btnRemove.isEnabled())
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        action.quantity(idCopy, -1);
                    }
                });
        }
    }
}
