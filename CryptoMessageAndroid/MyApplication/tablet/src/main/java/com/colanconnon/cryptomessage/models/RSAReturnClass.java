package com.colanconnon.cryptomessage.models;

import java.math.BigInteger;

/**
 * Created by colanconnon on 3/17/15.
 */
public class RSAReturnClass {
    private BigInteger n;
    private BigInteger p;
    private BigInteger q;
    private BigInteger d5;
    private BigInteger d3;

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getQ() {
        return q;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }

    public BigInteger getD5() {
        return d5;
    }

    public void setD5(BigInteger d5) {
        this.d5 = d5;
    }

    public BigInteger getD3() {
        return d3;
    }

    public void setD3(BigInteger d3) {
        this.d3 = d3;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }
}
