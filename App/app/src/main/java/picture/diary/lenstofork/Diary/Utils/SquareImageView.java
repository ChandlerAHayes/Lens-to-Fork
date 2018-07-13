package picture.diary.lenstofork.Diary.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SquareImageView extends android.support.v7.widget.AppCompatImageView {
    public void resizeToParent(View parent, int tabHeight){
        /**
         * max height cannot be greater than 1/3 of the parent height or 1/2 parent width with
         *  considering the tab and giving some room for titles and padding
         */

        int maxWidth = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
        maxWidth *= 1/3;
        int maxHeight = parent.getHeight() - tabHeight;
        maxHeight *= 1/2;

        if(maxWidth < maxHeight){
            setMeasuredDimension(maxWidth, maxWidth);
        }
        else{
            setMeasuredDimension(maxHeight, maxHeight);
        }

        int w = getWidth();
        int h = getHeight();
        h += 0;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(widthMeasureSpec < heightMeasureSpec){
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
        }
        else{
            setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
        }
    }

    //-------- Constructors
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
