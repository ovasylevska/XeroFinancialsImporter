package com.xerofinancials.importer.dto;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class UniqueDto {
    private String uniqueHash;

    public String getUniqueHash() {
        return this.uniqueHash;
    }

    protected void generateUniqueHash() {
        this.uniqueHash = new String(Hex.encodeHex(DigestUtils.md5(toString())));
    }
}
