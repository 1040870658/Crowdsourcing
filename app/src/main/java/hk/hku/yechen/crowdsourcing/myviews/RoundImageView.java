package hk.hku.yechen.crowdsourcing.myviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yechen on 2017/12/8.
 */

public class RoundImageView extends ImageView {
    public RoundImageView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }
    public RoundImageView(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.WHITE);
        int saveCount = canvas.getSaveCount();
        canvas.save();
        final RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
       // canvas.drawRoundRect(rectF, 10, 10, mPaint);
        canvas.drawCircle(rectF.centerX(),rectF.centerY(),(rectF.width())/2,mPaint);

        canvas.restoreToCount(saveCount);
        super.onDraw(canvas);
    }
}
