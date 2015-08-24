package huji.ac.il.finderskeepers.data;

import huji.ac.il.finderskeepers.R;

/**
 * Created by Maria on 7/25/2015.
 */
public enum ItemCondition {
    NEEDS_REPAIR(0,  R.drawable.needsrepair_icon),
    SOSO(1, R.drawable.soso_icon),
    MINT(2, R.drawable.mint_icon);

    public int value;
    public int iconID;


    ItemCondition (int value, int iconID) {
        this.value = value;
        this.iconID = iconID;
    }

    public static ItemCondition fromInt(int value) {
        return ItemCondition.values()[value];
    }

}
