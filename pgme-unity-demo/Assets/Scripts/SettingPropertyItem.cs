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
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using GMME;
using UnityEngine.UI;

public class SettingPropertyItem : MonoBehaviour
{
    public InputField keyInput;
    
    public InputField valueInput;
    
    public Button deleteBtn;

    private string _index;
    
    void Start()
    {
        deleteBtn.onClick.AddListener(DeteteClick);
        keyInput.onEndEdit.AddListener(KeyInputEndEdit);
        valueInput.onEndEdit.AddListener(ValueInputEndEdit);
    }
    
    void Update()
    {
        
    }
    
    void KeyInputEndEdit(string content)
    {
        ChangeValue();
    }
    
    void ValueInputEndEdit(string content)
    {
        ChangeValue();
    }
   
    
    public void UpdateItem(Dictionary<string, string> dic)
    {
        string index = "-1";
        string perpertyKey = "";
        string perpertyValue = "";
        dic.TryGetValue("index", out index);
        _index = index;
        dic.TryGetValue("perpertyKey", out perpertyKey);
        dic.TryGetValue("perpertyValue", out perpertyValue);
        keyInput.text = perpertyKey;
        valueInput.text = perpertyValue;
    }

    private void DeteteClick()
    {
        Loom.QueueOnMainThread(() =>
        {
            SendMessageUpwards("ItemDeleteIndex", _index, SendMessageOptions.RequireReceiver);
        });
    }
    
    void ChangeValue()
    {
        Dictionary<string, string> propertyDic = new Dictionary<string, string>();
        propertyDic.Add("perpertyKey", keyInput.text);
        propertyDic.Add("perpertyValue", valueInput.text);
        propertyDic.Add("index", _index);
        Loom.QueueOnMainThread(() =>
        {
            SendMessageUpwards("ChangePropertyDic", propertyDic, SendMessageOptions.RequireReceiver);
        });
    }
}
