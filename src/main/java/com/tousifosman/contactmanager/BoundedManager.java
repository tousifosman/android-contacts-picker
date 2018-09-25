package com.tousifosman.contactmanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public class BoundedManager {

    private WeakReference<Activity> activityRef;
    private ContactsManagerResult contactsManagerResult;

    BoundedManager(@NonNull Activity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    public void startContactsActivityForResult(ContactsManagerResult contactsManagerResult) {
        this.contactsManagerResult = contactsManagerResult;
        activityRef.get().startActivityForResult(
                new Intent(activityRef.get(), ContactForResultActivity.class), ContactsManager.getInstance().RESULT_CONTACT);

    }

    void notifyResult(int requestCode, int resultCode, Intent data) {

        if (activityRef == null || activityRef.get() == null || data == null
                || requestCode != ContactsManager.getInstance().RESULT_CONTACT || resultCode != Activity.RESULT_OK)
            return;

        final Activity activity = activityRef.get();
        String displayName;

        // Getting Contact ID and display name form the returned cursor.
        Cursor cursor = activity.getContentResolver().query(
                data.getData(), null, null, null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

            displayName = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            // Getting
            if (Integer.parseInt(cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                Cursor pCur = activity.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactId},
                        null);

                if (pCur != null && pCur.getCount() > 0) {

                    final ArrayList<ContactBean> phoneNumbers = new ArrayList<>();
                    HashSet<String> phoneNumberSet = new HashSet<>();

                    while (pCur.moveToNext()) {

                        String phoneNumber = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        String numberType = ContactsManager.getInstance().mapPhoneNumberType(pCur.getInt(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));

                        if (!phoneNumberSet.contains(phoneNumber)) {
                            phoneNumbers.add(new ContactBean(contactId, phoneNumber, numberType));
                            phoneNumberSet.add(phoneNumber);
                        }
                    }

                    pCur.close();

                    if (phoneNumbers.size() == 1) {
                        contactsManagerResult.onResult(displayName, phoneNumbers.get(0));
                    } else if (phoneNumbers.size() > 1) {

                        BoundedManager.ContactAdapter contactAdapter = new BoundedManager.ContactAdapter(activity);

                        contactAdapter.addAll(phoneNumbers);

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Select Phone number");

                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                contactsManagerResult.onFailure();
                            }
                        });

                        final String finalDisplayName = displayName;
                        builder.setAdapter(contactAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contactsManagerResult.onResult(finalDisplayName, phoneNumbers.get(which));
                            }
                        });
                        builder.show();

                    } else {
                        contactsManagerResult.onFailure();
                    }
                }
            } else {
                contactsManagerResult.onFailure();
            }
            cursor.close();
        }
    }

    private class ContactAdapter extends ArrayAdapter<ContactBean> {

        private Activity activity;

        private ContactAdapter(@NonNull Activity activity) {
            super(activity, android.R.layout.select_dialog_item);
            this.activity = activity;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ContactBean contactBean = getItem(position);
            LayoutInflater inflater = activity.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.list_item_contact, parent, false);
            ((TextView) rowView.findViewById(R.id.tv_li_contact_number)).setText(contactBean.getNumber());
            ((TextView) rowView.findViewById(R.id.tv_li_contact_type)).setText(contactBean.getType());

            if (position == 0)
                rowView.findViewById(R.id.divider).setVisibility(View.GONE);

            return rowView;
        }
    }

}
