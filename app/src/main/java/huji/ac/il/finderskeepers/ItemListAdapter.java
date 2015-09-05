package huji.ac.il.finderskeepers;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import huji.ac.il.finderskeepers.data.Item;

/**
 * Created by Paz on 9/5/2015.
 */
public class ItemListAdapter extends ArrayAdapter<Item> {
    private List<Bitmap> itemImgs;

    public ItemListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ItemListAdapter(Context context, int resource, List<Item> items, List<Bitmap> images) {
        super(context, resource, items);
        itemImgs = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_list_row, null);
        }

        Item p = getItem(position);
        Bitmap b = itemImgs.get(position);
        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.lblType);
            TextView tt2 = (TextView) v.findViewById(R.id.lblCondition);
            TextView tt3 = (TextView) v.findViewById(R.id.lblDescription);
            ImageView itemImgView = (ImageView) v.findViewById(R.id.itemSmallImage);
            if (tt1 != null) {
                tt1.setText(p.getType().name());
            }

            if (tt2 != null) {
                tt2.setText(p.getCondition().name());
            }

            if (tt3 != null) {
                tt3.setText(p.getDescription());
            }
            if (itemImgView != null) {
                itemImgView.setImageBitmap(b);
            }

        }

        return v;
    }


}
