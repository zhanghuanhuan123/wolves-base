 package com.wolves.wolf.base.db;

 import com.alibaba.druid.pool.DruidDataSource;
 import com.wolves.wolf.base.db.conf.JAXPConfigurator;
 import java.net.URL;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.concurrent.atomic.AtomicBoolean;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 import org.xml.sax.InputSource;

 public class DataBaseStoreManager
 {
   private static final Logger logger = LogManager.getLogger(DataBaseStoreManager.class);
   private static final AtomicBoolean IS_INITIAL = new AtomicBoolean(false);
   private static final HashMap<String, DruidDataSource> DRUID_DATA_SOURCE_HASH_MAP = new HashMap();

   public static synchronized void setupDriver(String xmlName) throws Exception {
     if (StringUtils.isBlank(xmlName)) {
       throw new RuntimeException("blank file name...");
     }
     if (!IS_INITIAL.get())
       setupDriver(new InputSource(getResource(xmlName).openStream()));
   }

   private static synchronized void setupDriver(InputSource inputSource) throws Exception {
     if (IS_INITIAL.get()) {
       logger.warn("数据库连接池已初始化完成，本次操作强行终止！");
     }
     logger.info("初始数据库连接池 start.....");
     long start = System.currentTimeMillis();
     JAXPConfigurator.configure(inputSource, false, DRUID_DATA_SOURCE_HASH_MAP);
     IS_INITIAL.set(true);
     logger.info("初始数据库连接池 end.....used " + (System.currentTimeMillis() - start) + " ms");
   }

   private static URL getResource(String resource) {
     if (StringUtils.isBlank(resource)) {
       throw new RuntimeException("empty resource.");
     }
     ClassLoader classLoader = DataBaseStoreManager.class.getClassLoader();
     if (classLoader != null) {
       URL url = classLoader.getResource(resource);
       if (url != null) {
         return url;
       }
     }
     return ClassLoader.getSystemResource(resource);
   }

   public static DataBaseStore createDataBaseStore(String poolName)
     throws SQLException
   {
     if (StringUtils.isBlank(poolName)) {
       throw new RuntimeException("empty poolName");
     }
     return new DataBaseStore(((DruidDataSource)DRUID_DATA_SOURCE_HASH_MAP.get(poolName)).getConnection());
   }
 }
