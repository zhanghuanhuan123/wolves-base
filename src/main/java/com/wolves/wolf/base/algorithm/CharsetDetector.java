 package com.wolves.wolf.base.algorithm;

 import java.io.IOException;
 import java.io.InputStream;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.mozilla.intl.chardet.nsDetector;
 import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

 public class CharsetDetector
 {
   private static final int CHUNK_SIZE = 2000;
   private static final Pattern METAPATTERN = Pattern.compile("<meta\\s+([^>]*http-equiv=\"?content-type\"?[^>]*)>", 2);
   private static final Pattern CHARSETPATTERN = Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)", 2);
   private String encodingType;

   public CharsetDetector()
   {
     this.encodingType = "";
   }

   public String analyse(byte[] byteArray) {
     this.encodingType = sniffCharacterEncoding(byteArray);
     if (!StringUtils.isBlank(this.encodingType)) {
       return this.encodingType;
     }
     int lang = 6;
     nsDetector det = new nsDetector(lang);
     det.Init(new nsICharsetDetectionObserver()
     {
       public void Notify(String charset) {
         org.mozilla.intl.chardet.HtmlCharsetDetector.found = true;
         CharsetDetector.this.encodingType = charset;
       }
     });
     boolean isAscii = det.isAscii(byteArray, byteArray.length);
     if (!isAscii)
       det.DoIt(byteArray, byteArray.length, false);
     det.DataEnd();
     if (isAscii) {
       this.encodingType = "ASCII";
     }
     if (StringUtils.isBlank(this.encodingType)) {
       this.encodingType = "ISO8859-1";
     }
     String result = this.encodingType;
     this.encodingType = "";
     return result;
   }

   public String analyse(InputStream inputStream) throws IOException
   {
     byte[] byteArray = IOUtils.toByteArray(inputStream);
     IOUtils.closeQuietly(inputStream);
     return analyse(byteArray);
   }

   private static String sniffCharacterEncoding(byte[] content) {
     int length = content.length >= CHUNK_SIZE ? CHUNK_SIZE : content.length;
     String str = new String(content, 0, length);
     Matcher metaMatcher = METAPATTERN.matcher(str);
     String encoding = null;
     if (metaMatcher.find()) {
       Matcher charsetMatcher = CHARSETPATTERN.matcher(metaMatcher.group(1));
       if (charsetMatcher.find()) {
         encoding = charsetMatcher.group(1);
       }
     }
     return encoding;
   }
 }

