 package com.wolves.wolf.base.db;

 import java.math.BigDecimal;
 import java.sql.Connection;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 public class DataBaseStore
 {
   private static Logger logger = LogManager.getLogger(DataBaseStore.class);
   private Connection conn;
   private Statement stmt;
   private ResultSet rset;

   public DataBaseStore(Connection conn)
     throws SQLException
   {
     this.conn = conn;
   }

   public List<HashMap<String, Object>> sqlQuery(String sql)
     throws SQLException
   {
     if (sql == null) {
       return null;
     }

     List list = null;
     try {
       this.stmt = this.conn.createStatement();
       this.rset = this.stmt.executeQuery(sql);
       list = getHashResults(this.rset);
     } finally {
       try {
         this.rset.close();
       } catch (Exception e) {
         logger.error(sql, e);
       }
       try {
         this.stmt.close();
       } catch (Exception e) {
         logger.error(sql, e);
       }
     }
     return list;
   }

   public int update(String sql)
     throws SQLException
   {
     SQLException er = null;
     int ret = 0;
     if (sql == null)
       return -1;
     try
     {
       this.stmt = this.conn.createStatement();
       ret = this.stmt.executeUpdate(sql);
     } catch (SQLException e) {
       er = e;
     } finally {
       try {
         this.stmt.close();
       } catch (Exception e) {
         logger.error(sql, e);
       }
     }
     if (er != null) {
       throw er;
     }
     return ret;
   }

   public void commitAndClose()
   {
     try
     {
       this.conn.commit();
     } catch (SQLException e) {
       logger.error(e);
     }
     try {
       this.conn.close();
     } catch (SQLException e) {
       logger.error(e);
     }
   }

   public void close()
   {
     try
     {
       this.conn.close();
     } catch (SQLException e) {
       logger.error(e);
     }
   }

   public Connection getConn()
   {
     return this.conn;
   }

   public void rollbackAndClose()
   {
     try
     {
       this.conn.rollback();
     } catch (SQLException e) {
       logger.error(e);
     }
     try {
       this.conn.close();
     } catch (SQLException e) {
       logger.error(e);
     }
   }

   private List<HashMap<String, Object>> getHashResults(ResultSet resultsSet)
     throws SQLException
   {
     ArrayList arraylist = new ArrayList();
     ResultSetMetaData resultSetMetaData = resultsSet.getMetaData();
     int columnCount = resultSetMetaData.getColumnCount();
     HashMap hashmap;
     for (; resultsSet.next(); arraylist.add(hashmap)) {
       hashmap = new HashMap(columnCount);
       for (int i = 1; i <= columnCount; i++) {
         Object obj = resultsSet.getObject(i);
         if ((obj != null) && ((obj instanceof BigDecimal))) {
           BigDecimal bigdecimal = (BigDecimal)obj;
           if (resultSetMetaData.getScale(i) == 0) {
             if (bigdecimal.toString().length() < 3)
               obj = Byte.valueOf(bigdecimal.byteValue());
             else if (bigdecimal.toString().length() < 5)
               obj = Short.valueOf(bigdecimal.shortValue());
             else if (bigdecimal.toString().length() < 10)
               obj = Integer.valueOf(bigdecimal.intValue());
             else if (bigdecimal.toString().length() < 19) {
               obj = Long.valueOf(bigdecimal.longValue());
             }
           }
         }
         hashmap.put(resultSetMetaData.getColumnLabel(i).toLowerCase(), obj);
       }
     }
     return arraylist;
   }
 }
