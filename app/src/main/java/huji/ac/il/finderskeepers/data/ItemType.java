package huji.ac.il.finderskeepers.data;

import huji.ac.il.finderskeepers.R;

/**
 * The type of the item - chosen by an icon when reporting
 * Created by Maria on 7/25/2015.
 */
public enum ItemType {
    FURNITURE(0, R.drawable.furniture_icon),
    CLOTHES(1, R.drawable.clothes_icon),
    BOOKS(2, R.drawable.books_icon);

    public int value;
    public int iconID;

    ItemType (int value, int iconID) {
        this.value = value;
        this.iconID = iconID;
    }
}
