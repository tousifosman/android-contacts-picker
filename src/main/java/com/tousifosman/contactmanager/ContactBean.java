package com.tousifosman.contactmanager;

public class ContactBean {
    private String contactId;
    private String number;
    private String type;

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getContactId() {
        return contactId;
    }

    public ContactBean(String contactId, String number, String type) {
        this.contactId = contactId;
        this.number = number;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ContactBean) {
            ContactBean contactBean = (ContactBean) obj;
            return contactBean.number.equals(number) && contactBean.type.equals(type);
        }
        return false;
    }
}
