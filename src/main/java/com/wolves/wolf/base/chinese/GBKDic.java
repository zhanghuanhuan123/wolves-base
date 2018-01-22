 package com.wolves.wolf.base.chinese;

 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;

 public class GBKDic
 {
   public static final byte GBK3_PRE_BE = -127;
   public static final byte GBK3_PRE_ED = -96;
   public static final byte GBK3_SUF_BE = 64;
   public static final byte GBK3_SUF_ED = -2;
   public static final byte GBK4_PRE_BE = -86;
   public static final byte GBK4_PRE_ED = -2;
   public static final byte GBK4_SUF_BE = 64;
   public static final byte GBK4_SUF_ED = -96;

   public static String getGBKString()
     throws UnsupportedEncodingException
   {
     byte[] byteArray = new byte[28480];
     int counter = 0;
     int i = 0;
     int j = 0;
     StringBuffer sb = new StringBuffer(14240);

     for (i = -127; i <= -96; i++) {
       for (j = 64; j < 127; j++) {
         byteArray[(counter++)] = ((byte)i);
         byteArray[(counter++)] = ((byte)j);
       }

     }

     for (i = -127; i <= -96; i++) {
       for (j = -128; j <= -2; j++) {
         byteArray[(counter++)] = ((byte)i);
         byteArray[(counter++)] = ((byte)j);
       }

     }

     for (i = -86; i <= -2; i++) {
       for (j = 64; j < 127; j++) {
         byteArray[(counter++)] = ((byte)i);
         byteArray[(counter++)] = ((byte)j);
       }

     }

     for (i = -86; i <= -2; i++) {
       for (j = -128; j <= -96; j++) {
         byteArray[(counter++)] = ((byte)i);
         byteArray[(counter++)] = ((byte)j);
       }

     }

     sb.append(GB2312Dic.getGB2312String());
     sb.append(new String(byteArray, "GBK"));
     return sb.toString();
   }

   public static void main(String[] args) throws UnsupportedEncodingException {
     System.out.println(getGBKString());
   }
 }

