 package com.wolves.wolf.base.db;

 import java.sql.SQLException;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;


 public class PagerQuery
 {
   private DataBaseStore dataBaseStore;
   private static Logger logger = LogManager.getLogger(PagerQuery.class);

   public PagerQuery(DataBaseStore dataBaseStore) {
     this.dataBaseStore = dataBaseStore;
   }

   public SimplePage executePagedSqlQuery(String sqlQuery, int currentPage, int recordsPerPage)
     throws SQLException
   {
     StringBuffer stringBuffer = new StringBuffer();
     SimplePage simplePage = new SimplePage();

     String sqlQueryCount = "select count(*) from (\n" + sqlQuery + "\n)";

     List listTemp = this.dataBaseStore.sqlQuery(sqlQueryCount);
     Object obj = ((HashMap)listTemp.get(0)).get("count(*)");
     simplePage.initialSize(Integer.parseInt(obj.toString()), recordsPerPage, currentPage);

     stringBuffer.append("select b_table.* from (\n");
     stringBuffer.append("    select a_table.*,rownum myrownum from (\n");
     stringBuffer.append(sqlQuery);
     stringBuffer.append("\n");
     stringBuffer.append("    ) a_table\n");
     stringBuffer.append(") b_table where b_table.myrownum <= ");
     stringBuffer.append(simplePage.getCurrentPage() * simplePage.getRecordsPerPage());
     stringBuffer.append(" and b_table.myrownum >= ");
     stringBuffer.append((simplePage.getCurrentPage() - 1) * simplePage.getRecordsPerPage() + 1);

     listTemp = this.dataBaseStore.sqlQuery(stringBuffer.toString());
     simplePage.setCurrentPageSize(listTemp.size());
     simplePage.setResults(listTemp);

     return simplePage;
   }

   public SimplePage executePagedSqlQuery(String sqlQuery, String sumQuery, int currentPage, int recordsPerPage)
     throws SQLException
   {
     StringBuffer stringBuffer = new StringBuffer();
     SimplePage simplePage = new SimplePage();

     List listTemp = this.dataBaseStore.sqlQuery(sumQuery);
     Collection collection = ((HashMap)listTemp.get(0)).values();
     Object object = null;
     for (Iterator localIterator = collection.iterator(); localIterator.hasNext(); ) { Object obj = localIterator.next();
       object = obj;
     }
     assert (object != null);
     simplePage.initialSize(Integer.parseInt(object.toString()), recordsPerPage, currentPage);

     stringBuffer.append("select b_table.* from (\n");
     stringBuffer.append("    select a_table.*,rownum myrownum from (\n");
     stringBuffer.append(sqlQuery);
     stringBuffer.append("\n");
     stringBuffer.append("    ) a_table\n");
     stringBuffer.append(") b_table where b_table.myrownum <= ");
     stringBuffer.append(simplePage.getCurrentPage() * simplePage.getRecordsPerPage());
     stringBuffer.append(" and b_table.myrownum >= ");
     stringBuffer.append((simplePage.getCurrentPage() - 1) * simplePage.getRecordsPerPage() + 1);

     listTemp = this.dataBaseStore.sqlQuery(stringBuffer.toString());
     simplePage.setCurrentPageSize(listTemp.size());
     simplePage.setResults(listTemp);

     return simplePage;
   }
 }
