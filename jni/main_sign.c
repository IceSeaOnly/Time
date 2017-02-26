//
// Created by Administrator on 2016/9/23.
//
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "md5.h"
#include "site_binghai_number2_Utils_SignUtil.h"
char* combination(char* ori,char* add){
    int len1 = strlen(ori);
    int len2 = strlen(add);
    char* res = (char*)malloc(sizeof(char)*(len1+len2+10));

    int dig = 0, i;
    for(i = 0; i<len1+len2+10 && ori[i]; res[dig++] = ori[i++]);
    for(i = 0; i<len1+len2+10 && (res[dig++] = add[i]); ++i);
    return res;
}

JNIEXPORT jstring JNICALL Java_site_binghai_number2_Utils_SignUtil_getSign
  (JNIEnv *env, jclass obj, jstring strText){
	char* szText = (char*)(*env)->GetStringUTFChars(env, strText, 0);
	char* bp = "binghai_password_1041414957";
	szText = combination(szText,bp);
  	MD5_CTX context = { 0 };
  	MD5Init(&context);
  	MD5Update(&context, szText, strlen(szText));
  	unsigned char dest[16] = { 0 };
  	MD5Final(dest, &context);
  	(*env)->ReleaseStringUTFChars(env, strText, szText);

  	int i = 0;
  	char szMd5[32] = { 0 };
  	for (i = 0; i < 16; i++)
  	{
  		sprintf(szMd5, "%s%02x", szMd5, dest[i]);
  	}
	return (*env)->NewStringUTF(env, szMd5);
  }

  