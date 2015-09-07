package huji.ac.il.finderskeepers.data;

import huji.ac.il.finderskeepers.R;

/**
 * The type of the item - chosen by an icon when reporting
 * Created by Maria on 7/25/2015.
 */
public enum ItemType {
    FURNITURE(0, R.drawable.furniture_icon_wide,R.drawable.furniture_marker),
    CLOTHES(1, R.drawable.clothes_icon_wide, R.drawable.clothes_marker),
    BOOKS(2, R.drawable.books_icon_wide, R.drawable.books_marker);

    public int value;
    public int iconID;
    public int markerID;

    ItemType (int value, int iconID, int markerID) {
        this.value = value;
        this.iconID = iconID;
        this.markerID = markerID;
    }



    public static ItemType fromInt(int value) {
        return ItemType.values()[value];
    }

}
