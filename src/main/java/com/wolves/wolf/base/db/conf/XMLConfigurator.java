 package com.wolves.wolf.base.db.conf;

 import com.alibaba.druid.pool.DruidDataSource;
 import java.util.HashMap;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 import org.xml.sax.Attributes;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 import org.xml.sax.helpers.DefaultHandler;

 public class XMLConfigurator extends DefaultHandler
 {
   private static final Logger LOG = LogManager.getLogger(XMLConfigurator.class);

   private StringBuffer content = new StringBuffer();
   private String poolName;
   private HashMap<String, String> properties = new HashMap();
   private static final String DRUID = "druid";
   private static final String PROPERTY = "property";
   private static final String NAME = "name";
   private static final String VALUE = "value";
   private static final String ID = "id";
   private boolean insideDruid;
   private HashMap<String, DruidDataSource> poolMap;

   public XMLConfigurator(HashMap<String, DruidDataSource> poolMap)
   {
     this.poolMap = poolMap;
   }

   public void startElement(String uri, String lname, String qname, Attributes attributes) throws SAXException
   {
     this.content.setLength(0);
     if (!namespaceOk(uri)) {
       return;
     }
     String elementName = getElementName(uri, lname, qname);
     if (elementName.equals("druid")) {
       if (this.insideDruid) {
         throw new SAXException("A <druid> element can't contain another <druid> element.");
       }
       this.insideDruid = true;
       this.properties.clear();
       this.poolName = attributes.getValue("id");
       if (this.poolMap.get(this.poolName) != null) {
         throw new SAXException("a connection pool named " + this.poolName + " has already exists.");
       }
     }
     if ((this.insideDruid) &&
       (elementName.equals("property")))
       setDriverProperty(attributes);
   }

   public void characters(char[] chars, int start, int length)
     throws SAXException
   {
     if (this.insideDruid)
       this.content.append(chars, start, length);
   }

   public void endElement(String uri, String lname, String qname)
     throws SAXException
   {
     if (!namespaceOk(uri)) {
       return;
     }
     String elementName = getElementName(uri, lname, qname);
     if (elementName.equals("druid")) {
       try {
         DruidDataSource druidDataSource = new DruidDataSource();
         if ((StringUtils.isBlank((CharSequence)this.properties.get("url"))) ||
           (StringUtils.isBlank((CharSequence)this.properties
           .get("username"))) ||
           (StringUtils.isBlank((CharSequence)this.properties
           .get("password"))))
         {
           throw new SAXException("empty url/username/password para value");
         }

         druidDataSource.setUrl(((String)this.properties.get("url")).trim());
         druidDataSource.setUsername(((String)this.properties.get("username")).trim());
         druidDataSource.setPassword(((String)this.properties.get("password")).trim());

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("initialSize"))) {
           druidDataSource.setInitialSize(Integer.parseInt(((String)this.properties.get("initialSize")).trim()));
         }
         if (StringUtils.isNotBlank((CharSequence)this.properties.get("minIdle"))) {
           druidDataSource.setMinIdle(Integer.parseInt(((String)this.properties.get("minIdle")).trim()));
         }
         if (StringUtils.isNotBlank((CharSequence)this.properties.get("maxActive"))) {
           druidDataSource.setMaxActive(Integer.parseInt(((String)this.properties.get("maxActive")).trim()));
         }

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("maxWait"))) {
           druidDataSource.setMaxWait(Integer.parseInt(((String)this.properties.get("maxWait")).trim()));
         }

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("timeBetweenEvictionRunsMillis"))) {
           druidDataSource.setTimeBetweenEvictionRunsMillis(Integer.parseInt(((String)this.properties.get("timeBetweenEvictionRunsMillis")).trim()));
         }

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("timeBetweenLogStatsMillis"))) {
           druidDataSource.setTimeBetweenLogStatsMillis(Integer.parseInt(((String)this.properties.get("timeBetweenLogStatsMillis")).trim()));
         }

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("minEvictableIdleTimeMillis"))) {
           druidDataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(((String)this.properties.get("minEvictableIdleTimeMillis")).trim()));
         }

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("validationQuery"))) {
           druidDataSource.setValidationQuery(((String)this.properties.get("validationQuery")).trim());
         }
         if (StringUtils.isNotBlank((CharSequence)this.properties.get("testWhileIdle"))) {
           druidDataSource.setTestWhileIdle(Boolean.parseBoolean(((String)this.properties.get("testWhileIdle")).trim()));
         }
         if (StringUtils.isNotBlank((CharSequence)this.properties.get("testOnBorrow"))) {
           druidDataSource.setTestOnBorrow(Boolean.parseBoolean(((String)this.properties.get("testOnBorrow")).trim()));
         }
         if (StringUtils.isNotBlank((CharSequence)this.properties.get("testOnReturn"))) {
           druidDataSource.setTestOnReturn(Boolean.parseBoolean(((String)this.properties.get("testOnReturn")).trim()));
         }

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("poolPreparedStatements"))) {
           druidDataSource.setPoolPreparedStatements(Boolean.parseBoolean(((String)this.properties.get("poolPreparedStatements")).trim()));
         }
         if (StringUtils.isNotBlank((CharSequence)this.properties.get("maxPoolPreparedStatementPerConnectionSize"))) {
           druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(((String)this.properties.get("maxPoolPreparedStatementPerConnectionSize")).trim()));
         }

         if (StringUtils.isNotBlank((CharSequence)this.properties.get("filters"))) {
           druidDataSource.setFilters(((String)this.properties.get("filters")).trim());
         }
         druidDataSource.init();
         this.poolMap.put(this.poolName, druidDataSource);
       } catch (Exception e) {
         throw new SAXException(e);
       }
       this.insideDruid = false;
     }
   }

   private void setDriverProperty(Attributes attributes) throws SAXException {
     String name = attributes.getValue("name");
     String value = attributes.getValue("value");
     if ((name == null) || (name.length() < 1) || (value == null)) {
       throw new SAXException("Name or value attribute missing from property element.Name: '" + name + "' Value: '" + value + "'.");
     }

     this.properties.put(name, value);
   }

   public void warning(SAXParseException e) throws SAXException
   {
     LOG.debug("The saxparser reported a warning.", e);
   }

   public void error(SAXParseException e) throws SAXException
   {
     throw e;
   }

   public void fatalError(SAXParseException e) throws SAXException
   {
     throw e;
   }

   private String getElementName(String uri, String lname, String qname)
   {
     if ((uri == null) || ("".equals(uri))) {
       return qname;
     }
     return lname;
   }

   private boolean namespaceOk(String uri)
   {
     return (uri == null) || (uri.length() == 0);
   }
 }

