/*
   Copyright 2020-2022. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.gmmesdk.demo.log;

/**
 * 日志工具
 */
public class Log {
    private static final int DEBUG = android.util.Log.DEBUG;

    private static final int INFO = android.util.Log.INFO;

    private static final int WARN = android.util.Log.WARN;

    private static final int ERROR = android.util.Log.ERROR;

    private static LogNode mLogNode;

    /**
     * 输出日志
     */
    public interface LogNode {
        /**
         * 输出日志
         *
         * @param priority 级别
         * @param tag 标签
         * @param msg 消息
         * @param throwable 异常
         */
        void println(int priority, String tag, String msg, Throwable throwable);
    }

    private Log() {
    }

    public static LogNode getLogNode() {
        return mLogNode;
    }

    public static void setLogNode(LogNode node) {
        mLogNode = node;
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param throwable 异常
     */
    public static void d(String tag, String msg, Throwable throwable) {
        println(DEBUG, tag, msg, throwable);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param throwable 异常
     */
    public static void i(String tag, String msg, Throwable throwable) {
        println(INFO, tag, msg, throwable);
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param throwable 异常
     */
    public static void w(String tag, String msg, Throwable throwable) {
        println(WARN, tag, msg, throwable);
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param throwable 异常
     */
    public static void w(String tag, Throwable throwable) {
        w(tag, null, throwable);
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param throwable 异常
     */
    public static void e(String tag, String msg, Throwable throwable) {
        println(ERROR, tag, msg, throwable);
    }

    /**
     * 输出日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    /**
     * 输出日志
     *
     * @param priority 级别
     * @param tag 标签
     * @param msg 消息
     * @param throwable 异常
     */
    public static void println(int priority, String tag, String msg, Throwable throwable) {
        if (mLogNode != null) {
            mLogNode.println(priority, tag, msg, throwable);
        }
    }

    /**
     * 输出日志
     *
     * @param priority 级别
     * @param tag 标签
     * @param msg 消息
     */
    public static void println(int priority, String tag, String msg) {
        println(priority, tag, msg, null);
    }
}
