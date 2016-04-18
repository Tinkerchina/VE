package com.hit.wi.ve.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.hit.wi.jni.Kernel;
import com.hit.wi.ve.R;
import com.hit.wi.ve.SoftKeyboard;
import com.hit.wi.ve.values.Global;

/**
 * Created by purebleusong on 2016/4/7.
 */
public class PreEditPopup {
    private PopupWindow container;
    private EditText editText;
    private SoftKeyboard softKeyboard;

    private Paint toolPaint;
    private int leftMargin = 0;
    private int selectStart;
    private int selectStop;

    public void setSoftKeyboard(SoftKeyboard softKeyboard){
        this.softKeyboard = softKeyboard;
    }

    public void create(Context context){
        toolPaint = new Paint();
        editText = new EditText(context);
        editText.setPadding(0, 0, 0, 0);
        editText.setGravity(Gravity.LEFT & Gravity.CENTER_VERTICAL);
        editText.setVisibility(View.VISIBLE);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setBackgroundResource(R.drawable.blank);
        editText.setBackgroundColor(softKeyboard.skinInfoManager.skinData.backcolor_editText);
        editText.getBackground().setAlpha(Global.getCurrentAlpha());
        editText.setOnClickListener(editOnClickListener);
        if (Global.shadowSwitch) editText.setShadowLayer(Global.shadowRadius, 0, 0, softKeyboard.skinInfoManager.skinData.shadow);

        container = new PopupWindow(editText, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        container.setFocusable(false);
        container.setTouchable(true);
        container.setClippingEnabled(false);
        container.setBackgroundDrawable(null);
    }

    public void upadteSize(int width,int height,int leftMargin){
        container.setHeight(height);
        container.setWidth(width);
        this.leftMargin = leftMargin;
    }

    public void updateSkin() {
        editText.setBackgroundResource(R.drawable.blank);
        editText.setBackgroundColor(softKeyboard.skinInfoManager.skinData.backcolor_preEdit);
        editText.setTextColor(softKeyboard.skinInfoManager.skinData.textcolors_preEdit);
        editText.getBackground().setAlpha(Global.getCurrentAlpha());
        editText.setShadowLayer(Global.shadowRadius,0,0,softKeyboard.skinInfoManager.skinData.shadow);
    }

    public boolean isShown() {
        return container.isShowing() | editText.isShown();
    }

    public void refresh(){
        if(editText==null) return;
        String pinyin = Kernel.getWordsShowPinyin();
        if(pinyin.length()>0){
            show(pinyin);
            editText.setSelection(Math.min(selectStart,pinyin.length()),Math.min(selectStop,pinyin.length()));
        } else {
            dismiss();
        }
    }

    public void show(CharSequence text){
        editText.setText(text);
        float length = toolPaint.measureText((String) text);
        //这两个magic number都是调参来的……
        editText.setTextSize(Math.min((float) (container.getHeight()*0.33),6*container.getWidth()/length));
        if (!isShown()){
            container.showAsDropDown(softKeyboard.keyboardLayout,leftMargin,-container.getHeight()-softKeyboard.keyboardParams.height);
        }
    }

    public void dismiss(){
        container.dismiss();
    }

    public void setCursor(int cursor) {
        this.selectStart = Math.max(cursor,0);
        this.selectStop = Math.max(cursor,0);
    }

    public void setCursor(int start,int stop) {
        this.selectStart = Math.max(start,0);
        this.selectStop = Math.max(stop,0);
    }

    /**
     * for sync the cursor of inputConnection and edit
     * */
    private View.OnClickListener editOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputConnection ic = softKeyboard.getCurrentInputConnection();
            ic.setSelection(editText.getSelectionStart(),editText.getSelectionEnd());
        }
    };

}