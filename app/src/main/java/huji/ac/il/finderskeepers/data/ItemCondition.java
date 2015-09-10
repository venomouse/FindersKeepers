package huji.ac.il.finderskeepers.data;


/**
 * This enum represents possible values for the condition of the item.
 * Currently the condition is measured by stars, on the scale from 0 to 5.
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
