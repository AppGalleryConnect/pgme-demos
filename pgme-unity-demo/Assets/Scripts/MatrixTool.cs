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

namespace GMME
{
    public class MatrixTool
    {
        static public float[] GetForwardMatrix(float right, float up, float forward)
        {
            float[] forwards = new float[3];
            forwards[0] = Cos(forward) * Cos(up);
            forwards[1] = -Sin(forward) * Cos(up);
            forwards[2] = Sin(up);
            return forwards;
        }
        
        static public float[] GetRightMatrix(float right, float up, float forward)
        {
            float[] rights = new float[3];
            rights[0] = Sin(forward) * Cos(right) + Cos(forward) * Sin(up) * Sin(right);
            rights[1] = Cos(forward) * Cos(right) - Sin(forward) * Sin(up) * Sin(right);
            rights[2] = -Cos(up) * Sin(right);
            return rights;
        }

        static public float[] GetUpMatrix(float right, float up, float forward)
        {
            float[] ups = new float[3];
            ups[0] = Sin(forward) * Sin(right) - Cos(forward) * Sin(up) * Cos(right);
            ups[1] = Cos(forward) * Sin(right) + Sin(forward) * Sin(up) * Cos(right);
            ups[2] = Cos(up) * Cos(right);
            return ups;
        }

        static float Cos(float theta)
        {
            double value = Math.Cos(theta * 3.14 / 180);
            return (float)value;
        }

        static float Sin(double theta)
        {
           double value =  Math.Sin(theta * 3.14 / 180);
           return (float)value;
        }

    }
}