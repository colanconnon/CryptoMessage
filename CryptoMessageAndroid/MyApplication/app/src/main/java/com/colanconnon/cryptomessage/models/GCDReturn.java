package com.colanconnon.cryptomessage.models;

import java.math.BigInteger;

/**
 * Created by colanconnon on 3/17/15.
 */
public class GCDReturn {
    private BigInteger d;
    private BigInteger ud;
    private BigInteger vd;

    public BigInteger getD() {
        return d;
    }

    public void setD(BigInteger d) {
        this.d = d;
    }

    public BigInteger getUd() {
        return ud;
    }

    public void setUd(BigInteger ud) {
        this.ud = ud;
    }

    public BigInteger getVd() {
        return vd;
    }

    public void setVd(BigInteger vd) {
        this.vd = vd;
    }
}
