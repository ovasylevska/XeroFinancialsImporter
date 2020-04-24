package com.xerofinancials.importer.dto;

import com.xero.models.accounting.Contact;

import java.util.Objects;

public class ContactDto {
    private String contactId;
    private String name;

    public ContactDto(Contact xeroContact) {
        if (xeroContact != null) {
            this.contactId = xeroContact.getContactID().toString();
            this.name = xeroContact.getName();
        }
    }

    public String getContactId() {
        return contactId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ContactDto that = (ContactDto) o;
        return Objects.equals(contactId, that.contactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactId);
    }
}
