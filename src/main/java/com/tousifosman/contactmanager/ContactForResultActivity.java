package com.tousifosman.contactmanager;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class ContactForResultActivity extends Activity {

    private ContactsManager contactsManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BoundedManager boundedManager = ContactsManager.getInstance().getBoundedManager();

        if (boundedManager != null)
            boundedManager.notifyResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        this.startActivityForResult(intent, ContactsManager.getInstance().RESULT_CONTACT);
    }
}
