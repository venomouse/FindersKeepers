package huji.ac.il.finderskeepers.data;

import huji.ac.il.finderskeepers.R;

/**
 * Created by Maria on 7/25/2015.
 */
public enum ItemCondition {
    MINT(0, 0),
    SOSO(1, 1),
    NEEDS_REPAIR(2, 2);

    public int value;
    public int iconID;


    ItemCondition (int value, int iconID) {
        this.value = value;
        this.iconID = iconID;
    }

}
