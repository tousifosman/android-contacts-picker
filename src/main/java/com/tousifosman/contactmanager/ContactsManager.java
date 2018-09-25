package com.tousifosman.contactmanager;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class ContactsManager {

    private final String TAG = ContactsManager.class.getSimpleName();

    public final int RESULT_CONTACT = 'C';

    @Nullable
    private static ContactsManager instance;

    @Nullable
    private BoundedManager boundedManager;

    public static BoundedManager bindWithActivity(Activity activity) {
        ContactsManager.getInstance().boundedManager = new BoundedManager(activity);
        return ContactsManager.getInstance().boundedManager;
    }

    public static ContactsManager getInstance() {
        if (instance == null)
            instance = new ContactsManager();
        return instance;
    }

    @Nullable
    BoundedManager getBoundedManager() {
        return boundedManager;
    }

    @Nullable
    public Bitmap getContactPhotoThumbnail(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);

        return BitmapFactory.decodeStream(is);
    }

    @Nullable
    public Bitmap getContactPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return BitmapFactory.decodeStream(fd.createInputStream());
        } catch (IOException e) {
            return null;
        }
    }

    String mapPhoneNumberType(int type) {
        String sType;
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                sType = "Home";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                sType = "Mobile";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                sType = "Work";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                sType = "Home Fax";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                sType = "Work Fax";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                sType = "Main";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                sType = "Pager";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                sType = "Assistant";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                sType = "Callback";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                sType = "Car";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                sType = "Company Main";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                sType = "ISDN";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                sType = "MMS";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                sType = "Other Fax";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                sType = "Radio";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                sType = "Telex";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                sType = "TTY TDD";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                sType = "Work Mobile";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                sType = "Work Pager";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                sType = "Custom";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
            default:
                sType = "Other";
        }
        return sType;
    }
}
