package com.regall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.regall.old.MainActivity;
import com.regall.old.SmsReceiver;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestConfirmRegistration;
import com.regall.old.network.request.RequestRegisterClient;
import com.regall.old.network.response.ResponseRegisterClient;

/**
 * Created by Alex on 17.01.2015.
 */
public class RegistrationActivity extends Activity {

    private static final int STATE_ENTER_PHONE = 1;
    private static final int STATE_ENTER_SMS_PASS = 2;
    private static final int STATE_SEND_SMS_PASS = 3;
    private static final int STATE_SUCCESS = 4;
    private static final int STATE_ERROR = 5;

    @InjectView(R.id.registerScreenButton) ImageView buttonView;
    @InjectView(R.id.registerScreenIndicator) ImageView indicatorView;
    @InjectView(R.id.registerScreenCloseButton) ImageView closeButton;
    @InjectView(R.id.registerScreenInputContainer) ViewGroup inputContainer;
    @InjectView(R.id.registerScreenStatusContainer) ViewGroup statusContainer;
    @InjectView(R.id.registerScreenInput) EditText inputView;
    @InjectView(R.id.registerScreenLabel1) TextView label1;
    @InjectView(R.id.registerScreenLabel2) TextView labell2;
    @InjectView(R.id.registerScreenStatusImage) ImageView statusImage;
    @InjectView(R.id.registerScreenStatusLabel1) TextView statusLabel1;
    @InjectView(R.id.registerScreenStatusLabel2) TextView statusLabel2;

