package com.mrtomrichy.encryptionbenchmarking.encryption;

import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

  private static byte[] keyValue = getRandomByteArray(16);

  public static void setKeySize(int size) {
    keyValue = getRandomByteArray(size);
  }

  public static int getKeySize() {
    return keyValue.length/8;
  }

  public static Algorithm[] getSupportedEncryptionTypes() {
    TreeSet<String> algos = new TreeSet<>();
    for (Provider p : Security.getProviders()) {
      for (Map.Entry<Object, Object> e : p.entrySet()) {
        String s = e.getKey().toString()
            + " -> " + e.getValue().toString();
        if (s.startsWith("Alg.Alias.")) {
          s = s.substring(10);
        }

        if(s.contains("Cipher")) {
          s = s.substring("Cipher.".length(), s.indexOf(' '));
          algos.add(s);
        }
      }
    }

    Algorithm[] array = new Algorithm[algos.size()];
    int count = 0;
    for(String a : algos) {
      array[count] = new Algorithm(a);
      count++;
    }

    return array;
  }

  public static byte[] getRandomByteArray(int size){
    byte[] result = new byte[size];
    Random random = new Random();
    random.nextBytes(result);
    return result;
  }

  public static byte[] encrypt(byte[] Data, String algorithm) throws Exception {
    Key key = generateKey(algorithm);
    Cipher c = Cipher.getInstance(algorithm);
    c.init(Cipher.ENCRYPT_MODE, key);
    byte[] encVal = c.doFinal(Data);

    return encVal;
  }

  public static byte[] decrypt(byte[] encryptedData, String algorithm) throws Exception {
    Key key = generateKey(algorithm);
    Cipher c = Cipher.getInstance(algorithm);
    c.init(Cipher.DECRYPT_MODE, key);

    byte[] decValue = c.doFinal(encryptedData);
    return decValue;
  }

  private static Key generateKey(String algorithm) throws Exception {
    Key key = new SecretKeySpec(keyValue, algorithm);
    return key;
  }
}
