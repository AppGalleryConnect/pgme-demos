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
using System.Collections;
using System.Collections.Generic;
using GMME;
using UnityEngine;
using UnityEngine.UI;

public class PlayerPositionInfo : BaseCtr
{
    // Start is called before the first frame update

    // 返回按钮
    public Button backBtn;

    // 新增玩家按钮
    public Button addPlayerBtn;

    // 清空所有按钮
    public Button clearAllBtn;

    // 设置范围按钮
    public Button setRangeBtn;

    // 位置 X轴输入框
    public InputField xPositionInput;

    // 位置 Y轴输入框
    public InputField yPositionInput;

    // 位置 Z轴输入框
    public InputField zPositionInput;

    // 朝向 X轴输入框
    public InputField xAxisInput;

    // 朝向 Y轴输入框
    public InputField yAxisInput;

    // 朝向 Z轴输入框
    public InputField zAxisInput;

    // 当前玩家openId
    public Text selfPlayerId;
    //  提示信息
    public Text tipsText;

    public GameObject itemPrefab;
    public RectTransform content;
    private ArrayList otherPostions = new ArrayList();
    private GameMediaEngine _engine;
    private GameObject _rangeSetObj; // 设置范围面板
    private GameObject _addPlayerObj; // 新增玩家面板

    public delegate void PlayerPositionEventDelegate();

    public static PlayerPositionEventDelegate EventClosePlayerPosition;
    private int saveRange;

    void Start()
    {
        _engine = GetEngineInstance();
        AddListenerFunc();
        selfPlayerId.text = UserAppCfg.GetUserID();
        SetDefaultValue();
        ReloadPlayerPositionFunc();
        ChangeBtnArea();
    }

    void ChangeBtnArea()
    {
        PhoneAdaptive.ChangeBtnClickArea(backBtn);
        PhoneAdaptive.ChangeBtnClickArea(clearAllBtn);
        PhoneAdaptive.ChangeBtnClickArea(addPlayerBtn);
        PhoneAdaptive.ChangeBtnClickArea(setRangeBtn);
    }

    void AddListenerFunc()
    {
        backBtn.onClick.AddListener(BackFunc);
        addPlayerBtn.onClick.AddListener(ShowAddPlayer);
        clearAllBtn.onClick.AddListener(ClearAllFunc);
        setRangeBtn.onClick.AddListener(ShowSetRange);
        xPositionInput.onEndEdit.AddListener(XpositionInputEndEdit);
        yPositionInput.onEndEdit.AddListener(YpositionInputEndEdit);
        zPositionInput.onEndEdit.AddListener(ZpositionInputEndEdit);
        xAxisInput.onEndEdit.AddListener(XaxisInputEndEdit);
        yAxisInput.onEndEdit.AddListener(YaxisInputEndEdit);
        zAxisInput.onEndEdit.AddListener(ZaxisInputEndEdit);
    }

    void SetDefaultValue()
    {
        xAxisInput.text = "0";
        yAxisInput.text = "0";
        zAxisInput.text = "0";
        xPositionInput.text = "0";
        yPositionInput.text = "0";
        zPositionInput.text = "0";
    }

    // Update is called once per frame
    void Update()
    {
    }

    // 返回
    void BackFunc()
    {
        EventClosePlayerPosition();
    }

    private void ShowSetRange()
    {
        _rangeSetObj = Resources.Load<GameObject>("Prefabs/SetRange");
        _rangeSetObj = Instantiate(_rangeSetObj, gameObject.transform);
        _rangeSetObj.transform.localScale = Vector3.one;
        Button confirmBtn = _rangeSetObj.transform.Find("RangeBtnConfirm").GetComponent<Button>();
        confirmBtn.onClick.AddListener(() => SetRangeConfirm());
        Button cancelBtn = _rangeSetObj.transform.Find("RangeBtnCancel").GetComponent<Button>();
        cancelBtn.onClick.AddListener(() => SetRangeCancel());
        InputField rangeText = _rangeSetObj.transform.Find("RangeField").GetComponent<InputField>();
        if (saveRange > 0)
        {
            rangeText.text = saveRange.ToString();
        }
        else
        {
            rangeText.text = "";
        }
    }

    private void SetRangeCancel()
    {
        DestroyImmediate(_rangeSetObj);
        tipsText.text = "";
    }