    private int state;
    private String phoneNumber;
    private String smsCode;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_register);
        ButterKnife.inject(this);
        progressDialog = new ProgressDialog(this);
        setState(STATE_ENTER_PHONE);
    }

    public void showProgressDialog(int messageId) {
        hideProgressDialog();
        progressDialog.setMessage(getString(messageId));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void setState(int newState) {
        switch (newState) {
            case STATE_ENTER_PHONE:
                inputView.removeTextChangedListener(smsTextWatcher);
                inputView.setText("");
                inputView.addTextChangedListener(phoneTextWatcher);
                inputView.setInputType(InputType.TYPE_CLASS_PHONE);
                buttonView.setEnabled(false);
                buttonView.setImageResource(R.drawable.new_reg_screen_send_button_inactive);
                buttonView.setOnClickListener(phoneClickListener);
                label1.setText("Для входа в систему Вам необходимо пройти процедуру регистрации");
                labell2.setText("Введите Ваш номер телефона");
                statusContainer.setVisibility(View.GONE);
                inputContainer.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                closeButton.setOnClickListener(successClickListener);
                indicatorView.setImageResource(R.drawable.new_reg_screen_state_1);
                break;
            case STATE_ENTER_SMS_PASS:
                inputView.removeTextChangedListener(phoneTextWatcher);
                inputView.setText("");
                inputView.addTextChangedListener(smsTextWatcher);
                inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
                buttonView.setEnabled(false);
                buttonView.setImageResource(R.drawable.new_reg_screen_apply_button_inactive);
                buttonView.setOnClickListener(smsClickListener);
                label1.setText("На Ваш телефон был отправлен SMS-пароль");
                labell2.setText("Введите SMS-пароль");
                statusContainer.setVisibility(View.GONE);
                inputContainer.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                closeButton.setOnClickListener(successClickListener);
                indicatorView.setImageResource(R.drawable.new_reg_screen_state_2);
                break;
            case STATE_SEND_SMS_PASS:
                break;
            case STATE_SUCCESS:
                inputView.removeTextChangedListener(smsTextWatcher);
                statusContainer.setVisibility(View.VISIBLE);
                inputContainer.setVisibility(View.GONE);
                indicatorView.setImageResource(R.drawable.new_reg_screen_state_4);
                statusImage.setImageResource(R.drawable.new_reg_screen_success);
                buttonView.setImageResource(R.drawable.new_reg_screen_start_using_button);
                buttonView.setOnClickListener(successClickListener);
                closeButton.setVisibility(View.GONE);
                statusLabel1.setText("Поздравляем!\nВы успешно зарегистрировались!");
                statusLabel2.setText("Все последующие входы будут происходить автоматически");
                break;
            case STATE_ERROR:
                inputView.removeTextChangedListener(smsTextWatcher);
                statusContainer.setVisibility(View.VISIBLE);
                inputContainer.setVisibility(View.GONE);
                indicatorView.setImageResource(R.drawable.new_reg_screen_state_5);
                statusImage.setImageResource(R.drawable.new_reg_screen_error);
                buttonView.setOnClickListener(errorClickListener);
                buttonView.setImageResource(R.drawable.new_reg_screen_go_back_button);
                closeButton.setVisibility(View.GONE);
                statusLabel1.setText("Произошла ошибка!");
                statusLabel2.setText("Неверно введен код регистрации");
                break;
        }
        this.state = newState;
    }

    private TextWatcher phoneTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String rawPhone = TextUtils.isEmpty(s) ? "" : s.toString().trim().replaceAll("\\D+", "");
            boolean isPhoneValid = rawPhone.length() > 9 && rawPhone.length() < 13;
            buttonView.setImageResource(isPhoneValid ? R.drawable.new_reg_screen_send_button : R.drawable.new_reg_screen_send_button_inactive);
            buttonView.setEnabled(isPhoneValid);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private TextWatcher smsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            buttonView.setImageResource(s.length() == 5 ? R.drawable.new_reg_screen_apply_button : R.drawable.new_reg_screen_apply_button_inactive);
            buttonView.setEnabled(s.length() == 5);
            indicatorView.setImageResource(s.length() == 5 ? R.drawable.new_reg_screen_state_3 : R.drawable.new_reg_screen_state_2);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener phoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            phoneNumber = inputView.getText().toString().trim().replaceAll("\\D+", "");
            RequestRegisterClient request = RequestRegisterClient.create(phoneNumber, "");
            API api = new API(getString(R.string.server_url));
            showProgressDialog(R.string.message_loading);
            api.registerNewClient(request, sendPhoneCallback);
        }
    };

    private View.OnClickListener smsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            smsCode = inputView.getText().toString();
            RequestConfirmRegistration request = RequestConfirmRegistration.create(phoneNumber, smsCode);
            API api = new API(getString(R.string.server_url));
            showProgressDialog(R.string.message_loading);
            api.confirmRegistration(request, sendSMSCodeCallback);
        }
    };

    private View.OnClickListener errorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setState(STATE_ENTER_SMS_PASS);
        }
    };

    private View.OnClickListener successClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }
    };

    private Callback<ResponseRegisterClient> sendPhoneCallback = new Callback<ResponseRegisterClient>() {
        @Override
        public void success(Object object) {
            hideProgressDialog();
            ResponseRegisterClient response = (ResponseRegisterClient) object;

            int responseCode = response.getStatusCode();
            if (responseCode == ResponseRegisterClient.STATUS_OK || responseCode == ResponseRegisterClient.STATUS_USER_NOT_ACTIVATED) {
                SharedPreferences prefs = getSharedPreferences(SmsReceiver.PREFERENCES_REGISTRATION, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(SmsReceiver.PREFERENCES_REGISTRATION_PHONE, phoneNumber);
                editor.apply();

                setState(STATE_ENTER_SMS_PASS);
            } else if(responseCode == ResponseRegisterClient.STATUS_USER_EXISTS){
                Toast.makeText(RegistrationActivity.this, "Такой пользователь уже существует", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RegistrationActivity.this, response.getStatusDetail(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void failure(Exception e) {
            e.printStackTrace();
            hideProgressDialog();
            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private Callback<ResponseRegisterClient> sendSMSCodeCallback = new Callback<ResponseRegisterClient>() {
        @Override
        public void success(Object object) {
            hideProgressDialog();
            ResponseRegisterClient response = (ResponseRegisterClient) object;
            if (response.isSuccess()) {
                setState(STATE_SUCCESS);
                Toast.makeText(RegistrationActivity.this, response.getStatusDetail(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                finish();
            } else {
                setState(STATE_ERROR);
                Toast.makeText(RegistrationActivity.this, response.getStatusDetail(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void failure(Exception e) {
            e.printStackTrace();
            hideProgressDialog();
            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            setState(STATE_ERROR);
        }
    };
}
