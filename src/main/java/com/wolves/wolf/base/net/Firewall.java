 package com.wolves.wolf.base.net;

 import java.net.Inet4Address;
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 import java.util.Collection;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.apache.commons.lang3.StringUtils;

 public class Firewall
 {
   private static final Firewall FIREWALL = new Firewall();
   private int[] baseIps;
   private int[] masks;
   private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
   private static final Pattern ADDRESS_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
   private static final String SLASH_FORMAT = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,2})";
   private static final Pattern CIDR_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,2})");
   private static final int NBITS = 32;

   public synchronized void initial(Collection<String> rules)
     throws UnknownHostException
   {
     this.baseIps = new int[rules.size()];
     this.masks = new int[rules.size()];
     int i = 0;
     for (String aRule : rules) {
       Matcher cidrMatcher = CIDR_PATTERN.matcher(aRule);
       Matcher addrMatcher = ADDRESS_PATTERN.matcher(aRule);
       int ip = ip2int("255.255.255.255", true);
       int maskLen = 32;
       int mask = 0;

       if (StringUtils.equals("*", aRule)) {
         maskLen = 0;
       }

       if (addrMatcher.matches()) {
         ip = ip2int(addrMatcher.group(), true);
         maskLen = 32;
       }

       if (cidrMatcher.matches()) {
         ip = ip2int(cidrMatcher.group(1) + "." + cidrMatcher
           .group(2) +
           "." + cidrMatcher
           .group(3) +
           "." + cidrMatcher
           .group(4),
           true);
         maskLen = Integer.parseInt(cidrMatcher.group(5));
         if ((maskLen < 0) || (maskLen > 32)) {
           throw new RuntimeException("invalid rule : " + aRule);
         }
       }

       for (int j = 0; j < maskLen; j++) {
         mask |= 1 << 31 - j;
       }
       this.masks[i] = mask;
       this.baseIps[(i++)] = (mask & ip);
     }
   }

   private int ip2int(String ipAddress, boolean checkValid) throws UnknownHostException {
     Inet4Address a = (Inet4Address)InetAddress.getByName(ipAddress);
     if ((checkValid) && (!StringUtils.equals(a.getHostAddress(), ipAddress))) {
       throw new RuntimeException("invalid ip address : " + ipAddress);
     }
     byte[] b = a.getAddress();
     return (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | b[3] & 0xFF;
   }

   public boolean accessGranted(String ip)
     throws UnknownHostException
   {
     if ((this.baseIps == null) || (this.masks == null)) {
       throw new RuntimeException("instance NOT initialized yet");
     }
     for (int i = 0; i < this.baseIps.length; i++) {
       if ((ip2int(ip, false) & this.masks[i]) == this.baseIps[i]) {
         return true;
       }
     }
     return false;
   }
 }
