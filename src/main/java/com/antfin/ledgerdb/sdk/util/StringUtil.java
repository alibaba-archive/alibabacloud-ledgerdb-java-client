package com.antfin.ledgerdb.sdk.util;

public class StringUtil {

  public static final String EMPTY = "";

  public static boolean equals(String s1, String s2) {
    if (s1 == s2) {
      return true;
    }
    if (s1 == null || s2 == null) {
      return false;
    }
    return s1.equals(s2);
  }
}

