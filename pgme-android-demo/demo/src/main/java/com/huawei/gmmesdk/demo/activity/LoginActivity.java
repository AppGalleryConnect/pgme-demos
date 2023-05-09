/*
   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.huawei.gmmesdk.demo.activity;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.gmmesdk.demo.Constant;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.log.Log;
import com.huawei.gmmesdk.demo.log.LogFragment;

/**
 * demo首页
 */
public class LoginActivity extends LoginLoggerActivity implements View.OnClickListener {
    /**
     * 日志标签
     */
    public static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * 申请权限请求码
     */
    private static final int REQUEST_PERMISSIONS_CODE = 0X001;

    /**
     * 所需权限
     */
    private static final String[] NEED_PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE};

    private long mLastClickTime = 0L;

    private EditText userIdView;

    /**
     * 权限是否已获取标志
     */
    private boolean mPermissionGranted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查权限
        checkPermissions();

        setContentView(R.layout.activity_main);
        findViewById(R.id.hw_gmme_init).setOnClickListener(this);
        userIdView = findViewById(R.id.openid_input);
        addLogFragment();
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_PHONE_STATE)) {
                // 如果权限未获取，则申请权限
                requestPermissions(NEED_PERMISSIONS, REQUEST_PERMISSIONS_CODE);
                return;
            }
        }
        mPermissionGranted = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (int index = 0; index < grantResults.length; index++) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[index])) {
                        // 在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                        // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                        new AlertDialog.Builder(this).setMessage("需要开启权限才能使用此功能")
                            .setPositiveButton("设置", (dialog, which) -> {
                                // 引导用户到设置中去进行设置
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);
                            })
                            .show();
                    }
                    return;
                }
            }
            mPermissionGranted = true;
        }
    }

    /**
     * 添加日志输出
     */
    private void addLogFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        final LogFragment fragment = new LogFragment();
        transaction.replace(R.id.login_text_out, fragment);
        transaction.commit();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hw_gmme_init:
                if (System.currentTimeMillis() - mLastClickTime >= Constant.TIME_INTERVAL) {
                    mLastClickTime = System.currentTimeMillis();
                    gmmeInit();
                }
                break;
            default:
                break;
        }
    }

    private void gmmeInit() {
        // 再次检查权限
        if (!mPermissionGranted) {
            checkPermissions();
            return;
        }

        // 检查是否输入用户ID
        String userId = userIdView.getText().toString().trim();
        if (TextUtils.isEmpty(userId)) {
            Log.i(TAG, "please input userid");
            return;
        }
        ((GmmeApplication) getApplication()).setOpenId(userId);
        GameMediaEngine rtcEngine = ((GmmeApplication) getApplication()).getEngine();
        if (rtcEngine == null) {
            Log.i(TAG, "engine is null");
            return;
        }
        rtcEngine.enableMic(false);
        Intent intent = new Intent(this, GmmeRoomActivity.class);
        intent.putExtra(Constant.KEY_USER_ID, userId);
        startActivityForResult(intent, Constant.GMME_INIT_ENGINE);
        Log.i(TAG, "engine create success");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}