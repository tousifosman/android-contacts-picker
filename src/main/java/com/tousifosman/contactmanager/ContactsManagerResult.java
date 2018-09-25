package com.tousifosman.contactmanager;

public interface ContactsManagerResult {
    void onResult(String displayName, ContactBean contactBean);
    void onFailure();
}
