 package com.wolves.wolf.base.chinese;

 import org.apache.commons.lang3.CharUtils;
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.commons.lang3.StringUtils;

 public class UnicodeEnDecoder
 {
   public static String decodeUnicode(String input)
     throws Exception
   {
     StringBuilder sb = new StringBuilder();
     for (String aChar : StringUtils.split(input, "\\u")) {
       if (aChar.length() == 2)
         sb.append("\\u00").append(aChar);
       else {
         sb.append("\\u").append(aChar);
       }
     }
     return StringEscapeUtils.unescapeJava(sb.toString());
   }

   public static String encodeUnicode(String input) throws Exception {
     StringBuilder sb = new StringBuilder();
     for (char aChar : input.toCharArray()) {
       sb.append(CharUtils.unicodeEscaped(aChar));
     }
     return sb.toString();
   }
 }

