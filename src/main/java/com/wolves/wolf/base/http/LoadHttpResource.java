 package com.wolves.wolf.base.http;

 import com.wolves.wolf.base.algorithm.CharsetDetector;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.apache.commons.collections4.map.ListOrderedMap;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.http.HttpHost;
 import org.apache.http.StatusLine;
 import org.apache.http.client.config.RequestConfig;
 import org.apache.http.client.config.RequestConfig.Builder;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.CloseableHttpResponse;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.entity.ByteArrayEntity;
 import org.apache.http.impl.client.CloseableHttpClient;
 import org.apache.http.impl.client.HttpClientBuilder;
 import org.apache.http.impl.client.HttpClients;
 import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
 import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
 import org.apache.http.message.BasicNameValuePair;
 import org.apache.http.util.EntityUtils;

 public class LoadHttpResource
 {
   private static final String DEFAULT_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
   private CloseableHttpClient httpClient;

   public LoadHttpResource()
   {
     this(-1);
   }

   public LoadHttpResource(int timeoutInSec) {
     this(null, timeoutInSec);
   }

   public LoadHttpResource(String ipAndPort) {
     this(ipAndPort, -1);
   }

   public LoadHttpResource(String ipAndPort, int timeoutInSec) {
     PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

     cm.setMaxTotal(200);

     cm.setDefaultMaxPerRoute(20);

     RequestConfig requestConfig = RequestConfig.custom()
       .setConnectTimeout(timeoutInSec * 1000)
       .setConnectionRequestTimeout(timeoutInSec * 1000)
       .setSocketTimeout(timeoutInSec * 1000)
       .build();

     if (StringUtils.isNotBlank(ipAndPort)) {
       String[] ipAndPortArray = StringUtils.split(ipAndPort, ":");
       HttpHost proxy = new HttpHost(ipAndPortArray[0], Integer.parseInt(ipAndPortArray[1]));
       DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
       this.httpClient = HttpClients.custom().setConnectionManager(cm).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
         .setDefaultRequestConfig(requestConfig)
         .setRoutePlanner(routePlanner).build();
     } else {
       this.httpClient = HttpClients.custom().setConnectionManager(cm).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
         .setDefaultRequestConfig(requestConfig)
         .build();
     }
   }

   public byte[] getBinResource(String url)
     throws Exception
   {
     return getBinResource(url, null);
   }

   public byte[] getBinResource(String url, Map headers) throws Exception
   {
     if (!StringUtils.isAsciiPrintable(url)) {
       throw new Exception("A url must ONLY contain Ascii character!");
     }
     HttpGet httpGet = new HttpGet(url);
     setHeader(headers, httpGet);

     return executeMethod(this.httpClient, httpGet);
   }

   public String getTextResource(String url, String encoding) throws Exception
   {
     return getTextResource(url, encoding, null);
   }

   public String getTextResource(String url, String encoding, Map headers) throws Exception
   {
     byte[] contentBytes = getBinResource(url, headers);
     if (StringUtils.isNotBlank(encoding)) {
       return new String(contentBytes, encoding);
     }
     return new String(contentBytes, new CharsetDetector().analyse(contentBytes));
   }

   public byte[] postBinResource(String url, ListOrderedMap<String, String> postNvps, String encoding) throws Exception
   {
     return postBinResource(url, null, postNvps, encoding);
   }

   public byte[] postBinResource(String url, byte[] postBody) throws Exception
   {
     return postBinResource(url, null, postBody);
   }

   public byte[] postBinResource(String url, Map headers, ListOrderedMap<String, String> postNvps, String encoding) throws Exception
   {
     if (!StringUtils.isAsciiPrintable(url))
       throw new Exception("A url must ONLY contain Ascii character!");
     HttpPost httpPost = new HttpPost(url);
     setHeader(headers, httpPost);
     setRequestBody(postNvps, encoding, httpPost);
     return executeMethod(this.httpClient, httpPost);
   }

   public byte[] postBinResource(String url, Map headers, byte[] postBody) throws Exception
   {
     if (!StringUtils.isAsciiPrintable(url))
       throw new Exception("A url must ONLY contain Ascii character!");
     HttpPost httpPost = new HttpPost(url);
     setHeader(headers, httpPost);
     httpPost.setEntity(new ByteArrayEntity(postBody));

     return executeMethod(this.httpClient, httpPost);
   }

   private void setHeader(Map headers, HttpRequestBase httpRequestBase)
   {
     if (headers != null)
     {
       String aKey;
       for (Iterator i$ = headers.keySet().iterator(); i$.hasNext(); httpRequestBase.setHeader(aKey, (String)headers.get(aKey)))
         aKey = (String)i$.next();
     }
   }

   private void setRequestBody(ListOrderedMap<String, String> postNvps, String encoding, HttpPost httpPost)
     throws UnsupportedEncodingException
   {
     if (postNvps != null) {
       List nvps = new ArrayList();
       String aKey;
       for (Iterator i$ = postNvps.keyList().iterator(); i$.hasNext(); nvps.add(new BasicNameValuePair(aKey, (String)postNvps.get(aKey)))) {
         aKey = (String)i$.next();
       }
       if (StringUtils.isBlank(encoding))
         encoding = "UTF-8";
       httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding.toUpperCase()));
     }
   }

   private byte[] executeMethod(CloseableHttpClient httpClient, HttpRequestBase httpRequestBase)
     throws Exception
   {
     CloseableHttpResponse httpResponse = null;
     try {
       httpResponse = httpClient.execute(httpRequestBase);
       if (httpResponse.getStatusLine().getStatusCode() != 200) {
         httpRequestBase.abort();
         throw new Exception("Http Response Status Code : " + httpResponse.getStatusLine().getStatusCode());
       }
       return EntityUtils.toByteArray(httpResponse.getEntity());
     } finally {
       if (httpResponse != null)
         httpResponse.close();
     }
   }

   public static void main(String[] args) throws Exception
   {
     System.setProperty("javax.net.ssl.trustStore", "C:\\Develop\\3rd\\Java\\jre1.8.0_121\\lib\\security\\7teen");
     System.out.println(new LoadHttpResource().getTextResource("https://www.internal.7teen.cn", null));
   }
 }

