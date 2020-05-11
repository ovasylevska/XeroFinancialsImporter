package com.xerofinancials.importer.dto;

import org.apache.commons.codec.digest.DigestUtils;

public class UniqueDto {
    private String uniqueHash;

    public String getUniqueHash() {
        return DigestUtils.sha256Hex(toString());
    }

    protected void generateUniqueHash() {
        this.uniqueHash = DigestUtils.sha256Hex(toString());
    }
}
