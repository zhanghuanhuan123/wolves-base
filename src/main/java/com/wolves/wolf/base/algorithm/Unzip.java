 package com.wolves.wolf.base.algorithm;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipInputStream;
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.io.IOUtils;

 public class Unzip
 {
   public void unzip(String src, String dest)
     throws IOException
   {
     File srcFile = new File(src);
     ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(srcFile));
     ZipEntry zipEntry = null;
     while (true) {
       try {
         zipEntry = zipInputStream.getNextEntry();
         if (zipEntry == null)
           break;
         if (zipEntry.isDirectory()) {
           FileUtils.forceMkdir(new File(dest + File.separator + zipEntry.getName()));
         } else {
           FileOutputStream fileOutputStream = new FileOutputStream(new File(dest + File.separator + zipEntry.getName()));
           if (zipEntry.getSize() <= 2147483648L)
             IOUtils.copy(zipInputStream, fileOutputStream);
           else
             IOUtils.copyLarge(zipInputStream, fileOutputStream);
           IOUtils.closeQuietly(fileOutputStream);
         }
       }
       catch (Exception e) {
         try {
           zipInputStream.skip(1L);
           System.out.print(".");
         }
         catch (Exception e1) {
           e1.printStackTrace();
         }
         e.printStackTrace();
       }
     }
     IOUtils.closeQuietly(zipInputStream);
   }
 }

