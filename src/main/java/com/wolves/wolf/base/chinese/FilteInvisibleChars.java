 package com.wolves.wolf.base.chinese;

 import java.io.UnsupportedEncodingException;
 import java.util.HashSet;

 public class FilteInvisibleChars
 {
   private static final StringBuffer sb = new StringBuffer();
   private static final HashSet<Character> hashSet = new HashSet();

   private FilteInvisibleChars()
   {
     try
     {
       sb.append(GB2312Dic.getGB2312SymbolString());
       sb.append(GB2312Dic.getGB2312String());
       sb.append(GBKDic.getGBKString());
       sb.append(ASCIIDic.getASCIIString());
     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
     }
     char[] charArray = sb.toString().toCharArray();
     for (char aChar : charArray)
       hashSet.add(Character.valueOf(aChar));
   }

   public static String filteInvisibleChars(String content)
   {
     if (content == null) {
       return null;
     }
     StringBuffer sb = new StringBuffer(content.length());
     char[] temp = content.toCharArray();
     for (char aChar : temp) {
       if (hashSet.contains(Character.valueOf(aChar))) {
         sb.append(aChar);
       }
     }

     return sb.toString();
   }

   static
   {
     FilteInvisibleChars localFilteInvisibleChars = new FilteInvisibleChars();
   }
 }

