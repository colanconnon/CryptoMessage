package com.colanconnon.cryptomessage.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by colanconnon on 3/18/15.
 */
public class RSAKeyManager {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Context context;

    public RSAKeyManager(Context context){
        this.context = context;
    }
    public void generateKeyPair(){
        SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        RSAEncrypt rsaEncrypt = new RSAEncrypt();
        rsaEncrypt.generateKeyPair();
        PublicKey publicKey = rsaEncrypt.getPublicKey();
        PrivateKey privateKey = rsaEncrypt.getPrivateKey();

        byte[] bytesPublicKey = publicKey.getEncoded();
        String publicKeyString = Base64.encodeToString(bytesPublicKey, Base64.DEFAULT);

        byte[] bytesPrivateKey = privateKey.getEncoded();
        String privateKeyString = Base64.encodeToString(bytesPrivateKey, Base64.DEFAULT);

        editor.putString("privateKey", privateKeyString);
        editor.putString("publicKey", publicKeyString);

        editor.commit();
    }
    public boolean isKeysInEditor(){
        SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
        String privateKey = prefs.getString("privateKey", null);
        String publicKey = prefs.getString("publicKey", null);
        if(privateKey == null){
            return false;
        }
       if(publicKey == null){
            return false;
        }
        return true;

    }

    public void getKeysFromEditor(){
        SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
        String privateKey = prefs.getString("privateKey", null);
        String publicKey = prefs.getString("publicKey", null);
        if(privateKey == null){
            return;
        }
        if(publicKey == null){
            return;
        }
        byte[] privateKeyBytes = Base64.decode(privateKey,Base64.DEFAULT);
        PKCS8EncodedKeySpec x509KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        byte[] publicKeyBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec2 = new X509EncodedKeySpec(publicKeyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(x509KeySpec);
            this.publicKey = keyFactory.generatePublic(x509KeySpec2);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyString(){
        byte[] bytesPublicKey = this.publicKey.getEncoded();
        String publicKeyString = Base64.encodeToString(bytesPublicKey, Base64.DEFAULT);
        return publicKeyString;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
