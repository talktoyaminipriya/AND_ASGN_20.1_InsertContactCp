package com.example.priya.insertcontactcp;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText mname,mphone;
    Button maddcontact,mview;
    TextView mdisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mname = (EditText)findViewById(R.id.name);
        mphone = (EditText)findViewById(R.id.phone);
        maddcontact = (Button) findViewById(R.id.addcontact);
        mview = (Button)findViewById(R.id.view);
        mdisplay = (TextView)findViewById(R.id.dispaly);

        mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayContacts();
            }
        });
        maddcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mname.getText().toString();
                String phone =mphone.getText().toString();

                if(name.equals("")&&phone.equals("")){
                    Toast.makeText(getApplicationContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                createContact(name,phone);
            }


        });

    }

    public void createContact (String name , String phone){

        Cursor cursor = getContentResolver().query(android.provider.ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        int count = cursor.getCount();
        if (count>0) {

            while (cursor.moveToFirst()) {

                String display_name = android.provider.ContactsContract.Contacts.DISPLAY_NAME;
                int colIndex = cursor.getColumnIndex(display_name);
                String existName = cursor.getString(colIndex);

                if(existName.equals(name)){
                    Toast.makeText(this,"The contact name: "+name+"already exists",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
// operation
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(getBaseContext(), "Contacts inserted sucessfully", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        Toast.makeText(this,"Created a new contact"+name,Toast.LENGTH_SHORT).show();

    }

    public void displayContacts() {

        //this class provides application access to the content model
        ContentResolver contentResolver = getContentResolver();


        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int count = cursor.getCount();
        if (count > 0) {

            String contactDetails = "";
            while (cursor.moveToFirst()) {

                String columnId = ContactsContract.Contacts._ID;
                int cursorIndex = cursor.getColumnIndex(columnId);

                String id = cursor.getString(cursorIndex);
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                int numCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if (numCount > 0) {

                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);

                    while (phoneCursor.moveToNext()) {

                        String phoneno = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        contactDetails += "Name: " + name + "Phone No: " + phoneno + "\n";
                    }
                    phoneCursor.close();
                }
            }
            mdisplay.setText(contactDetails);
        }
    }
}

