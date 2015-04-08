package com.colanconnon.cryptomessage.encryption;

import android.util.Log;

import com.colanconnon.cryptomessage.models.GCDReturn;
import com.colanconnon.cryptomessage.models.RSAReturnClass;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by colanconnon on 2/7/15.
 */
public class RSAEncryption {
    private static final double LOG2 = Math.log(2.0);

    public static BigInteger encryptWithRSA(RSAReturnClass rsaReturnClass, BigInteger message){
        int e = 5;
        BigInteger cipherText = message.pow(5).mod(rsaReturnClass.getN());
        return cipherText;
    }
    public static BigInteger decryptWithRSA(RSAReturnClass rsaReturnClass, BigInteger cipher){
        BigInteger plainText = cipher.modPow(rsaReturnClass.getD5() ,rsaReturnClass.getN());
        return plainText;
    }
    public static RSAReturnClass generateRSAKey(int k){
        if(k>= 2048 && k <= 8192){
            BigInteger p = generateRSAPrime(k/2);
            BigInteger q = generateRSAPrime(k/2);
            if(!p.equals(q)){
                BigInteger t = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))).divide(p.gcd(q.subtract(BigInteger.ONE)));


               GCDReturn gcdReturn2 = extendedGCD(BigInteger.valueOf(5) ,t);
               BigInteger g = gcdReturn2.getD();
               BigInteger u = gcdReturn2.getUd();
               BigInteger v = gcdReturn2.getVd();
                if(!g.equals(BigInteger.ONE)){
                    Log.e("Tag", "HEree");
                    return null;
                }
                BigInteger d5 = u.mod(t);

                RSAReturnClass rsaReturnClass = new RSAReturnClass();
                rsaReturnClass.setN(p.multiply(q));
                rsaReturnClass.setP(p);
                rsaReturnClass.setQ(q);

                rsaReturnClass.setD5(d5);
                Log.e("Tag", "HEre");
                return rsaReturnClass;
            }
        }
        return null;
    }

    private static GCDReturn extendedGCD(BigInteger a, BigInteger b){
        if(a.compareTo(BigInteger.ZERO) > 0 && b.compareTo(BigInteger.ZERO) > 0){
            BigInteger c = a;
            BigInteger d = b;
            BigInteger uc = BigInteger.ONE;
            BigInteger vc = BigInteger.ZERO;
            BigInteger ud = BigInteger.ZERO;
            BigInteger vd = BigInteger.ONE;
            while (!c.equals(BigInteger.ZERO)){
                BigInteger q = d.divide(c);
                c = d.subtract(q.multiply(c));
                d  = c;
                uc = ud.subtract(q.multiply(uc));
                vc = vd.subtract(q.multiply(vc));
                ud = uc;
                vd = vc;

            }
            GCDReturn gcdReturn = new GCDReturn();
            gcdReturn.setD(d);
            gcdReturn.setUd(ud);
            gcdReturn.setVd(vd);
            return gcdReturn;
        }
        return null;
    }
    //INPUT number of bits k
    //output n which is the prime number.

    public static BigInteger generateRSAPrime(int k){

        if(k >= 1024 && k <=4096){
            int r = 100 * k;
            BigInteger n = BigInteger.ZERO;
            do{
                r = r - 1;
                if (r == 0) {

                    return BigInteger.ZERO;
                }
                if(r > 0){
                    SecureRandom rand = new SecureRandom();

                    // rand between (2^k) -1  2^(k-1)
                    BigInteger max = BigInteger.valueOf(2).pow(k).subtract(BigInteger.ONE);

                    BigInteger min = BigInteger.valueOf(2).pow(k-1);

//

                    do {
                        n = new BigInteger(max.bitLength(), rand);
                    } while (n.compareTo(max) >= 0);
                    if(n.subtract(min).signum() != 1){
                        n = n.add(min);

                    }


                }
                if(!(n.mod(BigInteger.valueOf(3)).equals(BigInteger.ONE))){
                    if(!(n.mod(BigInteger.valueOf(5)).equals(BigInteger.ONE))){
                        if(n.isProbablePrime(90)){
                            return n;
                        }
                    }
                }
            }
            while(true);

        }
        return BigInteger.ZERO;
    }


   public static String encryption(String s, RSAReturnClass rsaReturnClass){
        BigInteger big = new BigInteger("0");
        try {
            byte[] b = s.getBytes("ISO-8859-1");
            big = byteToBigInteger(b);
            big = encryptWithRSA(rsaReturnClass, big);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return BigIntegerToHexString(big);
    }
    public static String decryption(String s, RSAReturnClass rsaReturnClass){
        BigInteger big = new BigInteger(s, 16);
        big = decryptWithRSA(rsaReturnClass, big);
        return BigIntegerToString(big).trim();
    }

    private static BigInteger byteToBigInteger(byte[] b) {
        BigInteger bigInt = new BigInteger("0");

        for (int i = 0; i < b.length; i++) {
            bigInt = bigInt.shiftLeft(8);
            Integer value = new Integer((b[i] & 0x7F) + (b[i] < 0 ? 128 : 0)); // 0 to 255 Ascii Code
            bigInt = bigInt.or(new BigInteger(value.toString()));
        }
        return bigInt;
    }
    private static String BigIntegerToString(BigInteger big) {
        byte[] bnew = big.toByteArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bnew.length; i++) {
            int value = (bnew[i] & 0x7F) + (bnew[i] < 0 ? 128 : 0); // 0 to 255 Ascii Code
            sb.append((char) value);
        }
        return sb.toString();
    }
    private static String BigIntegerToHexString(BigInteger big) {
        String str = big.toString(16);
        return str;
    }
    public static double logBigInteger(BigInteger val) {
        int blex = val.bitLength() - 1022;
        if (blex > 0)
            val = val.shiftRight(blex);
        double res = Math.log(val.doubleValue());
        return blex > 0 ? res + blex * LOG2 : res;
    }
}
