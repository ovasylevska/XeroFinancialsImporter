package com.xerofinancials.importer.dto;

import com.xero.models.accounting.BankTransaction;
import com.xerofinancials.importer.utils.DateUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.threeten.bp.OffsetDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class BankTransactionDto extends UniqueDto {
    private String bankTransactionId;
    private String type;
    private String contactId;
    private String bankAccountId;
    private int isReconciled;
    private LocalDate date;
    private String reference;
    private String currencyCode;
    private Double currencyRate;
    private String url;
    private String status;
    private String lineAmountTypes;
    private Double subTotal;
    private Double totalTax;
    private Double total;
    private String prepaymentId;
    private String overpaymentId;
    private LocalDateTime updatedDateUTC;
    private int hasAttachments;
    private String statusAttributeString;

    public BankTransactionDto(BankTransaction xeroBankTransaction) {
        this.bankTransactionId = xeroBankTransaction.getBankTransactionID().toString();
        if (xeroBankTransaction.getType() != null) {
            this.type = xeroBankTransaction.getType().getValue();
        }
        if (xeroBankTransaction.getContact() != null) {
            this.contactId = xeroBankTransaction.getContact().getContactID().toString();
        }
        if (xeroBankTransaction.getBankAccount() != null) {
            this.bankAccountId = xeroBankTransaction.getBankAccount().getAccountID().toString();
        }
        this.isReconciled = xeroBankTransaction.getIsReconciled() ? 1 : 0;
        if (xeroBankTransaction.getDate() != null) {
            org.threeten.bp.LocalDate date = xeroBankTransaction.getDate();
            this.date = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        this.reference = xeroBankTransaction.getReference();
        if (xeroBankTransaction.getCurrencyCode() != null) {
            this.currencyCode = xeroBankTransaction.getCurrencyCode().getValue();
        }
        this.currencyRate = xeroBankTransaction.getCurrencyRate();
        this.url = xeroBankTransaction.getUrl();
        if (xeroBankTransaction.getStatus() != null) {
            this.status = xeroBankTransaction.getStatus().getValue();
        }
        if (xeroBankTransaction.getLineAmountTypes() != null) {
            this.lineAmountTypes = xeroBankTransaction.getLineAmountTypes().getValue();
        }
        this.subTotal = xeroBankTransaction.getSubTotal();
        this.totalTax = xeroBankTransaction.getTotalTax();
        this.total = xeroBankTransaction.getTotal();
        if (xeroBankTransaction.getPrepaymentID() != null) {
            this.prepaymentId = xeroBankTransaction.getPrepaymentID().toString();
        }
        if (xeroBankTransaction.getPrepaymentID() != null) {
            this.prepaymentId = xeroBankTransaction.getPrepaymentID().toString();
        }
        if (xeroBankTransaction.getOverpaymentID() != null) {
            this.overpaymentId = xeroBankTransaction.getOverpaymentID().toString();
        }
        if (xeroBankTransaction.getUpdatedDateUTC() != null) {
            final OffsetDateTime updatedDateUTC = xeroBankTransaction.getUpdatedDateUTC();
            this.updatedDateUTC = DateUtils.convertToUtc(updatedDateUTC);
        }
        this.hasAttachments = xeroBankTransaction.getHasAttachments() ? 1 : 0;
        this.statusAttributeString = xeroBankTransaction.getStatusAttributeString();
        getUniqueHash();
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public String getType() {
        return type;
    }

    public String getContactId() {
        return contactId;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public int getIsReconciled() {
        return isReconciled;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getReference() {
        return reference;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Double getCurrencyRate() {
        return currencyRate;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getLineAmountTypes() {
        return lineAmountTypes;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public Double getTotalTax() {
        return totalTax;
    }

    public Double getTotal() {
        return total;
    }

    public String getPrepaymentId() {
        return prepaymentId;
    }

    public String getOverpaymentId() {
        return overpaymentId;
    }

    public LocalDateTime getUpdatedDateUTC() {
        return updatedDateUTC;
    }

    public int getHasAttachments() {
        return hasAttachments;
    }

    public String getStatusAttributeString() {
        return statusAttributeString;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BankTransactionDto that = (BankTransactionDto) o;
        return Objects.equals(bankTransactionId, that.bankTransactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankTransactionId);
    }

    @Override
    public String toString() {
        return "BankTransactionDto{" +
                "type='" + type + '\'' +
                ", contactId='" + contactId + '\'' +
                ", bankAccountId='" + bankAccountId + '\'' +
                ", isReconciled=" + isReconciled +
                ", date=" + date +
                ", reference='" + reference + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", currencyRate=" + currencyRate +
                ", url='" + url + '\'' +
                ", status='" + status + '\'' +
                ", lineAmountTypes='" + lineAmountTypes + '\'' +
                ", subTotal=" + subTotal +
                ", totalTax=" + totalTax +
                ", total=" + total +
                ", prepaymentId='" + prepaymentId + '\'' +
                ", overpaymentId='" + overpaymentId + '\'' +
                ", updatedDateUTC=" + updatedDateUTC +
                ", hasAttachments=" + hasAttachments +
                ", statusAttributeString='" + statusAttributeString + '\'' +
                '}';
    }
}
