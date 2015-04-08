package com.colanconnon.cryptomessage.encryption;

import android.util.Base64;
import android.util.Log;

import com.colanconnon.cryptomessage.models.Message;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by colanconnon on 3/11/15.
 */
public class AES256Encryption implements SymmetricKeyEncryptionStrategy {

    @Override
    public Message encrypt(Message message){
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);

            SecretKey secretKey = keyGen.generateKey();
            byte[] raw = secretKey.getEncoded();
            String key = Base64.encodeToString(raw,Base64.DEFAULT);
            Cipher aesCipher = Cipher.getInstance("AES");
            byte[] byteTextToEncrypt = message.getText().getBytes();
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = aesCipher.doFinal(byteTextToEncrypt);
            String cipherString = Base64.encodeToString(cipherText, Base64.DEFAULT);
            message.setText(cipherString);
            message.setEncryptionKey(key);
            Log.e("KEY", key);
            return message;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Message decrypt(Message message) {
        try{
            Key secretKey = new SecretKeySpec(Base64.decode(message.getEncryptionKey(),Base64.DEFAULT),"AES");
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] messageBytes = Base64.decode(message.getText(), Base64.DEFAULT);
            byte[] decryptedBytes = aesCipher.doFinal(messageBytes);
            String decryptedText = new String(decryptedBytes);
            message.setText(decryptedText);
            return message;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
