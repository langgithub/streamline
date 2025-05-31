package com.lang.streamline.utils

import android.util.Log
import kotlin.concurrent.thread

class Command {
    companion object {
        @JvmStatic
        fun call(cmd: String) {

            val env = "env _=/system/bin/env ANDROID_DATA=/data ANDROID_ART_ROOT=/apex/com.android.art HOME=/ ANDROID_TZDATA_ROOT=/apex/com.android.tzdata SYSTEMSERVERCLASSPATH=/system/framework/com.android.location.provider.jar:/system/framework/services.jar:/system_ext/framework/miui-services.jar:/system_ext/framework/miui.services.jar:/apex/com.android.adservices/javalib/service-adservices.jar:/apex/com.android.adservices/javalib/service-sdksandbox.jar:/apex/com.android.appsearch/javalib/service-appsearch.jar:/apex/com.android.art/javalib/service-art.jar:/apex/com.android.media/javalib/service-media-s.jar:/apex/com.android.permission/javalib/service-permission.jar TERM=xterm-256color ANDROID_SOCKET_adbd=27 ANDROID_STORAGE=/storage EXTERNAL_STORAGE=/sdcard DOWNLOAD_CACHE=/data/cache LOGNAME=shell ANDROID_ASSETS=/system/app STANDALONE_SYSTEMSERVER_JARS=/apex/com.android.btservices/javalib/service-bluetooth.jar:/apex/com.android.os.statsd/javalib/service-statsd.jar:/apex/com.android.scheduling/javalib/service-scheduling.jar:/apex/com.android.tethering/javalib/service-connectivity.jar:/apex/com.android.uwb/javalib/service-uwb.jar:/apex/com.android.wifi/javalib/service-wifi.jar DEX2OATBOOTCLASSPATH=/apex/com.android.art/javalib/core-oj.jar:/apex/com.android.art/javalib/core-libart.jar:/apex/com.android.art/javalib/okhttp.jar:/apex/com.android.art/javalib/bouncycastle.jar:/apex/com.android.art/javalib/apache-xml.jar:/system/framework/framework.jar:/system/framework/framework-graphics.jar:/system/framework/ext.jar:/system/framework/telephony-common.jar:/system/framework/voip-common.jar:/system/framework/ims-common.jar:/system/framework/mediatek-telephony-base.jar:/system/framework/mediatek-telephony-common.jar:/system/framework/mediatek-carrier-config-manager.jar:/system/framework/mediatek-common.jar:/system/framework/mediatek-framework.jar:/system/framework/mediatek-ims-common.jar:/system/framework/mediatek-ims-base.jar:/system/framework/mediatek-telecom-common.jar:/system_ext/framework/miui-framework.jar:/system_ext/framework/miui-telephony-common.jar:/apex/com.android.i18n/javalib/core-icu4j.jar BOOTCLASSPATH=/apex/com.android.art/javalib/core-oj.jar:/apex/com.android.art/javalib/core-libart.jar:/apex/com.android.art/javalib/okhttp.jar:/apex/com.android.art/javalib/bouncycastle.jar:/apex/com.android.art/javalib/apache-xml.jar:/system/framework/framework.jar:/system/framework/framework-graphics.jar:/system/framework/ext.jar:/system/framework/telephony-common.jar:/system/framework/voip-common.jar:/system/framework/ims-common.jar:/system/framework/mediatek-telephony-base.jar:/system/framework/mediatek-telephony-common.jar:/system/framework/mediatek-carrier-config-manager.jar:/system/framework/mediatek-common.jar:/system/framework/mediatek-framework.jar:/system/framework/mediatek-ims-common.jar:/system/framework/mediatek-ims-base.jar:/system/framework/mediatek-telecom-common.jar:/system_ext/framework/miui-framework.jar:/system_ext/framework/miui-telephony-common.jar:/apex/com.android.i18n/javalib/core-icu4j.jar SHELL=/bin/sh ANDROID_BOOTLOGO=1 ASEC_MOUNTPOINT=/mnt/asec HOSTNAME=air USER=shell TMPDIR=/data/local/tmp PATH=/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/odm/bin:/vendor/bin:/vendor/xbin ANDROID_ROOT=/system ANDROID_I18N_ROOT=/apex/com.android.i18n $cmd\n"
            // 启动一个 shell 进程
            val process = Runtime.getRuntime().exec("sh")

            thread(start = true) {
                process.inputStream.bufferedReader().forEachLine { line ->
                    println("Output: $line")
                    Log.e("MyActivity", line)
                }
            }

            // 创建线程持续读取错误输出
            thread(start = true) {
                process.errorStream.bufferedReader().forEachLine { line ->
                    println("Error: $line")
                    Log.e("MyActivity", line)
                }
            }

            // 向 shell 写入命令，并发送 exit 命令以退出 shell
            process.outputStream.use { os ->
                os.write(env.toByteArray())
                os.write("\nexit".toByteArray())
                os.flush()
            }

            // 读取标准输出内容
//            val output = process.inputStream.bufferedReader().use { it.readText() }
//            // 读取错误输出内容
//            val errorOutput = process.errorStream.bufferedReader().use { it.readText() }

            // 等待进程结束（可选）
            process.waitFor()

//             打印结果（或根据需要进一步处理）
//            println("Output:\n$output")
//            println("Error Output:\n$errorOutput")

        }


        @JvmStatic
        fun callAsync(cmd: String) {
            val env = "env _=/system/bin/env ANDROID_DATA=/data ANDROID_ART_ROOT=/apex/com.android.art HOME=/ ANDROID_TZDATA_ROOT=/apex/com.android.tzdata SYSTEMSERVERCLASSPATH=/system/framework/com.android.location.provider.jar:/system/framework/services.jar:/system_ext/framework/miui-services.jar:/system_ext/framework/miui.services.jar:/apex/com.android.adservices/javalib/service-adservices.jar:/apex/com.android.adservices/javalib/service-sdksandbox.jar:/apex/com.android.appsearch/javalib/service-appsearch.jar:/apex/com.android.art/javalib/service-art.jar:/apex/com.android.media/javalib/service-media-s.jar:/apex/com.android.permission/javalib/service-permission.jar TERM=xterm-256color ANDROID_SOCKET_adbd=27 ANDROID_STORAGE=/storage EXTERNAL_STORAGE=/sdcard DOWNLOAD_CACHE=/data/cache LOGNAME=shell ANDROID_ASSETS=/system/app STANDALONE_SYSTEMSERVER_JARS=/apex/com.android.btservices/javalib/service-bluetooth.jar:/apex/com.android.os.statsd/javalib/service-statsd.jar:/apex/com.android.scheduling/javalib/service-scheduling.jar:/apex/com.android.tethering/javalib/service-connectivity.jar:/apex/com.android.uwb/javalib/service-uwb.jar:/apex/com.android.wifi/javalib/service-wifi.jar DEX2OATBOOTCLASSPATH=/apex/com.android.art/javalib/core-oj.jar:/apex/com.android.art/javalib/core-libart.jar:/apex/com.android.art/javalib/okhttp.jar:/apex/com.android.art/javalib/bouncycastle.jar:/apex/com.android.art/javalib/apache-xml.jar:/system/framework/framework.jar:/system/framework/framework-graphics.jar:/system/framework/ext.jar:/system/framework/telephony-common.jar:/system/framework/voip-common.jar:/system/framework/ims-common.jar:/system/framework/mediatek-telephony-base.jar:/system/framework/mediatek-telephony-common.jar:/system/framework/mediatek-carrier-config-manager.jar:/system/framework/mediatek-common.jar:/system/framework/mediatek-framework.jar:/system/framework/mediatek-ims-common.jar:/system/framework/mediatek-ims-base.jar:/system/framework/mediatek-telecom-common.jar:/system_ext/framework/miui-framework.jar:/system_ext/framework/miui-telephony-common.jar:/apex/com.android.i18n/javalib/core-icu4j.jar BOOTCLASSPATH=/apex/com.android.art/javalib/core-oj.jar:/apex/com.android.art/javalib/core-libart.jar:/apex/com.android.art/javalib/okhttp.jar:/apex/com.android.art/javalib/bouncycastle.jar:/apex/com.android.art/javalib/apache-xml.jar:/system/framework/framework.jar:/system/framework/framework-graphics.jar:/system/framework/ext.jar:/system/framework/telephony-common.jar:/system/framework/voip-common.jar:/system/framework/ims-common.jar:/system/framework/mediatek-telephony-base.jar:/system/framework/mediatek-telephony-common.jar:/system/framework/mediatek-carrier-config-manager.jar:/system/framework/mediatek-common.jar:/system/framework/mediatek-framework.jar:/system/framework/mediatek-ims-common.jar:/system/framework/mediatek-ims-base.jar:/system/framework/mediatek-telecom-common.jar:/system_ext/framework/miui-framework.jar:/system_ext/framework/miui-telephony-common.jar:/apex/com.android.i18n/javalib/core-icu4j.jar SHELL=/bin/sh ANDROID_BOOTLOGO=1 ASEC_MOUNTPOINT=/mnt/asec HOSTNAME=air USER=shell TMPDIR=/data/local/tmp PATH=/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/odm/bin:/vendor/bin:/vendor/xbin ANDROID_ROOT=/system ANDROID_I18N_ROOT=/apex/com.android.i18n $cmd\n"
            thread(start = true) {
                // 启动一个 shell 进程
                val process = Runtime.getRuntime().exec("sh")

                // 创建线程持续读取标准输出
                thread(start = true) {
                    var lastLogTime = 0L
                    process.inputStream.bufferedReader().forEachLine { line ->
//                        MainActivity.sendLog(line)
                        println("Output: $line")
                        if(lastLogTime == 0L){
                        }
                        val currentTime = System.currentTimeMillis()
                        // 如果已经过去 60000 毫秒（1分钟），则打印日志，并更新 lastLogTime
                        if (currentTime - lastLogTime >= 60_000L) {
                            println("Output: $line")
                            lastLogTime = currentTime
                        }
                    }
                }

                // 创建线程持续读取错误输出
                thread(start = true) {
                    process.errorStream.bufferedReader().forEachLine { line ->
                        println("Error: $line")
                    }
                }

                // 向 shell 写入命令，不发送 exit 命令以保持进程持续运行
                process.outputStream.use { os ->
                    os.write(env.toByteArray())
                    os.flush()
                }

                // 根据实际情况决定是否等待进程结束
                // 如果是持续运行的程序，此处可能会一直阻塞
                process.waitFor()
            }
        }

        @JvmStatic
        fun callNormal(cmd: String) {
            // 启动一个 shell 进程
            val process = Runtime.getRuntime().exec("sh")

            thread(start = true) {
                process.inputStream.bufferedReader().forEachLine { line ->
                    println("Output: $line")
                }
            }

            // 创建线程持续读取错误输出
            thread(start = true) {
                process.errorStream.bufferedReader().forEachLine { line ->
                    println("Error: $line")
                }
            }

            // 向 shell 写入命令，并发送 exit 命令以退出 shell
            process.outputStream.use { os ->
                os.write(cmd.toByteArray())
                os.write("\nexit".toByteArray())
                os.flush()
            }
            // 等待进程结束（可选）
            process.waitFor()
        }
    }
}
