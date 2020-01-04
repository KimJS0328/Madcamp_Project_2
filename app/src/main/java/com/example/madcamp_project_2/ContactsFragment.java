package com.example.madcamp_project_2;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ContactsFragment extends Fragment {
    private ContactListAdapter adapter;
    private ArrayList<ContactItem> contactitems = null;
    private ListView listView = null;

    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (contactitems == null) {

            contactitems = getContactList();

            adapter = new ContactListAdapter(this.getContext(), contactitems);

            listView = view.findViewById(R.id.listview1);
            listView.setAdapter(adapter);
        }
        nextButton = view.findViewById(R.id.nextbtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), addContactLayout.class);
                startActivityForResult(intent, Code.requestCode);

            }
        });
    }

    public ArrayList<ContactItem> getContactList(){
        Context context = this.getContext();
        ContentResolver result = context.getContentResolver();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts._ID
        };
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
                " COLLATE LOCALIZED ASC";
        Cursor cursor = result.query(uri, projection, null, selectionArgs, sortOrder);
        LinkedHashSet<ContactItem> hashlist = new LinkedHashSet<>();
        if (cursor.moveToFirst()){
            do{
                long photo_id = cursor.getLong(2);
                long person_id = cursor.getLong(3);
                ContactItem contactItem = new ContactItem();
                contactItem.setUser_phNumber(cursor.getString(0));
                contactItem.setUser_Name(cursor.getString(1));
                contactItem.setPhoto_id((photo_id));
                contactItem.setPerson_id((person_id));

                hashlist.add(contactItem);
            }while (cursor.moveToNext());
        }
        ArrayList<ContactItem> contactItems = new ArrayList<>(hashlist);
        for (int i = 0; i < contactItems.size(); i++){
            contactItems.get(i).setId(i);
        }
        return contactItems;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == Code.requestCode && resultCode == Code.resultCode) {
            Log.d("ahsdfhsdklfkl","되돌아오긴함");
            ContactItem tmp_contactItem = new ContactItem();
            tmp_contactItem.setUser_Name(data.getStringExtra("name"));
            tmp_contactItem.setUser_phNumber(data.getStringExtra("phone"));
            Log.d("ahsdfhsdklfkl",""+tmp_contactItem.getUser_Name());
            contactitems.add(tmp_contactItem);
            adapter = new ContactListAdapter(getContext(), contactitems);
            listView.setAdapter(adapter);
        }
    }
}

