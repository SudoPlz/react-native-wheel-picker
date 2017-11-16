package com.zyu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.aigestudio.wheelpicker.WheelPicker;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class ReactWheelCurvedPicker extends WheelPicker {

    private final EventDispatcher mEventDispatcher;
    private List<Integer> mValueData;
    private int selectedLineColor;
    private int state;

    public ReactWheelCurvedPicker(ReactContext reactContext) {
        super(reactContext);
        mEventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolled(int offset) {
            }

            @Override
            public void onWheelSelected(int index) {
                if (mValueData != null && index < mValueData.size()) {
                    mEventDispatcher.dispatchEvent(
                            new ItemSelectedEvent(getId(), mValueData.get(index)));
                }
            }

            @Override
            public void onWheelScrollStateChanged(int newState) {
                state = newState;
            }
        });
    }

    public int getCurrentSelectedItem() {
        return this.getSelectedItemPosition();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(selectedLineColor);
        int colorTo = selectedLineColor;
        int colorFrom = Color.TRANSPARENT;
        LinearGradient linearGradientShader = new LinearGradient(mRectCurrentItem.left, mRectCurrentItem.top, mRectCurrentItem.right/2, mRectCurrentItem.top, colorFrom, colorTo, Shader.TileMode.MIRROR);
        paint.setShader(linearGradientShader);

        canvas.drawLine(mRectCurrentItem.left, mRectCurrentItem.top, mRectCurrentItem.right, mRectCurrentItem.top, paint);
        canvas.drawLine(mRectCurrentItem.left, mRectCurrentItem.bottom, mRectCurrentItem.right, mRectCurrentItem.bottom, paint);
    }

    @Override
    public void setSelectedItemPosition(int index) {
        super.setSelectedItemPosition(index);
        mHandler.post(this);
    }

    public void setSelectedLineColor(int selectedLineColor) {
        this.selectedLineColor = selectedLineColor;
        invalidate();
    }

    public void setValueData(List<Integer> data) {
        mValueData = data;
    }

    public int getState() {
        return state;
    }
}

class ItemSelectedEvent extends Event<ItemSelectedEvent> {

    public static final String EVENT_NAME = "wheelCurvedPickerPageSelected";

    private final int mValue;

    protected ItemSelectedEvent(int viewTag,  int value) {
        super(viewTag);
        mValue = value;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
    }

    private WritableMap serializeEventData() {
        WritableMap eventData = Arguments.createMap();
        eventData.putInt("data", mValue);
        return eventData;
    }
}
