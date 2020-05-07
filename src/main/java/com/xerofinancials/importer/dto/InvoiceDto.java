package com.xerofinancials.importer.dto;

import com.xero.models.accounting.Invoice;
import com.xerofinancials.importer.utils.DateUtils;
import org.threeten.bp.OffsetDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InvoiceDto {
    private String invoiceID;
    private String invoiceType;
    private String contactId;
    private LocalDate date;
    private LocalDate dueDate;
    private String lineAmountTypes;
    private String invoiceNumber;
    private String reference;
    private String brandingThemeID;
    private String url;
    private String currencyCode;
    private Double currencyRate;
    private String status;
    private int sentToContact;
    private LocalDate expectedPaymentDate;
    private LocalDate plannedPaymentDate;
    private Double ciSDeduction;
    private Double subTotal;
    private Double totalTax;
    private Double total;
    private Double totalDiscount;
    private int hasAttachments;
    private int isDiscounted;
    private Double amountDue;
    private Double amountPaid;
    private LocalDate fullyPaidOnDate;
    private Double amountCredited;
    private LocalDateTime updatedDateUTC;
    private String statusAttributeString;

    public InvoiceDto(Invoice xeroInvoice) {
        this.invoiceID = xeroInvoice.getInvoiceID().toString();
        if (xeroInvoice.getType() != null) {
            this.invoiceType = xeroInvoice.getType().getValue();
        }
        if (xeroInvoice.getContact() != null) {
            this.contactId = xeroInvoice.getContact().getContactID().toString();
        }
        if (xeroInvoice.getDate() != null) {
            org.threeten.bp.LocalDate date = xeroInvoice.getDate();
            this.date = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        if (xeroInvoice.getDueDate() != null) {
            org.threeten.bp.LocalDate date = xeroInvoice.getDueDate();
            this.dueDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        if (xeroInvoice.getLineAmountTypes() != null) {
            this.lineAmountTypes = xeroInvoice.getLineAmountTypes().getValue();
        }
        this.invoiceNumber = xeroInvoice.getInvoiceNumber();
        this.reference = xeroInvoice.getReference();
        if (xeroInvoice.getBrandingThemeID() != null) {
            this.brandingThemeID = xeroInvoice.getBrandingThemeID().toString();
        }
        this.url = xeroInvoice.getUrl();
        if (xeroInvoice.getCurrencyCode() != null) {
            this.currencyCode = xeroInvoice.getCurrencyCode().getValue();
        }
        this.currencyRate = xeroInvoice.getCurrencyRate();
        if (xeroInvoice.getStatus() != null) {
            this.status = xeroInvoice.getStatus().getValue();
        }
        if (xeroInvoice.getSentToContact() != null) {
            this.sentToContact = xeroInvoice.getSentToContact() ? 1 : 0;
        } else {
            this.sentToContact = -1;
        }
        if (xeroInvoice.getExpectedPaymentDate() != null) {
            org.threeten.bp.LocalDate date = xeroInvoice.getExpectedPaymentDate();
            this.expectedPaymentDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        if (xeroInvoice.getPlannedPaymentDate() != null) {
            org.threeten.bp.LocalDate date = xeroInvoice.getPlannedPaymentDate();
            this.plannedPaymentDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        this.ciSDeduction = xeroInvoice.getCiSDeduction();
        this.subTotal = xeroInvoice.getSubTotal();
        this.totalTax = xeroInvoice.getTotalTax();
        this.total = xeroInvoice.getTotal();
        this.totalDiscount = xeroInvoice.getTotalDiscount();
        this.hasAttachments = xeroInvoice.getHasAttachments() ? 1 : 0;
        this.isDiscounted = xeroInvoice.getIsDiscounted() ? 1 : 0;
        this.amountDue = xeroInvoice.getAmountDue();
        this.amountPaid = xeroInvoice.getAmountPaid();
        if (xeroInvoice.getFullyPaidOnDate() != null) {
            org.threeten.bp.LocalDate date = xeroInvoice.getFullyPaidOnDate();
            this.fullyPaidOnDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        this.amountCredited = xeroInvoice.getAmountCredited();
        if (xeroInvoice.getUpdatedDateUTC() != null) {
            final OffsetDateTime updatedDateUTC = xeroInvoice.getUpdatedDateUTC();
            this.updatedDateUTC = DateUtils.convertToUtc(updatedDateUTC);
        }
        this.statusAttributeString = xeroInvoice.getStatusAttributeString();
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public String getContactId() {
        return contactId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getLineAmountTypes() {
        return lineAmountTypes;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getReference() {
        return reference;
    }

    public String getBrandingThemeID() {
        return brandingThemeID;
    }

    public String getUrl() {
        return url;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Double getCurrencyRate() {
        return currencyRate;
    }

    public String getStatus() {
        return status;
    }

    public int getSentToContact() {
        return sentToContact;
    }

    public LocalDate getExpectedPaymentDate() {
        return expectedPaymentDate;
    }

    public LocalDate getPlannedPaymentDate() {
        return plannedPaymentDate;
    }

    public Double getCiSDeduction() {
        return ciSDeduction;
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

    public Double getTotalDiscount() {
        return totalDiscount;
    }

    public int getHasAttachments() {
        return hasAttachments;
    }

    public int getIsDiscounted() {
        return isDiscounted;
    }

    public Double getAmountDue() {
        return amountDue;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public LocalDate getFullyPaidOnDate() {
        return fullyPaidOnDate;
    }

    public Double getAmountCredited() {
        return amountCredited;
    }

    public LocalDateTime getUpdatedDateUTC() {
        return updatedDateUTC;
    }

    public String getStatusAttributeString() {
        return statusAttributeString;
    }
}
