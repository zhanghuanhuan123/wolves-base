 package com.wolves.wolf.base.algorithm;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.io.IOUtils;
 import org.apache.tools.tar.TarEntry;
 import org.apache.tools.tar.TarInputStream;

 public class Untar
 {
   public void untar(String src, String dest)
     throws IOException
   {
     File srcFile = new File(src);
     TarInputStream tarInputStream = new TarInputStream(new FileInputStream(srcFile));
     TarEntry tarEntry = null;
     while (true) {
       try {
         tarEntry = tarInputStream.getNextEntry();
         if (tarEntry == null)
           break;
         if (tarEntry.isDirectory()) {
           FileUtils.forceMkdir(new File(dest + File.separator + tarEntry.getName()));
         } else {
           FileOutputStream fileOutputStream = new FileOutputStream(new File(dest + File.separator + tarEntry.getName()));
           if (tarEntry.getSize() <= 2147483648L)
             IOUtils.copy(tarInputStream, fileOutputStream);
           else
             IOUtils.copyLarge(tarInputStream, fileOutputStream);
           IOUtils.closeQuietly(fileOutputStream);
         }
       } catch (Exception e) {
         try {
           tarInputStream.skip(1L);
           System.out.print(".");
         }
         catch (Exception e1) {
           e1.printStackTrace();
         }
         e.printStackTrace();
       }
     }
     IOUtils.closeQuietly(tarInputStream);
   }
 }

