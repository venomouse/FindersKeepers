package huji.ac.il.finderskeepers.design;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Maria on 9/5/2015.
 */
public class RectButton extends Button {
    public RectButton(Context context) {
        super(context);
    }

    public RectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()*3/4); // Snap to width
    }
}