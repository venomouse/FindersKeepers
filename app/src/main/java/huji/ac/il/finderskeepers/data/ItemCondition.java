package huji.ac.il.finderskeepers.data;

import huji.ac.il.finderskeepers.R;

/**
 * Created by Maria on 7/25/2015.
 */
public enum ItemCondition {
    ZERO_STARS(0),
    ONE_STAR(1),
    TWO_STARS(2),
    THREE_STARS(3),
    FOUR_STARS(4),
    FIVE_STARS(5);

    public int value;

    ItemCondition (int value) {
        this.value = value;
    }

    public static ItemCondition fromInt(int value) {
        return ItemCondition.values()[value];
    }

}
