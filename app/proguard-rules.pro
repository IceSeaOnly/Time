-keepclasseswithmembernames class ** {
    native <methods>;
}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**
-keep class anet.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-printmapping  upload.map

-keepattributes Signature,InnerClasses


-keep class com.tencent.upload.network.base.ConnectionImpl
-keep class com.tencent.upload.network.base.ConnectionImpl {
    *;
}

-keep class com.tencent.upload.UploadManager { *; }
-keep class com.tencent.upload.UploadManager$* { *; }

-keep class com.tencent.upload.Const {
    *;
}
-keep class com.tencent.upload.Const$* { *; }

-keep class com.tencent.upload.task.** { *;}
-keep class com.tencent.upload.impl.** { * ; }
-keep class com.tencent.upload.utils.** { * ; }

-keepclasseswithmembers class com.tencent.upload.task.** { *; }
-keepclasseswithmembernames class com.tencent.upload.task.** { *; }

-keep class com.tencent.upload.task.ITask$* { *; }
-keep class com.tencent.upload.task.impl.FileDeleteTask$* { *; }
-keep class com.tencent.upload.task.impl.FileStatTask$* { *; }
-keep class com.tencent.upload.task.impl.FileCopyTask$* { *; }

-keep class com.tencent.upload.common.Global { *; }
-keep class com.tencent.upload.log.trace.TracerConfig { *; }

-keep class * extends com.qq.taf.jce.JceStruct { *; }

-printmapping  upload.map

-keepattributes Signature,InnerClasses


-keep class com.tencent.upload.network.base.ConnectionImpl
-keep class com.tencent.upload.network.base.ConnectionImpl {
    *;
}

-keep class com.tencent.upload.UploadManager { *; }
-keep class com.tencent.upload.UploadManager$* { *; }

-keep class com.tencent.upload.Const {
    *;
}
-keep class com.tencent.upload.Const$* { *; }

-keep class com.tencent.upload.task.** { *;}
-keep class com.tencent.upload.impl.** { * ; }
-keep class com.tencent.upload.utils.** { * ; }

-keepclasseswithmembers class com.tencent.upload.task.** { *; }
-keepclasseswithmembernames class com.tencent.upload.task.** { *; }

-keep class com.tencent.upload.task.ITask$* { *; }
-keep class com.tencent.upload.task.impl.FileDeleteTask$* { *; }
-keep class com.tencent.upload.task.impl.FileStatTask$* { *; }
-keep class com.tencent.upload.task.impl.FileCopyTask$* { *; }

-keep class com.tencent.upload.common.Global { *; }
-keep class com.tencent.upload.log.trace.TracerConfig { *; }

-keep class * extends com.qq.taf.jce.JceStruct { *; }
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}