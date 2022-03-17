package com.espimx.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by Administrator on 2018/3/7 0007.
 */

public class SafeKeyboardView extends KeyboardView {

    private static final String TAG = "SafeKeyboardView";

    private Context mContext;
    private boolean isCap;
    private boolean isCapLock;
    private boolean enableVibrate;
    private Drawable delDrawable;
    private Drawable lowDrawable;
    private Drawable upDrawable;
    private Drawable upDrawableLock;
    private Keyboard lastKeyboard;
    /**
     * 按键的宽高至少是图标宽高的倍数
     */
    private static final int ICON2KEY = 2;

    // 键盘的一些自定义属性
    private boolean randomDigit;    // 数字随机
    private final static boolean DIGIT_RANDOM = false;
    //    private boolean onlyIdCard;     // 仅显示 身份证 键盘
//    private final static boolean ONLY_ID_CARD = false;
    private boolean rememberLastType;     // 仅显示 身份证 键盘
    private final static boolean REM_LAST_TYPE = true;
    private final static boolean DEFAULT_ENABLE_VIBRATE = false;

    public SafeKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.mContext = context;

        initAttrs(context, attrs, 0);
    }

    public SafeKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.mContext = context;

        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SafeKeyboardView, defStyleAttr, 0);
            randomDigit = array.getBoolean(R.styleable.SafeKeyboardView_random_digit, DIGIT_RANDOM);
            // onlyIdCard = array.getBoolean(R.styleable.SafeKeyboardView_only_id_card, ONLY_ID_CARD);
            rememberLastType = array.getBoolean(R.styleable.SafeKeyboardView_remember_last_type, REM_LAST_TYPE);
            enableVibrate = array.getBoolean(R.styleable.SafeKeyboardView_enable_vibrate, DEFAULT_ENABLE_VIBRATE);
            array.recycle();
        }
    }

    public void setRememberLastType(boolean remember) {
        rememberLastType = remember;
    }

    private void init(Context context) {
        this.isCap = false;
        this.isCapLock = false;
        // 默认三种图标
        this.delDrawable = context.getDrawable(R.drawable.ic_keyboard_delete);
        this.lowDrawable = context.getDrawable(R.drawable.icon_capital_default);
        this.upDrawable = context.getDrawable(R.drawable.icon_capital_selected);
        this.upDrawableLock = context.getDrawable(R.drawable.icon_capital_selected_lock);
        this.lastKeyboard = null;
    }

    public boolean isRandomDigit() {
        return randomDigit;
    }

