package app.erp.com.erp_app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CircleAnimIndicator extends LinearLayout {
    private Context mContext;
    private int itemMargin = 10;
    private int animDuration = 250;
    private int mDefaultCircle;
    private int mSelectCircle;
    private ImageView[] imageDot;

    public void setAnimDuration(int animDration) {
        this.animDuration = animDration;
    }

    public void setItemMargin(int itemMargin) {
        this.itemMargin = itemMargin;
    }

    public CircleAnimIndicator(Context context) {
        super(context);
        mContext = context;
    }

    public CircleAnimIndicator(Context context , AttributeSet attr) {
        super(context,attr);
        mContext = context;
    }

    public void createDotPanel(int count, int defaultCircle , int selectCircle){
        mDefaultCircle = defaultCircle;
        mSelectCircle = selectCircle;

        imageDot = new ImageView[count];

        for(int i = 0 ; i < count; i++){
            imageDot[i] = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = itemMargin;
            params.bottomMargin = itemMargin;
            params.leftMargin = itemMargin;
            params.rightMargin = itemMargin;
            params.gravity = Gravity.CENTER;

            imageDot[i].setLayoutParams(params);
            imageDot[i].setImageResource(defaultCircle);
            imageDot[i].setTag(imageDot[i].getId(), false);
            this.addView(imageDot[i]);
        }

        selectDot(0);

    }

    public void selectDot(int position) {

        for (int i = 0; i < imageDot.length; i++) {
            if (i == position) {
                imageDot[i].setImageResource(mSelectCircle);
                selectScaleAnim(imageDot[i],1f,1.1f);
            } else {

                if((boolean)imageDot[i].getTag(imageDot[i].getId()) == true){
                    imageDot[i].setImageResource(mDefaultCircle);
                    defaultScaleAnim(imageDot[i], 1.1f, 1f);
                }
            }
        }
    }

    public void selectScaleAnim(View view, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(animDuration);
        view.startAnimation(anim);
        view.setTag(view.getId(),true);
    }


    /**
     * 선택되지 않은 점의 애니메이션
     * @param view
     * @param startScale
     * @param endScale
     */
    public void defaultScaleAnim(View view, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(animDuration);
        view.startAnimation(anim);
        view.setTag(view.getId(),false);
    }


}