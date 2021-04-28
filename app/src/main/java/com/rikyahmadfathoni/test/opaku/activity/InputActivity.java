package com.rikyahmadfathoni.test.opaku.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityInputBinding;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

public class InputActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(FragmentActivity activity, String titleName, int inputType) {
        start(activity, titleName, null, inputType);
    }

    public static void start(FragmentActivity activity, String titleName,  String inputValue,
                             int inputType) {
        if (activity != null) {
            final Intent intent = new Intent(activity, InputActivity.class);
            intent.putExtra(KEY_TITLE_NAME, titleName);
            intent.putExtra(KEY_INPUT_TYPE, inputType);
            intent.putExtra(KEY_INPUT_VALUE, inputValue);
            activity.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private static final String KEY_TITLE_NAME = "KEY_TITLE_NAME";
    public static final String KEY_INPUT_TYPE = "KEY_INPUT_TYPE";
    public static final String KEY_INPUT_VALUE = "KEY_INPUT_VALUE";

    public static final int REQUEST_CODE = 100;

    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_EMAIL = 1;
    public static final int INPUT_TYPE_PASSWORD = 2;
    public static final int INPUT_TYPE_NUMBER = 3;

    private ActivityInputBinding binding;
    private String titleName;
    private String inputValue;
    private int inputType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindIntent(getIntent());
        bindData();
        bindEvent();
    }

    private void bindEvent() {
        binding.header.iconBack.setOnClickListener(this);
        binding.buttonSave.setOnClickListener(this);
        binding.inputText.addTextChangedListener(textWatcher);
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            titleName = intent.getStringExtra(KEY_TITLE_NAME);
            inputType = intent.getIntExtra(KEY_INPUT_TYPE, INPUT_TYPE_TEXT);
            inputValue = intent.getStringExtra(KEY_INPUT_VALUE);
        }
    }

    private void bindData() {
        binding.header.textTitle.setText(titleName);
        binding.inputText.setInputType(getInputType());
        binding.inputText.setHint(R.string.hint_input_here);
        binding.inputText.setText(inputValue);
    }

    private int getInputType() {
        if (inputType == INPUT_TYPE_PASSWORD) {
            return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        } else if (inputType == INPUT_TYPE_NUMBER) {
            return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
        } else if (inputType == INPUT_TYPE_EMAIL) {
            return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        }
        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            super.onBackPressed();
        } else if (view == binding.buttonSave) {
            final String inputText = binding.inputText.getText().toString();
            if (inputType == INPUT_TYPE_EMAIL) {
                if (!UtilsString.isValidEmail(inputText)) {
                    UtilsDialog.showToast(this, getString(R.string.message_invalid_email));
                    return;
                }
            }
            final Intent returnIntent = new Intent();
            returnIntent.putExtra(KEY_INPUT_VALUE, inputText);
            returnIntent.putExtra(KEY_INPUT_TYPE, inputType);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (binding == null) {
                return;
            }
            binding.buttonSave.setEnabled(true);
        }
    };
}