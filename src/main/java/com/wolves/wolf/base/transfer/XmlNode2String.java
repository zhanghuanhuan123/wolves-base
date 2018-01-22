 package com.wolves.wolf.base.transfer;

 import java.lang.reflect.Method;
 import org.w3c.dom.Attr;
 import org.w3c.dom.Document;
 import org.w3c.dom.DocumentType;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;

 public class XmlNode2String
 {
   protected boolean fCanonical;
   protected boolean fXML11;

   public XmlNode2String()
   {
   }

   public XmlNode2String(boolean canonical)
   {
     this.fCanonical = canonical;
   }

   public void setCanonical(boolean canonical) {
     this.fCanonical = canonical;
   }

   public String getXmlString(Node node) {
     StringBuffer sb = new StringBuffer();
     write(node, sb, false);
     return sb.toString();
   }

   public String getHtmlString(Node node) {
     StringBuffer sb = new StringBuffer();
     write(node, sb, true);
     return sb.toString();
   }

   private void write(Node node, StringBuffer sb, boolean htmlMode) {
     if (node == null)
       return;
     short type = node.getNodeType();
     switch (type)
     {
     case 9:
       Document document = (Document)node;
       this.fXML11 = "1.1".equals(getVersion(document));
       if (!this.fCanonical) {
         if (this.fXML11) {
           sb.append("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
           sb.append("\n");
         } else {
           sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
           sb.append("\n");
         }
         write(document.getDoctype(), sb, htmlMode);
       }
       write(document.getDocumentElement(), sb, htmlMode);
       break;
     case 10:
       DocumentType doctype = (DocumentType)node;
       sb.append("<!DOCTYPE ");
       sb.append(doctype.getName());
       String publicId = doctype.getPublicId();
       String systemId = doctype.getSystemId();
       if (publicId != null) {
         sb.append(" PUBLIC '");
         sb.append(publicId);
         sb.append("' '");
         sb.append(systemId);
         sb.append('\'');
       } else if (systemId != null) {
         sb.append(" SYSTEM '");
         sb.append(systemId);
         sb.append('\'');
       }
       String internalSubset = doctype.getInternalSubset();
       if (internalSubset != null) {
         sb.append(" [");
         sb.append("\n");
         sb.append(internalSubset);
         sb.append(']');
       }
       sb.append('>');
       sb.append("\n");
       break;
     case 1:
       sb.append('<');
       sb.append(node.getNodeName());
       Attr[] attrs = sortAttributes(node.getAttributes());
       Attr[] arr$ = attrs;
       int len$ = arr$.length;
       for (int i$ = 0; i$ < len$; i$++) {
         Attr attr = arr$[i$];
         sb.append(' ');
         sb.append(attr.getNodeName());
         sb.append("=\"");
         normalizeAndPrint(attr.getNodeValue(), true, sb, htmlMode);
         sb.append('"');
       }

       sb.append('>');
       for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
         write(child, sb, htmlMode);
       }
       break;
     case 11:
       sb.append('<');
       sb.append(node.getNodeName());
       Attr[] arr = sortAttributes(node.getAttributes());

       int len = arr.length;
       for (int i$ = 0; i$ < len; i$++) {
         Attr attr = arr[i$];
         sb.append(' ');
         sb.append(attr.getNodeName());
         sb.append("=\"");
         normalizeAndPrint(attr.getNodeValue(), true, sb, htmlMode);
         sb.append('"');
       }

       sb.append('>');
       for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
         write(child, sb, htmlMode);
       }
       break;
     case 5:
       if (this.fCanonical) {
         for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
           write(child, sb, htmlMode);
       }
       else {
         sb.append('&');
         sb.append(node.getNodeName());
         sb.append(';');
       }
       break;
     case 4:
       if (this.fCanonical) {
         normalizeAndPrint(node.getNodeValue(), false, sb, htmlMode);
       } else {
         sb.append("<![CDATA[");
         sb.append(node.getNodeValue());
         sb.append("]]>");
       }
       break;
     case 3:
       if ((htmlMode) && (node.getParentNode().getNodeName().equalsIgnoreCase("script")))
         normalizeAndPrint(node.getNodeValue(), false, sb, false);
       else
         normalizeAndPrint(node.getNodeValue(), false, sb, true);
       break;
     case 7:
       sb.append("<?");
       sb.append(node.getNodeName());
       String data = node.getNodeValue();
       if ((data != null) && (data.length() > 0)) {
         sb.append(' ');
         sb.append(data);
       }
       sb.append("?>");
       break;
     case 8:
       if (!this.fCanonical) {
         sb.append("<!--");
         String comment = node.getNodeValue();
         if ((comment != null) && (comment.length() > 0))
           sb.append(comment);
         sb.append("-->");
       }break;
     case 2:
     case 6:
     }
     if (type == 1) {
       sb.append("</");
       sb.append(node.getNodeName());
       sb.append('>');
     }
   }

   protected Attr[] sortAttributes(NamedNodeMap attrs) {
     int len = attrs == null ? 0 : attrs.getLength();
     Attr[] array = new Attr[len];
     for (int i = 0; i < len; i++) {
       if (attrs == null)
         throw new AssertionError();
       array[i] = ((Attr)attrs.item(i));
     }

     for (int i = 0; i < len - 1; i++) {
       String name = array[i].getNodeName();
       int index = i;
       for (int j = i + 1; j < len; j++) {
         String curName = array[j].getNodeName();
         if (curName.compareTo(name) < 0) {
           name = curName;
           index = j;
         }
       }

       if (index != i) {
         Attr temp = array[i];
         array[i] = array[index];
         array[index] = temp;
       }
     }

     return array;
   }

   protected void normalizeAndPrint(String s, boolean isAttValue, StringBuffer sb, boolean xmlEncode) {
     int len = s == null ? 0 : s.length();
     for (int i = 0; i < len; i++) {
       if (s == null)
         throw new AssertionError();
       char c = s.charAt(i);
       normalizeAndPrint(c, isAttValue, sb, xmlEncode);
     }
   }

   protected void normalizeAndPrint(char c, boolean isAttValue, StringBuffer sb, boolean xmlEncode)
   {
     switch (c) {
     case '<':
       if (xmlEncode)
         sb.append("&lt;");
       else
         sb.append("<");
       break;
     case '>':
       if (xmlEncode)
         sb.append("&gt;");
       else
         sb.append(">");
       break;
     case '&':
       if (xmlEncode)
         sb.append("&amp;");
       else
         sb.append("&");
       break;
     case '"':
       if (isAttValue)
         sb.append("&quot;");
       else
         sb.append("\"");
       break;
     case '\r':
       sb.append("&#xD;");
       break;
     case '\n':
       if (this.fCanonical)
         sb.append("&#xA;");
       break;
     }

     if (((this.fXML11) && (((c >= '\001') && (c <= '\037') && (c != '\t') && (c != '\n')) || ((c >= '') && (c <= '')) || (c == ' '))) || ((isAttValue) && ((c == '\t') || (c == '\n')))) {
       sb.append("&#x");
       sb.append(Integer.toHexString(c).toUpperCase());
       sb.append(";");
     } else {
       sb.append(c);
     }
   }

   protected String getVersion(Document document)
   {
     if (document == null)
       return null;
     String version = null;
     try {
       Method getXMLVersion = document.getClass().getMethod("getXmlVersion", new Class[0]);
       if (getXMLVersion != null)
         version = (String)getXMLVersion.invoke(document, (Object[])null);
     }
     catch (Exception localException) {
     }
     return version;
   }
 }
