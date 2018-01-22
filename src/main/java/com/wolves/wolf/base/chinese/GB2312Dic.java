 package com.wolves.wolf.base.chinese;

 import java.io.UnsupportedEncodingException;

 public class GB2312Dic
 {
   private static final byte GB2312_PRE_BE = -80;
   private static final byte GB2312_PRE_ED = -9;
   private static final byte GB2312_SUF_BE = -95;
   private static final byte GB2312_SUF_ED = -2;
   private static final byte GB2312_PREEX = -41;
   private static final byte GB2312_SUFEX1 = -6;
   private static final byte GB2312_SUFEX2 = -5;
   private static final byte GB2312_SUFEX3 = -4;
   private static final byte GB2312_SUFEX4 = -3;
   private static final byte GB2312_SUFEX5 = -2;

   public static String getGB2312String()
     throws UnsupportedEncodingException
   {
     byte[] byteArray = new byte[13526];
     int counter = 0;
     byte i = 0;
     byte j = 0;

     for (i = -80; i <= -9; i = (byte)(i + 1)) {
       for (j = -95; j <= -2; j = (byte)(j + 1)) {
         if ((i != -41) || ((j != -6) && (j != -5) && (j != -4) && (j != -3) && (j != -2)))
         {
           byteArray[(counter++)] = i;
           byteArray[(counter++)] = j;
         }
       }
     }
     return new String(byteArray, "GB2312");
   }

   public static String getGB2312SymbolString() {
     return "，、。；：！？“”‘’《》～【】『』…￥％（）－—＋＝×÷　";
   }
 }

