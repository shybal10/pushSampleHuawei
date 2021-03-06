/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.push.sample.shybal;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.PhoneAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView accountTv;
    private EditText countryCodeEdit;
    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText verifyCodeEdit;
    private ViewGroup countryCodeLayout;
    private LinearLayout galleryLayout;
    private Type type = Type.EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        accountTv = findViewById(R.id.tv_account);
        countryCodeEdit = findViewById(R.id.et_country_code);
        accountEdit = findViewById(R.id.et_account);
        passwordEdit = findViewById(R.id.et_password);
        verifyCodeEdit = findViewById(R.id.et_verify_code);
        RadioGroup radioGroup = findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radiobutton_email:
                        type = Type.EMAIL;
                        break;
                    case R.id.radiobutton_phone:
                        type = Type.PHONE;
                        break;
                }
                updateView();
            }
        });

        countryCodeLayout = findViewById(R.id.layout_cc);
        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);
        Button registerBtn = findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(this);
        Button send = findViewById(R.id.btn_send);
        send.setOnClickListener(this);
        galleryLayout = findViewById(R.id.id_gallery);
        findViewById(R.id.btn_login_anonymous).setOnClickListener(this);
    }



    public class ThirdListener implements View.OnClickListener {
        private String name;

        ThirdListener(String name) {
            this.name = name;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private void updateView() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_send:
                sendVerificationCode();
                break;
        }
    }


    private void sendVerificationCode() {
        VerifyCodeSettings settings = VerifyCodeSettings.newBuilder()
            .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(30) //shortest send interval ???30-120s
            .locale(Locale.getDefault()) //optional,must contain country and language eg:zh_CN
            .build();
        /*
        if (type == Type.EMAIL) {
            String email = accountEdit.getText().toString().trim();
            Task<VerifyCodeResult> task = EmailAuthProvider.requestVerifyCode(email, settings);
            task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
                @Override
                public void onSuccess(VerifyCodeResult verifyCodeResult) {
                    //You need to get the verification code from your email
                }
            }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(LoginActivity.this, "requestVerifyCode fail:" + e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {*/
            String countryCode = countryCodeEdit.getText().toString().trim();
            String phoneNumber = accountEdit.getText().toString().trim();
            Task<VerifyCodeResult> task = PhoneAuthProvider.requestVerifyCode(countryCode, phoneNumber, settings);
            task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
                @Override
                public void onSuccess(VerifyCodeResult verifyCodeResult) {
                    Log.d("Success Result",verifyCodeResult.toString());
                    Toast.makeText(LoginActivity.this, "requestVerifyCode Success:\n" + verifyCodeResult, Toast.LENGTH_LONG).show();
                    //You need to get the verification code from your phone
                }
            }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(LoginActivity.this, "requestVerifyCode fail:" + e, Toast.LENGTH_SHORT).show();
                }
            });
        }


    private void login() {
        if (type == Type.EMAIL) {
            String email = accountEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();
            String verifyCode = verifyCodeEdit.getText().toString().trim();
            AGConnectAuthCredential credential;
            if (TextUtils.isEmpty(verifyCode)) {
                credential = EmailAuthProvider.credentialWithPassword(email, password);
            } else {
                //If you do not have a password, param password can be null
                credential = EmailAuthProvider.credentialWithVerifyCode(email, password, verifyCode);
            }

        } else {
            String countryCode = countryCodeEdit.getText().toString().trim();
            String phoneNumber = accountEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();
            String verifyCode = verifyCodeEdit.getText().toString().trim();
            AGConnectAuthCredential credential;
            if (TextUtils.isEmpty(verifyCode)) {
                credential = PhoneAuthProvider.credentialWithPassword(countryCode, phoneNumber, password);
            } else {
                //If you do not have a password, param password can be null
                credential = PhoneAuthProvider.credentialWithVerifyCode(countryCode, phoneNumber, password, verifyCode);
            }
        }
    }




    enum Type {
        EMAIL,
        PHONE
    }
}
