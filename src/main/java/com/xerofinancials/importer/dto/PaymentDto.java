package com.xerofinancials.importer.dto;

import com.xero.models.accounting.Payment;
import com.xerofinancials.importer.utils.DateUtils;
import org.threeten.bp.OffsetDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentDto {
    private String paymentId;
    private String invoiceId;
    private String creditNote; //todo : ???
    private String prepayment; //todo : ???
    private String overpayment; //todo : ???
    private String invoiceNumber;
    private String creditNoteNumber;
    private String accountId;
    private String code;
    private LocalDate paymentDate;
    private Double currencyRate;
    private Double amount;
    private String reference;
    private int isReconciled;
    private String status;
    private String paymentType;
    private LocalDateTime updatedDateUTC;
    private String bankAccountNumber;
    private String particulars;
    private String details;
    private int hasAccount;

    public PaymentDto(Payment xeroPayment) {
        this.paymentId = xeroPayment.getPaymentID().toString();
        this.invoiceId = xeroPayment.getInvoice().getInvoiceID().toString();
        if (xeroPayment.getCreditNote() != null) {
            this.creditNote = xeroPayment.getCreditNote().toString();
        }
        if (xeroPayment.getPrepayment() != null) {
            this.prepayment = xeroPayment.getPrepayment().toString();
        }
        if (xeroPayment.getOverpayment() != null) {
            this.overpayment = xeroPayment.getOverpayment().toString();
        }
        this.invoiceNumber = xeroPayment.getInvoiceNumber();
        this.creditNoteNumber = xeroPayment.getCreditNoteNumber();
        if (xeroPayment.getAccount() != null) {
            this.accountId = xeroPayment.getAccount().getAccountID().toString();
        }
        this.code = xeroPayment.getCode();
        if (xeroPayment.getDate() != null) {
            org.threeten.bp.LocalDate date = xeroPayment.getDate();
            this.paymentDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        this.currencyRate = xeroPayment.getCurrencyRate();
        this.amount = xeroPayment.getAmount();
        this.reference = xeroPayment.getReference();
        this.isReconciled = xeroPayment.getIsReconciled() ? 1 : 0;
        if (xeroPayment.getStatus() != null) {
            this.status = xeroPayment.getStatus().getValue();
        }
        if (xeroPayment.getPaymentType() != null) {
            this.paymentType = xeroPayment.getPaymentType().getValue();
        }
        if (xeroPayment.getUpdatedDateUTC() != null) {
            final OffsetDateTime updatedDateUTC = xeroPayment.getUpdatedDateUTC();
            this.updatedDateUTC = DateUtils.convertToUtc(updatedDateUTC);
        }
        this.bankAccountNumber = xeroPayment.getBankAccountNumber();
        this.particulars = xeroPayment.getParticulars();
        this.details = xeroPayment.getDetails();
        this.hasAccount = xeroPayment.getHasAccount() ? 1 : 0;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getCreditNote() {
        return creditNote;
    }

    public String getPrepayment() {
        return prepayment;
    }

    public String getOverpayment() {
        return overpayment;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCreditNoteNumber() {
        return creditNoteNumber;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCode() {
        return code;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Double getCurrencyRate() {
        return currencyRate;
    }

    public Double getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public int getIsReconciled() {
        return isReconciled;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public LocalDateTime getUpdatedDateUTC() {
        return updatedDateUTC;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getParticulars() {
        return particulars;
    }

    public String getDetails() {
        return details;
    }

    public int getHasAccount() {
        return hasAccount;
    }
}
