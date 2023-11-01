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
using System.Collections.Generic;
using GMME;
using UnityEngine;
using UnityEngine.UI;

public class PositionItem : BaseCtr
{
    public Text openId;
    public InputField xInputField;
    public InputField yInputField;
    public InputField zInputField;
    public Button deleteBtn;

    private void Start()
    {
        AddListenerFunc();
    }


    void AddListenerFunc()
    {
        deleteBtn.onClick.AddListener(DeleteFunc);
        xInputField.onEndEdit.AddListener(XpositionInputEndEdit);
        yInputField.onEndEdit.AddListener(YpositionInputEndEdit);
        zInputField.onEndEdit.AddListener(ZpositionInputEndEdit);
    }

    public void RenderItem(RemotePlayerPosition position)
    {
        openId.text = position.OpenId;
        xInputField.text = position.Position.Right.ToString();
        yInputField.text = position.Position.Up.ToString();
        zInputField.text = position.Position.Forward.ToString();
    }

    // 删除
    void DeleteFunc()
    {
        SendMessageUpwards("DeletePlayerPosition", openId.text, SendMessageOptions.RequireReceiver);
    }

    // 位置 X轴
    void XpositionInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsPositionInputOutRange(float.Parse(content)))
        {
            xInputField.text = "0";
            SendMessageUpwards("ShowPositionErrorTips", SendMessageOptions.RequireReceiver);
        }

        ChangePosition();
    }

    // 位置 Y轴
    void YpositionInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsPositionInputOutRange(float.Parse(content)))
        {
            yInputField.text = "0";
            SendMessageUpwards("ShowPositionErrorTips", SendMessageOptions.RequireReceiver);
        }

        ChangePosition();
    }

    // 位置 Z轴
    void ZpositionInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsPositionInputOutRange(float.Parse(content)))
        {
            zInputField.text = "0";
            SendMessageUpwards("ShowPositionErrorTips", SendMessageOptions.RequireReceiver);
        }

        ChangePosition();
    }

    // 修改坐标
    void ChangePosition()
    {
        Dictionary<string, string> position = new Dictionary<string, string>();
        position.Add("openId", openId.text);
        position.Add("right", xInputField.text);
        position.Add("forward", zInputField.text);
        position.Add("up", yInputField.text);
        SendMessageUpwards("ChangeRemotePosition", position, SendMessageOptions.RequireReceiver);
    }

    bool IsPositionInputOutRange(float value)
    {
        return IsValueOutRange(100.0f, -100.0f, value);
    }

    bool IsValueOutRange(float maxValue, float minValue, float value)
    {
        return (value > maxValue) || (value < minValue);
    }
}