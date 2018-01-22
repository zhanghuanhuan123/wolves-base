 package com.wolves.wolf.base.transfer;

 import com.wolves.wolf.base.algorithm.CharsetDetector;
 import java.io.ByteArrayInputStream;
 import java.io.PrintStream;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.xerces.parsers.DOMParser;
 import org.cyberneko.html.HTMLConfiguration;
 import org.w3c.dom.Document;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.xml.sax.InputSource;

 public class Html2Xml
 {
   private static final int CHUNK_SIZE = 2000;
   private static final Pattern METAPATTERN = Pattern.compile("<meta\\s+([^>]*http-equiv=\"?content-type\"?[^>]*)>", 2);
   private static final Pattern CHARSETPATTERN = Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)", 2);
   private static final String DEFAULT_CHAR_ENCODING = "UTF-8";
   private static final XmlNode2String XML_NODE_2_STRING = new XmlNode2String(false);

   public Node transfer2Node(byte[] content, String encoding)
     throws Exception
   {
     if (StringUtils.isBlank(encoding)) {
       encoding = sniffCharacterEncoding(content);
     }
     if (StringUtils.isBlank(encoding)) {
       encoding = new CharsetDetector().analyse(content);
     }
     if (StringUtils.isBlank(encoding)) {
       encoding = DEFAULT_CHAR_ENCODING;
     }
     String tempString = new String(content, encoding).trim();
     if ((!tempString.startsWith("<")) || (!tempString.endsWith(">")))
       tempString = "<html><body>" + tempString + "</body></html>";
     tempString = tempString.replaceAll("&nbsp;", " ");
     InputSource input = new InputSource(new ByteArrayInputStream(tempString.getBytes(encoding)));
     input.setEncoding(encoding);
     return transfer(input);
   }

   public String transfer2String(byte[] content, String encoding) throws Exception
   {
     Node node = transfer2Node(content, encoding);
     return XML_NODE_2_STRING.getXmlString(node);
   }

   private Node transfer(InputSource input) throws Exception
   {
     HTMLConfiguration configuration = new HTMLConfiguration();
     configuration.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
     configuration.setProperty("http://cyberneko.org/html/properties/names/attrs", "no-change");
     DOMParser parser = new DOMParser(configuration);
     parser.setFeature("http://apache.org/xml/features/include-comments", true);
     parser.setFeature("http://xml.org/sax/features/namespaces", false);
     parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", false);

     parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);

     parser.setFeature("http://cyberneko.org/html/features/report-errors", false);
     parser.setFeature("http://cyberneko.org/html/features/augmentations", false);
     parser.parse(input);
     Document doc = parser.getDocument();
     Node node = locationHtmlRoot(doc);
     if (node != null) {
       return node;
     }
     return doc;
   }

   private Node locationHtmlRoot(Node aNode)
   {
     if ((aNode.getNodeName().equalsIgnoreCase("html")) && (aNode.getChildNodes().getLength() != 0))
       return aNode;
     NodeList nodeList = aNode.getChildNodes();
     for (int i = 0; i < nodeList.getLength(); i++) {
       Node nodeReturn = locationHtmlRoot(nodeList.item(i));
       if (nodeReturn != null) {
         return nodeReturn;
       }
     }

     return null;
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

   private static void print(Node node, String indent) {
     System.out.println(indent + node.getClass().getName());
     for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
       print(child, indent + " ");
   }
 }

