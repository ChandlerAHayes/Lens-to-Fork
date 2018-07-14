package picture.diary.lenstofork.Diary.Utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

public class SquareImageView extends AppCompatImageView {

    /**
     * Resizes the ImageView to a square that is at most nearly a 1/2 of the parent's View in width
     * and at most nearly a 1/3 of the parent's View in height.
     *
     * @param parent
     */
    public void resizeImage(View parent){
        int width = (int) Math.floor(parent.getMeasuredWidth() * 0.48);
        int height = (int) Math.floor(parent.getMeasuredHeight() * 0.31);

        if(width < height){
            getLayoutParams().height = width;
            getLayoutParams().width = width;
        }
        else{
            getLayoutParams().height = height;
            getLayoutParams().width = height;
        }

        requestLayout();
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
