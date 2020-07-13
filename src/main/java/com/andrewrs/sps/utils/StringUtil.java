package com.andrewrs.sps.utils;


public class StringUtil {
  public static String escapeQuotesInParameter(String param)
  {
      StringBuilder data = new StringBuilder();
      int lastIndex = 0;
      for(int i = 0;i < param.length();i++)
      {
          if(param.charAt(lastIndex) == '\\' || param.charAt(i) != '\'' || param.charAt(i) != '\"')
            data.append(param.charAt(i));
          else
          {
              data.append('\\');
              data.append(param.charAt(i));
          }
          lastIndex = i;
      }
      return data.toString();
  }
  public static String replaceDoubleBackslashWithSingle(String input)
  {
      StringBuilder output = new StringBuilder();
      for(int i = 0;i < input.length()-1;i++)
      {
          if(input.charAt(i) == '\\' && input.charAt(i+1) == '\\')
            i++;
          output.append(input.charAt(i));
          System.out.println(input.charAt(i));
      }
      return output.toString();
  }
}