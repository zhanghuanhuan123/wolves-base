 package com.wolves.wolf.base.db.conf;

 import com.alibaba.druid.pool.DruidDataSource;
 import java.io.FileReader;
 import java.io.Reader;
 import java.util.HashMap;
 import javax.xml.parsers.SAXParser;
 import javax.xml.parsers.SAXParserFactory;

 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXNotRecognizedException;
 import org.xml.sax.SAXNotSupportedException;
 import org.xml.sax.XMLReader;

 public class JAXPConfigurator
 {
   private static final Logger logger = LogManager.getLogger(JAXPConfigurator.class);
   private static final boolean NAMESPACE_AWARE = true;

   public static void configure(String xmlFileName, boolean validate, HashMap<String, DruidDataSource> poolMap)
     throws Exception
   {
     configure(new InputSource(new FileReader(xmlFileName)), validate, poolMap);
   }

   public static void configure(InputSource inputSource, boolean validate, HashMap<String, DruidDataSource> poolMap) throws Exception {
     SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
     saxParserFactory.setValidating(validate);
     SAXParser saxParser = saxParserFactory.newSAXParser();
     XMLReader xmlReader = saxParser.getXMLReader();
     XMLConfigurator xmlConfigurator = new XMLConfigurator(poolMap);
     xmlReader.setErrorHandler(xmlConfigurator);
     setSAXFeature(xmlReader, "http://xml.org/sax/features/namespaces", true);
     setSAXFeature(xmlReader, "http://xml.org/sax/features/namespace-prefixes", false);
     saxParser.parse(inputSource, xmlConfigurator);
   }

   public static void configure(Reader reader, boolean validate, HashMap<String, DruidDataSource> poolMap) throws Exception {
     configure(new InputSource(reader), validate, poolMap);
   }

   private static void setSAXFeature(XMLReader xmlReader, String feature, boolean state) {
     try {
       xmlReader.setFeature(feature, state);
     } catch (SAXNotRecognizedException e) {
       logger.warn("Feature: '" + feature + "' not recognised by xml reader " + xmlReader + ".", e);
     } catch (SAXNotSupportedException e) {
       logger.warn("Feature: '" + feature + "' not supported by xml reader " + xmlReader + ".", e);
     }
   }
 }
