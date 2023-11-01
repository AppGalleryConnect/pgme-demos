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
using UnityEngine;
using TMPro;
using System;
using System.Text;
using System.Collections;
using GMME;

public class BaseCtr : MonoBehaviour
{
    private readonly StringBuilder _cbMsgBuilder = new ();

    public TMP_Text callbackMsg;

    public virtual void Update()
    {
        if (callbackMsg)
        {
            RefreshTipText();
        }
    }

    private void RefreshTipText()
    {
        var callbackMessage = _cbMsgBuilder.ToString();
        if (callbackMessage.Length != 0)
        {
            callbackMsg.text = _cbMsgBuilder.ToString();
        }
    }

    protected void AppendCallbackMessage(string message)
    {
        if (_cbMsgBuilder.Length > 2000)
        {
            _cbMsgBuilder.Clear();
        }
        _cbMsgBuilder.Append(string.Format("{0:T}", DateTime.Now)).Append(" ").Append(message).Append(Environment.NewLine);
    }

    protected void ClearCallbackMessage()
    {
        _cbMsgBuilder.Clear();
        callbackMsg.text = "";
    }

    public static IEnumerator DelayToInvokeDo(Action action, float delaySeconds)
    {
        yield return new WaitForSeconds(delaySeconds);
        action();
    }

    protected GameMediaEngine GetEngineInstance()
    {
        GameMediaEngine engine = GMMEEnginHolder.GetInstance().gMMEEngine;
        if (engine == null)
        {
            AppendCallbackMessage("engine is null, please init engine first");
            Debug.Log("engine is null, please init engine first");
        }
        return engine;
    }
}
