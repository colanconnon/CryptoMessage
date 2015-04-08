package com.colanconnon.cryptomessage.models;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by colanconnon on 3/19/15.
 */
public class Recipient {
    private String publicKey;
    private int deviceID;
    private String userName;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public PublicKey getPubPublicKey(){

        byte[] publicKeyBytes = Base64.decode(this.publicKey, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec2 = new X509EncodedKeySpec(publicKeyBytes);



        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

             PublicKey returnPublicKey = keyFactory.generatePublic(x509KeySpec2);

            return returnPublicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