    private void SetRangeConfirm()
    {
        InputField rangeText = _rangeSetObj.transform.Find("RangeField").GetComponent<InputField>();
        if (rangeText.text.Trim() == "")
        {
            tipsText.text = "请输入范围";
            return;
        }

        if (long.Parse(rangeText.text) <= 0)
        {
            tipsText.text = "范围值需大于0";
            return;
        }
        long range = long.Parse(rangeText.text.ToString());
        if (range > Int32.MaxValue)
        {
            tipsText.text = "设置失败，最大值应小于等于" + Int32.MaxValue.ToString();
            return;
        }

        SetRangeFunc((int)range);
        DestroyImmediate(_rangeSetObj);
        tipsText.text = "";
    }

    private void ShowAddPlayer()
    {
        _addPlayerObj = Resources.Load<GameObject>("Prefabs/AddPlayer");
        _addPlayerObj = Instantiate(_addPlayerObj, gameObject.transform);
        _addPlayerObj.transform.localScale = Vector3.one;
        Button confirmBtn = _addPlayerObj.transform.Find("BtnConfirm").GetComponent<Button>();
        confirmBtn.onClick.AddListener(() => AddPlayerConfirm());
        Button cancelBtn = _addPlayerObj.transform.Find("BtnCancel").GetComponent<Button>();
        cancelBtn.onClick.AddListener(() => AddPlayerCancel());
    }

    private void AddPlayerCancel()
    {
        tipsText.text = "";
        DestroyImmediate(_addPlayerObj);
    }

    private void AddPlayerConfirm()
    {
        InputField addText = _addPlayerObj.transform.Find("PlayerField").GetComponent<InputField>();
        if (addText.text.Trim() == "")
        {
            tipsText.text = "玩家ID不能为空";
            return;
        }

        if (addText.text == UserAppCfg.GetUserID())
        {
            tipsText.text = "本人不需要添加";
            return;
        }

        foreach (RemotePlayerPosition position in otherPostions)
        {
            if (position.OpenId == addText.text)
            {
                tipsText.text = "该玩家已存在";
                return;
            }
        }

        tipsText.text = "";
        DestroyImmediate(_addPlayerObj);
        AddPlayerFunc(addText.text);
    }

    // 新增玩家
    void AddPlayerFunc(string openId)
    {
        RemotePlayerPosition remotePlayerPosition = new RemotePlayerPosition();
        remotePlayerPosition.OpenId = openId;
        PlayerPosition playerPosition = new PlayerPosition();
        playerPosition.Right = 0.0f;
        playerPosition.Forward = 0.0f;
        playerPosition.Up = 0.0f;
        remotePlayerPosition.Position = playerPosition;
        otherPostions.Add(remotePlayerPosition);
        ReloadPlayerPositionFunc();
        UpdateRemotePosition();
    }

    // 清空所有玩家
    void ClearAllFunc()
    {
        foreach (Transform child in content.transform)
        {
            Destroy(child.gameObject);
        }

        otherPostions.Clear();
        ReloadPlayerPositionFunc();
        UpdateRemotePosition();
        _engine.ClearAllRemotePositions();
    }

    //  设置范围
    void SetRangeFunc(int range)
    {
        int flag = _engine.SetAudioRecvRange(range);
        if (flag == 0)
        {
            saveRange = range;
        }
    }

