package com.colanconnon.cryptomessage.encryption;

import android.util.Base64;
import android.util.Log;

import com.colanconnon.cryptomessage.models.Message;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * Created by colanconnon on 3/18/15.
 */
public class RSAEncrypt {
    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;

    public RSAEncrypt(){


    }
    public RSAEncrypt(PublicKey publicKey, PrivateKey privateKey){
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
    public void generateKeyPair(){
        try{
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public Message encrypt(Message message){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] byteTextToEncrypt =  Base64.decode(message.getEncryptionKey(), Base64.DEFAULT);
            byte[] cipherText = cipher.doFinal(byteTextToEncrypt);
            String cipherString = Base64.encodeToString(cipherText, Base64.DEFAULT);
            message.setEncryptionKey(cipherString);
            Log.e("PubKey", publicKey.getFormat());

            Log.e("pubkey1", publicKey.getAlgorithm());
            return message;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public Message decrypt(Message message){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] byteTextToDecrypt = Base64.decode(message.getEncryptionKey(), Base64.DEFAULT);
            byte[] plainText = cipher.doFinal(byteTextToDecrypt);
            String plainTextString = Base64.encodeToString(plainText, Base64.DEFAULT);
            message.setEncryptionKey(plainTextString);
            return message;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
