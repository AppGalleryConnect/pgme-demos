/**
 * Copyright 2023. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
using System;
using UnityEngine;
using UnityEngine.UIElements;
using Button = UnityEngine.UI.Button;
using Image = UnityEngine.UI.Image;

public class PhoneAdaptive
{
    public static void ChangeBtnClickArea(Button btn)
    {
        if (!NeedChangeBtnClickArea())
        {
            return;
        }
        Image image = btn.GetComponent<Image>();
        if (image != null)
        {
            image.raycastPadding = new Vector4(x: 0, y: 0, z: 0, w: -50);
        }
    }
    public static bool NeedChangeBtnClickArea()
    {
#if  UNITY_IOS && !UNITY_EDITOR
        string modelStr = SystemInfo.deviceModel;
        string modelType = modelStr.ToLower().Trim().Substring(0, 3);
        if (modelType == "iph")
        {
            // iPhone 12mini "iPhone13,1"
            return modelStr.Equals("iPhone13,1");
        }
        else
        {
            return false;
        }
#endif
        return false;
    }
}
