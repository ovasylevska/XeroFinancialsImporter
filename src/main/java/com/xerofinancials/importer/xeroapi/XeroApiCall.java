package com.xerofinancials.importer.xeroapi;

import com.xero.api.client.AccountingApi;

import java.io.IOException;

public interface XeroApiCall<T> {
    T execute(AccountingApi accountingApi, String accessToken, String tenantId) throws IOException;
}
