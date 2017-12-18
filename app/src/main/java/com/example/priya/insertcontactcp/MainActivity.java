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
    Button maddcontact;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // calling views
        mname = (EditText)findViewById(R.id.name);
        mphone = (EditText)findViewById(R.id.phone);
        maddcontact = (Button) findViewById(R.id.addcontact);
     
        maddcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                //  Gets values from the UI

                String name = mname.getText().toString();
                String phone =mphone.getText().toString();
                
                // checks if fields empty or not

                if(name.equals("")&&phone.equals("")){
                    Toast.makeText(getApplicationContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                createContact(name,phone);
            }


        });

    }
// insert contact method
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
        
        // Creates a new array of ContentProviderOperation objects.
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        int rawContactInsertIndex = ops.size();

        // Builds the operation and inserts it to the array of operations
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                // Builds the operation and adds it to the array of operations
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                 // inserts the specified Name data row and sets name
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                // Builds the operation and adds it to the array of operations
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                   // inserts the specified Phone data row and sets phone number
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                // Builds the operation and adds it to the array of operations
                .build());
        
        //Applies the array of ContentProviderOperation objects in batch. The results are discarded.
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

    


