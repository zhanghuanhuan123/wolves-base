 package com.wolves.wolf.base.xml;

 import java.util.ArrayList;
 import org.apache.commons.lang3.StringUtils;
 import org.w3c.dom.Element;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;

 public class XMLUtils
 {
   public static Element findTargetElement(Node node, String name, String attr, String attrValue)
   {
     if (node.getNodeType() != 1)
       return null;
     if ((node.getNodeName().equalsIgnoreCase(name)) && ((StringUtils.isBlank(attr)) || ((node.getAttributes() != null) && (node.getAttributes().getNamedItem(attr) != null))) && ((StringUtils.isBlank(attrValue)) || (node.getAttributes().getNamedItem(attr).getNodeValue().equals(attrValue))))
       return (Element)node;
     Element element = (Element)node;
     NodeList nodeList = element.getChildNodes();
     for (int i = 0; i < nodeList.getLength(); i++) {
       Element result = findTargetElement(nodeList.item(i), name, attr, attrValue);
       if (result != null) {
         return result;
       }
     }
     return null;
   }

   public static void findTargetElements(Node node, String name, String attr, String attrValue, ArrayList<Node> arrayList) {
     if (node.getNodeType() == 1)
       if ((node.getNodeName().equalsIgnoreCase(name)) && ((StringUtils.isBlank(attr)) || ((node.getAttributes() != null) && (node.getAttributes().getNamedItem(attr) != null))) && ((StringUtils.isBlank(attrValue)) || (node.getAttributes().getNamedItem(attr).getNodeValue().equals(attrValue)))) {
         arrayList.add(node);
       } else {
         Element element = (Element)node;
         NodeList nodeList = element.getChildNodes();
         for (int i = 0; i < nodeList.getLength(); i++)
           findTargetElements(nodeList.item(i), name, attr, attrValue, arrayList);
       }
   }

   public static String ripText(Node node, StringBuffer sb)
   {
     if (sb == null)
       sb = new StringBuffer();
     if (node.getNodeType() != 1) {
       if ((node.getNodeType() == 3) && (node.getNodeValue().trim().length() != 0))
         sb.append(node.getNodeValue().trim());
     } else {
       Element element = (Element)node;
       NodeList nodeList = element.getChildNodes();
       for (int i = 0; i < nodeList.getLength(); i++) {
         ripText(nodeList.item(i), sb);
       }
     }
     return sb.toString();
   }

   public static String ripTextWithFormat(Node node, StringBuffer sb) {
     if (sb == null)
       sb = new StringBuffer();
     if (node.getNodeType() != 1) {
       if ((node.getNodeType() == 3) && (node.getNodeValue().trim().length() != 0))
         sb.append(node.getNodeValue().trim());
     } else {
       Element element = (Element)node;
       if ((element.getNodeName().equalsIgnoreCase("br")) || (element.getNodeName().equalsIgnoreCase("tr")))
         sb.append("\n");
       NodeList nodeList = element.getChildNodes();
       for (int i = 0; i < nodeList.getLength(); i++) {
         ripTextWithFormat(nodeList.item(i), sb);
       }
     }
     return sb.toString();
   }

   public static ArrayList ripPicData(Node node, ArrayList<String> arrayList) {
     if (arrayList == null)
       arrayList = new ArrayList();
     if ((node.getNodeName().equalsIgnoreCase("IMG")) && (node.getAttributes().getNamedItem("src") != null)) {
       arrayList.add(node.getAttributes().getNamedItem("src").getNodeValue());
     } else if (node.getNodeType() == 1) {
       Element element = (Element)node;
       NodeList nodeList = element.getChildNodes();
       for (int i = 0; i < nodeList.getLength(); i++) {
         ripPicData(nodeList.item(i), arrayList);
       }
     }
     return arrayList;
   }
 }