    // 位置 X轴
    void XpositionInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsPositionInputOutRange(float.Parse(content)))
        {
            xPositionInput.text = "0";
            ShowPositionErrorTips();
        }

        UpdateSelfPosition();
    }

    // 位置 Y轴
    void YpositionInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsPositionInputOutRange(float.Parse(content)))
        {
            yPositionInput.text = "0";
            ShowPositionErrorTips();
        }

        UpdateSelfPosition();
    }

    // 位置 Z轴
    void ZpositionInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsPositionInputOutRange(float.Parse(content)))
        {
            zPositionInput.text = "0";
            ShowPositionErrorTips();
        }

        UpdateSelfPosition();
    }

    // 朝向 X轴
    void XaxisInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsAxisInputOutRange(float.Parse(content)))
        {
            xAxisInput.text = "0";
            ShowAxisErrorTips();
        }

        UpdateSelfPosition();
    }

    // 朝向 Y轴
    void YaxisInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsAxisInputOutRange(float.Parse(content)))
        {
            yAxisInput.text = "0";
            ShowAxisErrorTips();
        }

        UpdateSelfPosition();
    }

    // 朝向 X轴
    void ZaxisInputEndEdit(string content)
    {
        if (content.Trim() == "" || IsAxisInputOutRange(float.Parse(content)))
        {
            zAxisInput.text = "0";
            ShowAxisErrorTips();
        }

        UpdateSelfPosition();
    }

    // 刷新玩家位置列表
    void ReloadPlayerPositionFunc()
    {
        foreach (Transform child in content.transform)
        {
            Destroy(child.gameObject);
        }

        foreach (RemotePlayerPosition position in otherPostions)
        {
            GameObject item = Instantiate(itemPrefab, content.transform);
            item.transform.localScale = Vector3.one;
            item.SendMessage("RenderItem", position, SendMessageOptions.RequireReceiver);
        }
    }

    // 删除玩家位置
    void DeletePlayerPosition(string openId)
    {
        int targetIndex = -1;
        for (int i = 0; i < otherPostions.Count; i++)
        {
            RemotePlayerPosition remotePlayerPosition = (RemotePlayerPosition)otherPostions[i];
            if (remotePlayerPosition.OpenId == openId)
            {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex >= 0)
        {
            otherPostions.RemoveAt(targetIndex);
            ReloadPlayerPositionFunc();
        }

        UpdateRemotePosition();
        _engine.ClearRemotePlayerPosition(openId);
    }

    // 修改远端玩家位置
    void ChangeRemotePosition(Dictionary<string, string> position)
    {
        string openId = "";
        string forward = "";
        string up = "";
        string right = "";
        position.TryGetValue("openId", out openId);
        position.TryGetValue("forward", out forward);
        position.TryGetValue("up", out up);
        position.TryGetValue("right", out right);
        foreach (RemotePlayerPosition remotePlayerPosition in otherPostions)
        {
            if (remotePlayerPosition.OpenId == openId)
            {
                remotePlayerPosition.Position.Forward = float.Parse(forward);
                remotePlayerPosition.Position.Right = float.Parse(right);
                remotePlayerPosition.Position.Up = float.Parse(up);
                break;
            }
        }

        UpdateRemotePosition();
    }


    // 上报自身坐标
    void UpdateSelfPosition()
    {
        SelfPosition selfPosition = new SelfPosition();
        PlayerPosition position = new PlayerPosition();
        position.Forward = FloatValueWithInput(zPositionInput);
        position.Up = FloatValueWithInput(yPositionInput);
        position.Right = FloatValueWithInput(xPositionInput);
        selfPosition.Position = position;
        Axis axis = new Axis();
        axis.Forward = MatrixTool.GetForwardMatrix(FloatValueWithInput(xAxisInput), FloatValueWithInput(yAxisInput),
            FloatValueWithInput(zAxisInput));
        axis.Right = MatrixTool.GetRightMatrix(FloatValueWithInput(xAxisInput), FloatValueWithInput(yAxisInput),
            FloatValueWithInput(zAxisInput));
        axis.Up = MatrixTool.GetUpMatrix(FloatValueWithInput(xAxisInput), FloatValueWithInput(yAxisInput),
            FloatValueWithInput(zAxisInput));
        selfPosition.Axis = axis;
        int flag = _engine.UpdateSelfPosition(selfPosition);
    }

    // 上报其他玩家坐标
    void UpdateRemotePosition()
    {
        RemotePlayerPosition[] targetPositions =
            (RemotePlayerPosition[])(otherPostions.ToArray(typeof(RemotePlayerPosition)));
        _engine.UpdateRemotePosition(targetPositions);
    }

    // 设置接收范围
    void SetRange(int range)
    {
        _engine.SetAudioRecvRange(range);
    }
    
    // 位置坐标输入错误提示
    void ShowPositionErrorTips()
    {
        tipsText.text = "位置范围 -100~100";
        Invoke("ClearError",1.5f);
    }
    
    // 朝向坐标输入错误
    void ShowAxisErrorTips()
    {
        tipsText.text = "朝向范围 -180~180";
        Invoke("ClearError",1.5f);
    }
    
    // 清除提示信息
    void ClearError()
    {
        tipsText.text = "";
    }
    float FloatValueWithInput(InputField inputField)
    {
        return inputField.text.Trim() == "" ? 0.0f : float.Parse(inputField.text);
    }

    bool IsPositionInputOutRange(float value)
    {
        return IsValueOutRange(100.0f, -100.0f, value);
    }

    bool IsAxisInputOutRange(float value)
    {
        return IsValueOutRange(180.0f, -180.0f, value);
    }

    bool IsValueOutRange(float maxValue, float minValue, float value)
    {
        return (value > maxValue) || (value < minValue);
    }
}