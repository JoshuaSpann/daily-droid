package com.example.jspann.textfilewriter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by jspann on 11/30/2017.
 */

public class JavaUtils {
    public void SetOnTextChangedListener(EditText editText, Object callback){
        Object mycallback = callback;
        //callback;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
               // mycallback;
            }
        });
    }
}
