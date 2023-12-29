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
using System.Linq;
using GMME;
using UnityEngine.UI;

public class SettingProperty : MonoBehaviour
{
    // 新增按钮
    public Button addButton;
    
    // 确定按钮
    public Button okButton;
    
    // 取消按钮
    public Button canceButton;
    
    // listview
    public RectTransform content;
    
    // item
    public GameObject itemPrefab;
    
    // 属性
    private Dictionary<string, string> _propertyDic = new Dictionary<string, string>();
    
    // 需要添加的列表
    private List<Dictionary<string, string>> _newPropertyDicList = new ();  

    void Start()
    {
        ChangeBtnArea();
        addButton.onClick.AddListener(AddClick);
        canceButton.onClick.AddListener(CanceClick);
        okButton.onClick.AddListener(OKClick);
    }

    void OnDestroy()
    {
        _newPropertyDicList = null;
    }


    void ChangeBtnArea()
    {
        PhoneAdaptive.ChangeBtnClickArea(addButton);
        PhoneAdaptive.ChangeBtnClickArea(canceButton);
        PhoneAdaptive.ChangeBtnClickArea(okButton);
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    private void AddClick()
    {
        if (_newPropertyDicList.Count >= 5)
        {
            return;
        }
        Dictionary<string, string> propertyDic = new Dictionary<string, string>();
        _newPropertyDicList.Add(propertyDic);
        ReloadListViewFunc();
    }
    
    private void CanceClick()
    {
        SendMessageUpwards("SetPropertyCancel", SendMessageOptions.RequireReceiver);
    }
    
    private void OKClick()
    {
        Loom.QueueOnMainThread(() =>
        {
            SendMessageUpwards("AddPropertyFunc", _newPropertyDicList, SendMessageOptions.RequireReceiver);
        });
    }

    private void ItemDeleteIndex(string index)
    {
        if (int.Parse(index) >= 0)
        {
            _newPropertyDicList.RemoveAt(int.Parse(index));
        }
        ReloadListViewFunc();
    }

    private void UpdatePropertyList(List<Dictionary<string, string>> list)
    {
        foreach (var dic in list)
        {
            _newPropertyDicList.Add(dic);
        }
        ReloadListViewFunc();
    }

    private void DeleteProperty(string index)
    {
        int indx = int.Parse(index);
        if (indx >= 0)
        {
            _newPropertyDicList.RemoveAt(indx); 
        }
        ReloadListViewFunc();
    }

    private void ChangePropertyDic(Dictionary<string, string> dic)
    {
        string index = "-1";
        string perpertyKey = "";
        string perpertyValue = "";
        dic.TryGetValue("index", out index);
        dic.TryGetValue("perpertyKey", out perpertyKey);
        dic.TryGetValue("perpertyValue", out perpertyValue);
        int i = 0;
        List<Dictionary<string, string>> newPropertyDicList = new ();
        foreach (var data in _newPropertyDicList)
        {
            if (i == int.Parse(index))
            {
                Dictionary<string, string> temDictionary = new Dictionary<string, string>();
                temDictionary.Add(perpertyKey,perpertyValue);
                newPropertyDicList.Add(temDictionary); 
            }
            else
            {
                newPropertyDicList.Add(data);
            }

            i++;
        }
        _newPropertyDicList = newPropertyDicList;
    }

    // 刷新玩家位置列表
    private void ReloadListViewFunc()
    {
        foreach (Transform child in content.transform)
        {
            Destroy(child.gameObject);
        }
        
        if (_newPropertyDicList.Count >= 0)
        {
            for (int i = 0; i < _newPropertyDicList.Count; i++)
            {
                Dictionary<string, string> dic = _newPropertyDicList[i];
                Dictionary<string, string> propertyDic = new Dictionary<string, string>();
                GameObject item = Instantiate(itemPrefab, content.transform);
                item.transform.localScale = Vector3.one;
                string perpertyKey = "";
                string perpertyValue = "";
                if (dic != null)
                {
                    foreach (string key in dic.Keys)
                    {
                        if (key != null)
                        {
                            perpertyKey = key;
                        }
                
                        if (dic[key] != null)
                        {
                            perpertyValue = dic[key];
                        }
                    }
                }
                propertyDic.Add("index", i.ToString());
                propertyDic.Add("perpertyKey", perpertyKey);
                propertyDic.Add("perpertyValue", perpertyValue);
                item.SendMessage("UpdateItem", propertyDic, SendMessageOptions.RequireReceiver); 
            }
        }
    }
}