//    public boolean isOnlyIdCard() {
//        return onlyIdCard;
//    }

    public boolean isRememberLastType() {
        return rememberLastType;
    }

    public boolean isVibrateEnable() {
        return enableVibrate;
    }

    public void enableVibrate(boolean enableVibrate) {
        this.enableVibrate = enableVibrate;
    }

    @Override
    public void setKeyboard(Keyboard keyboard) {
        super.setKeyboard(keyboard);
        this.lastKeyboard = keyboard;
    }

    public Keyboard getLastKeyboard() {
        return lastKeyboard;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -5 || key.codes[0] == -2 || key.codes[0] == 100860
                        || key.codes[0] == -1 || key.codes[0] == -4)
                    drawSpecialKey(canvas, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawSpecialKey(Canvas canvas, Keyboard.Key key) {
        int color = Color.WHITE;
        if (key.codes[0] == -5) {
            drawKeyBackground(R.drawable.keyboard_key_background, canvas, key);
            drawTextAndIcon(canvas, key, delDrawable, color);
        } else if (key.codes[0] == -2 || key.codes[0] == 100860) {
            drawKeyBackground(R.drawable.keyboard_change, canvas, key);
            drawTextAndIcon(canvas, key, null, color);
        } else if (key.codes[0] == -1) {
            if (isCapLock) {
                drawKeyBackground(R.drawable.keyboard_change, canvas, key);
                drawTextAndIcon(canvas, key, upDrawableLock, color);
            } else if (isCap) {
                drawKeyBackground(R.drawable.keyboard_change, canvas, key);
                drawTextAndIcon(canvas, key, upDrawable, color);
            } else {
                drawKeyBackground(R.drawable.keyboard_change, canvas, key);
                drawTextAndIcon(canvas, key, lowDrawable, color);
            }
        } else if (key.codes[0] == -4) {
            drawKeyBackground(R.drawable.keyboard_confirm, canvas, key);
            drawTextAndIcon(canvas, key, null, Color.WHITE);
        }
    }

    private void drawKeyBackground(int id, Canvas canvas, Keyboard.Key key) {
        Drawable drawable = mContext.getResources().getDrawable(id);
        int[] state = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            drawable.setState(state);
        }
        drawable.setBounds(key.x + getPaddingLeft(), key.y + getPaddingTop(),
                key.x + key.width + getPaddingLeft(), key.y + key.height + getPaddingTop());
        drawable.draw(canvas);
    }

    private void drawTextAndIcon(Canvas canvas, Keyboard.Key key, @Nullable Drawable drawable, int color) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(color);

            if (key.label != null) {
                String label = key.label.toString();

                if (label.length() > 1 && key.codes.length < 2) {
                    paint.setTextSize(mLabelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    paint.setTextSize(mKeyTextSize + 10);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (1.0f * key.width / 2) + getPaddingLeft(),
                        (key.y + 1.0f * key.height / 2) + 1.0f * bounds.height() / 2 + getPaddingTop(), paint);
            }
            if (drawable == null) return;
            // 约定: 最终图标的宽度和高度都需要在按键的宽度和高度的二分之一以内
            // 如果: 图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 那就不需要变换, 否则就需要等比例缩小
            int iconSizeWidth, iconSizeHeight;
            key.icon = drawable;
            int iconW = key.icon.getIntrinsicWidth();
            int iconH = key.icon.getIntrinsicHeight();
            if (key.width >= (ICON2KEY * iconW) && key.height >= (ICON2KEY * iconH)) {
                //图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 不需要缩放, 因为图片已经够小或者按键够大
                setIconSize(canvas, key, iconW, iconH);
            } else {
                //图标的实际宽度和高度至少有一个不在按键的宽度或高度的二分之一以内, 需要等比例缩放, 因为此时图标的宽或者高已经超过按键的二分之一
                //需要把超过的那个值设置为按键的二分之一, 另一个等比例缩放
                //不管图标大小是多少, 都以宽度width为标准, 把图标的宽度缩放到和按键一样大, 并同比例缩放高度
                double multi = 1.0 * iconW / key.width;
                int tempIconH = (int) (iconH / multi);
                if (tempIconH <= key.height) {
                    //宽度相等时, 图标的高度小于等于按键的高度, 按照现在的宽度和高度设置图标的最终宽度和高度
                    iconSizeHeight = tempIconH / ICON2KEY;
                    iconSizeWidth = key.width / ICON2KEY;
                } else {
                    //宽度相等时, 图标的高度大于按键的高度, 这时按键放不下图标, 需要重新按照高度缩放
                    double mul = 1.0 * iconH / key.height;
                    int tempIconW = (int) (iconW / mul);
                    iconSizeHeight = key.height / ICON2KEY;
                    iconSizeWidth = tempIconW / ICON2KEY;
                }
                setIconSize(canvas, key, iconSizeWidth, iconSizeHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIconSize(Canvas canvas, Keyboard.Key key, int iconSizeWidth, int iconSizeHeight) {
        int left = key.x + (key.width - iconSizeWidth) / 2 + getPaddingLeft();
        int top = key.y + (key.height - iconSizeHeight) / 2 + getPaddingTop();
        int right = key.x + (key.width + iconSizeWidth) / 2 + getPaddingLeft();
        int bottom = key.y + (key.height + iconSizeHeight) / 2 + getPaddingTop();
        key.icon.setBounds(left, top, right, bottom);
        key.icon.draw(canvas);
        key.icon = null;
    }

    public void setCap(boolean cap) {
        isCap = cap;
    }

    public void setCapLock(boolean isCapLock) {
        this.isCapLock = isCapLock;
    }

    public void setDelDrawable(Drawable delDrawable) {
        this.delDrawable = delDrawable;
    }

    public void setLowDrawable(Drawable lowDrawable) {
        this.lowDrawable = lowDrawable;
    }

    public void setUpDrawable(Drawable upDrawable) {
        this.upDrawable = upDrawable;
    }

    public void setUpDrawableLock(Drawable upDrawableLock) {
        this.upDrawableLock = upDrawableLock;
    }


}
