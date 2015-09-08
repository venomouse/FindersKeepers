package huji.ac.il.finderskeepers.design;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * These buttons are used for actions button panels with 2 buttons in a row.
 * Created by Maria on 9/5/2015.
 */
public class HalfButton extends Button {
    public HalfButton(Context context) {
        super(context);
    }

    public HalfButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HalfButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()/2);
    }
}