package me.herbix.dice;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Chaofan on 2016/6/5.
 */
public class DiceStatusView extends View {

    private int color;
    private int[] status;
    private boolean topSmall = false;

    private float[] percents = new float[7];

    public DiceStatusView(Context context) {
        super(context);
    }

    public DiceStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttr(context, attrs);
    }

    public DiceStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttr(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DiceStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAttr(context, attrs);
    }

    private void loadAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiceStatusView);
        color = a.getColor(R.styleable.DiceStatusView_diceStatusColor, Color.BLACK);
        topSmall = a.getBoolean(R.styleable.DiceStatusView_topSmall, false);
        a.recycle();
    }

    public void setStatus(int[] status) {
        this.status = status;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private Paint paint = new Paint();
    {
        paint.setTextSize(dip2px(16));
    }

    public int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (status == null || status[0] == 0) {
            return;
        }

        float h = getHeight();
        float w = getWidth();

        paint.setColor(color);

        float maxPercents = 0;
        for (int i=1; i<=6; i++) {
            percents[i] = (float)status[i] / status[0];
            if (percents[i] > maxPercents) {
                maxPercents = percents[i];
            }
        }

        if (topSmall) {
            for (int i = 1; i <= 6; i++) {
                canvas.drawRect((maxPercents - percents[i]) / maxPercents * w, (i - 1) * h / 6, w, i * h / 6, paint);
            }
        } else {
            for (int i = 1; i <= 6; i++) {
                canvas.drawRect((maxPercents - percents[i]) / maxPercents * w, (6 - i) * h / 6, w, (7 - i) * h / 6, paint);
            }
        }

        paint.setColor(Color.WHITE);

        if (topSmall) {
            for (int i = 1; i <= 6; i++) {
                canvas.drawText(String.valueOf(i), w - dip2px(16), (i - 0.5f) * h / 6 + dip2px(7), paint);
            }
        } else {
            for (int i = 1; i <= 6; i++) {
                canvas.drawText(String.valueOf(i), w - dip2px(16), (6.5f - i) * h / 6 + dip2px(7), paint);
            }
        }
    }
}
