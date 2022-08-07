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

package com.huawei.gmmesdk.demo.sign;

import android.util.Base64;

import com.huawei.game.common.https.Utils;
import com.huawei.game.common.utils.LogUtil;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;

/**
 * 生成接入签名，使用SHA256withRSA/PSS
 */
public class Signer {
    private static final String TAG = Signer.class.getSimpleName();

    public static final String ALGORITHM_NAME = "SHA256withRSA/PSS";

    public static final String MD_NAME = "SHA-256";

    public static final String MGF_NAME = "MGF1";

    public static final int SALT_LEN = 32;

    public static final int TRAILER_FIELD = 1;

    public static String generate(String appId, String openId, String nonce, String timestamp, String privateKey) {
        if (Utils.isEmpty(privateKey)) {
            LogUtil.e(TAG, "privateKey is empty.");
            return null;
        }
        PrivateKey priKey = getPrivateKey(privateKey);
        if (priKey == null) {
            LogUtil.e(TAG, "getPrivateKey failed.");
            return null;
        }
        StringBuilder sourceStr = new StringBuilder().append("appId=")
            .append(appId)
            .append("&nonce=")
            .append(nonce)
            .append("&openId=")
            .append(openId)
            .append("&timestamp=")
            .append(timestamp);
        return sign(sourceStr.toString(), priKey);
    }

    public static PrivateKey getPrivateKey(String privateKeyStr) {
        try {
            byte[] privatekey = Base64.decode(privateKeyStr, Base64.NO_WRAP);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privatekey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, "base64 decode IllegalArgumentException");
        } catch (GeneralSecurityException e) {
            LogUtil.e(TAG, "load Key Exception:" + e.getMessage());
        } catch (Exception e) {
            LogUtil.e(TAG, "base64 decode Exception" + e.getMessage());
        }
        return null;
    }

    private static String sign(String data, PrivateKey priKey) {
        try {
            Signature signature = Signature.getInstance(ALGORITHM_NAME);
            signature.setParameter(
                new PSSParameterSpec(MD_NAME, MGF_NAME, MGF1ParameterSpec.SHA256, SALT_LEN, TRAILER_FIELD));
            signature.initSign(priKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] result = signature.sign();
            return Base64.encodeToString(result, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(TAG, "sign NoSuchAlgorithmException: " + e.getMessage());
        } catch (InvalidKeyException e) {
            LogUtil.e(TAG, "sign InvalidKeyException: " + e.getMessage());
        } catch (SignatureException e) {
            LogUtil.e(TAG, "sign SignatureException: " + e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            LogUtil.e(TAG, "sign InvalidAlgorithmParameterException: " + e.getMessage());
        } catch (Exception e) {
            LogUtil.e(TAG, "sign Exception: " + e.getMessage());
        }
        return null;
    }
}