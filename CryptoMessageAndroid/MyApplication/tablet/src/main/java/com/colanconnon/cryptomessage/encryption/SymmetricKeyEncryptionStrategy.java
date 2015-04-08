package com.colanconnon.cryptomessage.encryption;

import com.colanconnon.cryptomessage.models.Message;

/**
 * Created by colanconnon on 3/11/15.
 */
public interface SymmetricKeyEncryptionStrategy {

    public abstract Message encrypt(Message message);

    public abstract Message decrypt(Message message);
}
