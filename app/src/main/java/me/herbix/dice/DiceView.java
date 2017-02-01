package me.herbix.dice;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Chaofan on 2016/6/4.
 */
public class DiceView extends View {

    private Bitmap bitmap = null;
    private int color = Color.RED;
    private int number = 6;

    public DiceView(Context context) {
        super(context);
    }

    public DiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttr(context, attrs);
    }

    public DiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttr(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DiceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAttr(context, attrs);
    }

    private void loadAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiceView);
        color = a.getColor(R.styleable.DiceView_diceColor, Color.RED);
        int bitmapId = a.getResourceId(R.styleable.DiceView_bitmap, -1);
        if (bitmapId != -1) {
            bitmap = BitmapFactory.decodeResource(getResources(), bitmapId);
        }
        a.recycle();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Paint bgPaint = new Paint();
    private Paint forePaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        int size = Math.min(getWidth(), getHeight());
        if (bitmap == null) {
            if (color == Color.WHITE) {
                drawStandardDice(canvas, size);
            } else {
                drawNormalDice(canvas, size);
            }
        } else {
            drawBitmapDice(canvas, size);
        }
    }

    private void drawNormalDice(Canvas canvas, int size) {
        bgPaint.setColor(color);
        drawRoundRect(canvas, 0, 0, size, size, size / 10f, bgPaint);

        forePaint.setColor(Color.WHITE);
        switch (number) {
            case 1:
                canvas.drawCircle(size / 2f, size / 2f, size / 7f, forePaint);
                break;
            case 2:
                canvas.drawCircle(size / 2f, size / 7f * 2.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 2f, size / 7f * 4.8f, size / 10f, forePaint);
                break;
            case 3:
                canvas.drawCircle(size / 5f * 1.2f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 2.5f, size / 5f * 2.5f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.8f, size / 5f * 3.8f, size / 10f, forePaint);
                break;
            case 4:
                canvas.drawCircle(size / 5f * 1.4f, size / 5f * 1.4f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.6f, size / 5f * 3.6f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.6f, size / 5f * 1.4f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.4f, size / 5f * 3.6f, size / 10f, forePaint);
                break;
            case 5:
                canvas.drawCircle(size / 5f * 1.2f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 2.5f, size / 5f * 2.5f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.8f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.2f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.8f, size / 5f * 1.2f, size / 10f, forePaint);
                break;
            case 6:
                canvas.drawCircle(size / 5f * 1.5f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.5f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.5f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.5f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.5f, size / 2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.5f, size / 2f, size / 10f, forePaint);
        }
    }

    private void drawStandardDice(Canvas canvas, int size) {
        bgPaint.setColor(Color.rgb(96, 96, 96));
        drawRoundRect(canvas, 0, 0, size, size, size / 10f, bgPaint);
        bgPaint.setColor(Color.WHITE);
        drawRoundRect(canvas, 2, 2, size - 2, size - 2, size / 10f, bgPaint);

        switch (number) {
            case 1:
                forePaint.setColor(Color.rgb(192, 0, 0));
                canvas.drawCircle(size / 2f, size / 2f, size / 7f, forePaint);
                break;
            case 2:
                forePaint.setColor(Color.rgb(0, 0, 192));
                canvas.drawCircle(size / 2f, size / 7f * 2.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 2f, size / 7f * 4.8f, size / 10f, forePaint);
                break;
            case 3:
                forePaint.setColor(Color.rgb(0, 0, 192));
                canvas.drawCircle(size / 5f * 1.2f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 2.5f, size / 5f * 2.5f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.8f, size / 5f * 3.8f, size / 10f, forePaint);
                break;
            case 4:
                forePaint.setColor(Color.rgb(192, 0, 0));
                canvas.drawCircle(size / 5f * 1.4f, size / 5f * 1.4f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.6f, size / 5f * 3.6f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.6f, size / 5f * 1.4f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.4f, size / 5f * 3.6f, size / 10f, forePaint);
                break;
            case 5:
                forePaint.setColor(Color.rgb(0, 0, 192));
                canvas.drawCircle(size / 5f * 1.2f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 2.5f, size / 5f * 2.5f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.8f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.2f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.8f, size / 5f * 1.2f, size / 10f, forePaint);
                break;
            case 6:
                forePaint.setColor(Color.rgb(0, 0, 192));
                canvas.drawCircle(size / 5f * 1.5f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.5f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.5f, size / 5f * 3.8f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.5f, size / 5f * 1.2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 1.5f, size / 2f, size / 10f, forePaint);
                canvas.drawCircle(size / 5f * 3.5f, size / 2f, size / 10f, forePaint);
        }
    }

    protected void drawRoundRect(Canvas canvas, float l, float t, float r, float b, float radius, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(l, t, r, b, radius, radius, paint);
        } else {
            canvas.drawCircle(l + radius, t + radius, radius, paint);
            canvas.drawCircle(r - radius, t + radius, radius, paint);
            canvas.drawCircle(r - radius, b - radius, radius, paint);
            canvas.drawCircle(l + radius, b - radius, radius, paint);
            canvas.drawRect(l, t + radius, r, b - radius, paint);
            canvas.drawRect(l + radius, t, r - radius, b, paint);
        }
    }

    private void drawBitmapDice(Canvas canvas, int size) {
        bgPaint.setColor(color);
        drawRoundRect(canvas, 0, 0, size, size, size / 10f, bgPaint);
        bgPaint.setColor(Color.WHITE);
        drawRoundRect(canvas, 2, 2, size - 2, size - 2, size / 10f, bgPaint);

        int w = bitmap.getWidth() / 6;
        int h = bitmap.getHeight();
        Rect src = new Rect((number - 1) * w, 0, number * w, h);
        Rect dst = new Rect(0, 0, size, size);
        canvas.drawBitmap(bitmap, src, dst, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(0, 0, size, size, 0, 2, false, bgPaint);
        }
    }

    public int getNumber() {
        return number;
    }
}
