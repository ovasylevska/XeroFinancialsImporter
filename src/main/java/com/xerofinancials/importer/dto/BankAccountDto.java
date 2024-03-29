package com.xerofinancials.importer.dto;

import com.xero.models.accounting.Account;
import com.xerofinancials.importer.utils.StringUtils;

import java.util.Objects;

public class BankAccountDto extends UniqueDto {
    private String bankAccountId;
    private String name;
    private String code;

    public BankAccountDto(Account xeroBankAccount) {
        if (xeroBankAccount != null) {
            this.bankAccountId = xeroBankAccount.getAccountID().toString();
            this.name = xeroBankAccount.getName();
            this.code = StringUtils.replaceEmptyWithNull(xeroBankAccount.getCode());
            generateUniqueHash();
        }
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BankAccountDto that = (BankAccountDto) o;
        return Objects.equals(bankAccountId, that.bankAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankAccountId);
    }

    @Override
    public String toString() {
        return "BankAccountDto{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
