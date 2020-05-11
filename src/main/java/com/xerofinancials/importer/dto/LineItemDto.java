package com.xerofinancials.importer.dto;

import com.xero.models.accounting.BankTransaction;
import com.xero.models.accounting.Invoice;
import com.xero.models.accounting.LineItem;
import com.xero.models.accounting.LineItemTracking;
import com.xerofinancials.importer.utils.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;

public class LineItemDto extends UniqueDto {
    private String lineItemId;
    private String description;
    private Double quantity;
    private Double unitAmount;
    private String itemCode;
    private String accountCode;
    private String taxType;
    private Double taxAmount;
    private Double lineAmount;
    private String tracking ;
    private Double discountRate;
    private Double discountAmount;
    private String repeatingInvoiceID;

    private String bankTransactionId;
    private String invoiceId;

    public LineItemDto (LineItem xeroLineItem, BankTransaction xeroBankTransaction) {
        if (xeroLineItem == null) {
            return;
        }
        this.lineItemId = xeroLineItem.getLineItemID().toString();
        this.bankTransactionId = xeroBankTransaction.getBankTransactionID().toString();
        this.description = xeroLineItem.getDescription();
        this.quantity = xeroLineItem.getQuantity() != null ? xeroLineItem.getQuantity().doubleValue() : null;
        this.unitAmount = xeroLineItem.getUnitAmount() != null ? xeroLineItem.getUnitAmount().doubleValue() : null;
        this.itemCode = StringUtils.replaceEmptyWithNull(xeroLineItem.getItemCode());
        this.accountCode = StringUtils.replaceEmptyWithNull(xeroLineItem.getAccountCode());
        this.taxType = xeroLineItem.getTaxType();
        this.taxAmount = xeroLineItem.getTaxAmount();
        this.lineAmount = xeroLineItem.getLineAmount();
        if (xeroLineItem.getTracking() != null) {
            this.tracking = xeroLineItem.getTracking()
                    .stream()
                    .map(LineItemTracking::toString)
                    .collect(Collectors.joining(", "));
        }
        this.discountRate = xeroLineItem.getDiscountRate();
        this.discountAmount = xeroLineItem.getDiscountAmount();
        if (xeroLineItem.getRepeatingInvoiceID() != null) {
            this.repeatingInvoiceID = xeroLineItem.getRepeatingInvoiceID().toString();
        }
    }

    public LineItemDto (LineItem xeroLineItem, Invoice xeroInvoice) {
        if (xeroLineItem == null) {
            return;
        }
        this.lineItemId = xeroLineItem.getLineItemID().toString();
        this.invoiceId = xeroInvoice.getInvoiceID().toString();
        this.description = xeroLineItem.getDescription();
        this.quantity = xeroLineItem.getQuantity() != null ? xeroLineItem.getQuantity().doubleValue() : null;
        this.unitAmount = xeroLineItem.getUnitAmount() != null ? xeroLineItem.getUnitAmount().doubleValue() : null;
        this.itemCode = StringUtils.replaceEmptyWithNull(xeroLineItem.getItemCode());
        this.accountCode = StringUtils.replaceEmptyWithNull(xeroLineItem.getAccountCode());
        this.taxType = xeroLineItem.getTaxType();
        this.taxAmount = xeroLineItem.getTaxAmount();
        this.lineAmount = xeroLineItem.getLineAmount();
        if (xeroLineItem.getTracking() != null) {
            this.tracking = xeroLineItem.getTracking()
                    .stream()
                    .map(LineItemTracking::toString)
                    .collect(Collectors.joining(", "));
        }
        this.discountRate = xeroLineItem.getDiscountRate();
        this.discountAmount = xeroLineItem.getDiscountAmount();
        if (xeroLineItem.getRepeatingInvoiceID() != null) {
            this.repeatingInvoiceID = xeroLineItem.getRepeatingInvoiceID().toString();
        }
    }

    public String getLineItemId() {
        return lineItemId;
    }

    public String getDescription() {
        return description;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getUnitAmount() {
        return unitAmount;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getTaxType() {
        return taxType;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public Double getLineAmount() {
        return lineAmount;
    }

    public String getTracking() {
        return tracking;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public String getRepeatingInvoiceID() {
        return repeatingInvoiceID;
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LineItemDto that = (LineItemDto) o;
        return Objects.equals(lineItemId, that.lineItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineItemId);
    }

    @Override
    public String toString() {
        return "LineItemDto{" +
                "description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitAmount=" + unitAmount +
                ", itemCode='" + itemCode + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", taxType='" + taxType + '\'' +
                ", taxAmount=" + taxAmount +
                ", lineAmount=" + lineAmount +
                ", tracking='" + tracking + '\'' +
                ", discountRate=" + discountRate +
                ", discountAmount=" + discountAmount +
                ", repeatingInvoiceID='" + repeatingInvoiceID + '\'' +
                ", bankTransactionId='" + bankTransactionId + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                '}';
    }
}
