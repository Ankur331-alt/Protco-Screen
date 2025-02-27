/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smart.rinoiot.center.update;

import android.content.Context;
import android.content.Intent;


import java.io.File;

/**
 * APK安装工具类
 *
 * @author xuexiang
 * @since 2018/7/2 上午1:18
 */
public final class ApkInstallUtils {

    /**
     * 安装 文件（APK）
     */
    public static void openInstallFile(String path, Context mContext) {
        Intent intents = new Intent();
        intents.setAction(Intent.ACTION_VIEW);
        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intents, "application/vnd.android.package-archive", new File(path), false);
        mContext.startActivity(intents);
    }
}
