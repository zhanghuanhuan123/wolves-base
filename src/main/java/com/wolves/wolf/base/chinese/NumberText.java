 package com.wolves.wolf.base.chinese;

 import java.util.HashMap;
 import java.util.Map;

 public abstract class NumberText
 {
   public static NumberText getInstance(Lang lang)
   {
     return lang.instance();
   }

   public final String getText(long number)
   {
     return getText(Long.toString(number));
   }

   public abstract String getText(String paramString);

   public final String getOrdinalText(long number)
   {
     return getOrdinalText(Long.toString(number));
   }

   public abstract String getOrdinalText(String paramString);

   abstract int limit();

   void checkNumber(String number)
   {
     if (!number.matches("-?\\d+")) {
       throw new NumberFormatException();
     }
     int length = number.length();
     if (number.startsWith("-")) {
       length--;
     }
     if (length > limit())
     {
       throw new UnsupportedOperationException("The current " + NumberText.class
         .getSimpleName() + "can only handle numbers up to (+/-)10^" +
         limit() + ".");
     }
   }

   private static class NumberTextChinese extends NumberText
   {
     private static final NumberText SIMPLIFIED = new NumberTextChinese(Type.Simplified);

     private static final NumberText TRADITIONAL = new NumberTextChinese(Type.Traditional);
     private final Type type;

     private NumberTextChinese(Type type)
     {
       super();
       assert (type != null);

       this.type = type;
     }

     int limit()
     {
       return 44;
     }

     public String getText(String number)
     {
       checkNumber(number);

       StringBuilder builder = new StringBuilder();
       buildText(builder, number);
       return builder.toString();
     }

     public String getOrdinalText(String number)
     {
       checkNumber(number);

       StringBuilder builder = new StringBuilder().append(Connect.Di);
       buildText(builder, number);
       return builder.toString();
     }

     private void buildText(StringBuilder builder, String number)
     {
       assert (builder != null);

       if (number.startsWith("-")) {
         builder.append(getConnectDisplay(Connect.Fu));
         number = number.substring(1);
       }

       int power = 0;
       while (number.length() > (power + 1) * 4) {
         power++;
       }
       while (power > 0) {
         if (extendToken(builder, number, power * 4))
           builder.append(getPowerDisplay(Power.values()[(power - 1)]));
         power--;
       }
       extendToken(builder, number, 0);
     }

     private boolean extendToken(StringBuilder builder, String number, int suffix)
     {
       assert ((builder != null) && (number.length() > suffix));

       int len = number.length() - suffix;
       int qian = len > 3 ? number.charAt(len - 4) - '0' : -1;
       int bai = len > 2 ? number.charAt(len - 3) - '0' : -1;
       int shi = len > 1 ? number.charAt(len - 2) - '0' : -1;
       int ind = number.charAt(len - 1) - '0';

       boolean nonZero = false;
       if (qian == 0) {
         if ((bai > 0) || (shi > 0) || (ind > 0))
           builder.append(getConnectDisplay(Connect.Ling));
       } else if (qian > 0) {
         builder.append(getDigitDisplay(Digit.values()[qian]))
           .append(getConnectDisplay(Connect.Qian));

         nonZero = true;
       }

       if (bai == 0) {
         if ((qian > 0) && ((shi > 0) || (ind > 0)))
           builder.append(getConnectDisplay(Connect.Ling));
       } else if (bai > 0) {
         builder.append(getDigitDisplay(Digit.values()[bai]))
           .append(getConnectDisplay(Connect.Bai));

         nonZero = true;
       }

       if (shi == 0) {
         if ((bai > 0) && (ind > 0))
           builder.append(getConnectDisplay(Connect.Ling));
       } else if (shi > 0) {
         if ((number.length() > 2) || (shi != 1))
           builder.append(getDigitDisplay(Digit.values()[shi]));
         builder.append(getConnectDisplay(Connect.Shi));
         nonZero = true;
       }

       if (ind == 0) {
         boolean addZero = len == 1;
         for (int i = 1; (addZero) && (i <= suffix); i++) {
           if (number.charAt(i) != '0')
             addZero = false;
         }
         if (addZero) builder.append(getConnectDisplay(Connect.Ling));
       }
       else { builder.append(getDigitDisplay(Digit.values()[ind]));
         nonZero = true;
       }
       return nonZero;
     }

     String getConnectDisplay(Connect connect)
     {
       assert (connect != null);

       return this.type == Type.Simplified ? connect.display : connect.displayTraditional;
     }

     String getPowerDisplay(Power power)
     {
       assert (power != null);

       return this.type == Type.Simplified ? power.display : power.displayTraditional;
     }

     String getDigitDisplay(Digit digit)
     {
       assert (digit != null);

       return this.type == Type.Simplified ? digit.display : digit.displayTraditional;
     }

     static enum Digit
     {
       Ling("零", "零"),
       Yi("一", "壹"),
       Er("二", "贰"),
       San("三", "叁"),
       Si("四", "肆"),
       Wu("五", "伍"),
       Liu("六", "陆"),
       Qi("七", "柒"),
       Ba("八", "捌"),
       Jiu("九", "玖");

       final String display;
       final String displayTraditional;

       private Digit(String display, String displayTraditional) { this.display = display;
         this.displayTraditional = displayTraditional;
       }
     }

     static enum Power
     {
       Wan("万", "萬"),
       Yi("亿", "億"),
       Zhao("兆", "兆"),
       Jing("京", "京"),
       Gai("垓", "垓"),
       Zi("秭", "秭"),
       Rang("穰", "穰"),
       Gou("沟", "溝"),
       Jian("涧", "澗"),
       Zheng("正", "正"),
       Zai("载", "載");

       final String display;
       final String displayTraditional;

       private Power(String display, String displayTraditional) { this.display = display;
         this.displayTraditional = displayTraditional;
       }
     }

     static enum Connect
     {
       Di("第", "第"),
       Fu("负", "負"),
       Ling("零", "零"),
       Shi("十", "拾"),
       Bai("百", "佰"),
       Qian("千", "仟");

       final String display;
       final String displayTraditional;

       private Connect(String display, String displayTraditional) { this.display = display;
         this.displayTraditional = displayTraditional;
       }
     }

     static enum Type
     {
       Simplified, Traditional;
     }
   }

   private static class NumberTextEnglishCleanSpaceOnly extends NumberText.NumberTextEnglish
   {
     private static final NumberText INSTANCE = new NumberTextEnglishCleanSpaceOnly();

     private NumberTextEnglishCleanSpaceOnly()
     {
       super();
     }

     String getConnectDisplay(NumberText.NumberTextEnglish.Connect connect)
     {
       return connect == NumberText.NumberTextEnglish.Connect.AfterTen ? " " : super
         .getConnectDisplay(connect);
     }
   }

   private static class NumberTextEnglish extends NumberText
   {
     private static final NumberText INSTANCE;
     private static final Map<String, String> _Ordinals;

     private NumberTextEnglish()
     {
       super();
     }

     int limit()
     {
       return 63;
     }

     public String getText(String number)
     {
       checkNumber(number);

       StringBuilder builder = new StringBuilder();
       buildText(builder, number);
       return builder.toString();
     }

     public String getOrdinalText(String number)
     {
       checkNumber(number);

       StringBuilder builder = new StringBuilder();
       buildText(builder, number);
       replaceLastTokenWithOrdinal(builder);
       return builder.toString();
     }

     private void buildText(StringBuilder builder, String number)
     {
       assert (builder != null);

       if (number.startsWith("-")) {
         builder.append(getConnectDisplay(Connect.Minus))
           .append(getConnectDisplay(Connect.AfterMinus));

         number = number.substring(1);
       }

       int power = 0;
       while (number.length() > (power + 1) * 3) {
         power++;
       }
       while (power > 0) {
         boolean modified = extendToken(builder, number, power * 3);
         if (modified)
           builder.append(getConnectDisplay(Connect.AfterNumber))
             .append(getPowerDisplay(Power.values()[(power - 1)]));
         power--;
       }
       extendToken(builder, number, 0);
     }

     private boolean extendToken(StringBuilder builder, String number, int suffix)
     {
       assert ((builder != null) && (suffix < number.length()));

       int len = number.length() - suffix;
       int hundreds = len > 2 ? number.charAt(len - 3) - '0' : -1;
       int tens = len > 1 ? number.charAt(len - 2) - '0' : -1;
       int inds = number.charAt(len - 1) - '0';

       if ((hundreds <= 0) && (tens <= 0) && (inds <= 0) && (suffix > 0))
         return false;
       if (len > 3) {
         builder.append(getConnectDisplay(Connect.AfterPower));
       }
       if (hundreds == 0) {
         if ((len > 3) && ((tens > 0) || (inds > 0)))
           builder.append(getConnectDisplay(Connect.And))
             .append(getConnectDisplay(Connect.AfterAnd));
       }
       else if (hundreds > 0) {
         builder.append(getDigitName(Digit.values()[hundreds]))
           .append(getConnectDisplay(Connect.AfterNumber))
           .append(getConnectDisplay(Connect.Hundred));

         if ((tens > 0) || (inds > 0)) {
           builder.append(getConnectDisplay(Connect.AfterHundred))
             .append(getConnectDisplay(Connect.And))
             .append(getConnectDisplay(Connect.AfterAnd));
         }
       }

       if (tens > 1) {
         builder.append(getDigitMultiTen(Digit.values()[tens]));
         if (inds > 0) {
           builder.append(getConnectDisplay(Connect.AfterTen));
         }
       }
       if (tens == 1)
         builder.append(getDigitPlusTen(Digit.values()[inds]));
       else if ((inds > 0) || (number.length() == 1)) {
         builder.append(getDigitName(Digit.values()[inds]));
       }
       return true;
     }

     private void replaceLastTokenWithOrdinal(StringBuilder builder)
     {
       assert ((builder != null) && (builder.length() > 0));

       int suffix = builder.length() - 1;
       while ((suffix >= 0) && (!isConnect(builder.charAt(suffix))))
         suffix--;
       String lastToken = builder.substring(suffix + 1);
       builder.delete(suffix + 1, builder.length()).append(toOrdinal(lastToken));
     }

     String getPowerDisplay(Power power)
     {
       assert (power != null);

       return power.display;
     }

     String getConnectDisplay(Connect connect)
     {
       assert (connect != null);

       return connect.display;
     }

     String getDigitName(Digit digit)
     {
       assert (digit != null);

       return digit.display;
     }

     String toOrdinal(String name)
     {
       assert ((name != null) && (!name.isEmpty()));

       String result = (String)_Ordinals.get(name);
       if (result == null) {
         if (name.charAt(name.length() - 1) == 'y')
           result = name.substring(0, name.length() - 1) + "ieth";
         else
           result = name + "th";
       }
       return result;
     }

     String getDigitPlusTen(Digit digit)
     {
       assert (digit != null);

       return digit.plusTen;
     }

     String getDigitMultiTen(Digit digit)
     {
       assert (digit != null);

       return digit.multiTen;
     }

     boolean isConnect(char c) {
       return Connect.isConnect(c);
     }

     static
     {
       INSTANCE = new NumberTextEnglish();

       _Ordinals = new HashMap();
       for (Digit d : Digit.values())
         _Ordinals.put(d.display, d.displayOrdinal);
     }

     static enum Digit
     {
       Zero("zero", "zeroth", "ten", ""),
       One("one", "first", "eleven", "ten"),
       Two("two", "second", "twelve", "twenty"),
       Three("three", "third", "thirteen", "thirty"),
       Four("four", "fourth", "fourteen", "fourty"),
       Five("five", "fifth", "fifteen", "fifty"),
       Six("six", "sixth", "sixteen", "sixty"),
       Seven("seven", "seventh", "seventeen", "seventy"),
       Eight("eight", "eighth", "eighteen", "eighty"),
       Nine("nine", "nineth", "nineteen", "ninety");

       final String display;
       final String displayOrdinal;
       final String plusTen;
       final String multiTen;

       private Digit(String display, String displayOrdinal, String plusTen, String multiTen) { this.display = display;
         this.displayOrdinal = displayOrdinal;
         this.plusTen = plusTen;
         this.multiTen = multiTen;
       }
     }

     static enum Power
     {
       Thousand("thousand"),
       Million("million"),
       Billion("billion"),
       Trillion("trillion"),
       Quadrillion("quadrillion"),
       Quintillion("quintillion"),
       Sextillion("sextillion"),
       Septillion("septillion"),
       Octillion("octillion"),
       Nonillion("nonillion"),
       Decillion("decillion"),
       Undecillion("undecillion"),
       Duodecillion("duodecillion"),
       Tredecillion("tredecillion"),
       Quattuordecillion("quattuordecillion"),
       Quindecillion("quindecillion"),
       Sexdecillion("sexdecillion"),
       Septendecillion("septendecillion"),
       Octodecillion("octodecillion"),
       Novemdecillion("novemdecillion"),
       Vigintillion("vigintillion");

       final String display;

       private Power(String display)
       {
         this.display = display;
       }
     }

     static enum Connect
     {
       Minus("minus"),
       Hundred("hundred"),
       And("and"),
       AfterMinus(" "),
       AfterNumber(" "),
       AfterPower(" "),
       AfterHundred(" "),
       AfterAnd(" "),
       AfterTen("-");

       final String display;

       private Connect(String display) {
         this.display = display;
       }

       private static boolean isConnect(char c) {
         return (c == ' ') || (c == '-');
       }
     }
   }

   public static enum Lang
   {
     English(NumberText.NumberTextEnglishCleanSpaceOnly.INSTANCE),
     EnglishWithDash(NumberText.NumberTextEnglish.INSTANCE),
     ChineseSimplified(NumberText.NumberTextChinese.SIMPLIFIED),
     ChineseTraditional(NumberText.NumberTextChinese.TRADITIONAL);

     private final NumberText instance;

     private Lang(NumberText instance)
     {
       this.instance = instance;
     }

     private NumberText instance()
     {
       if (this.instance == null) {
         throw new UnsupportedOperationException("Language not supported yet : " + this);
       }

       return this.instance;
     }
   }
 }

