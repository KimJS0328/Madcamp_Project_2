package com.example.madcamp_project_2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ContactsFragment extends Fragment {
    private ContactListAdapter adapter;
    private ArrayList<ContactItem> contactItems = null;
    private ListView listView = null;
    String userId;

    private Button addContactButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = getView().findViewById(R.id.listview1);
        contactItems = new ArrayList<>();
        userId = ((MainActivity)requireContext()).userId;

        addContactButton = view.findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent,0);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                builder.setMessage("Are you sure you want to delete this event?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RetrofitConnection retrofitConnection = new RetrofitConnection();
                                ContactItem tmp = contactItems.get(position);
                                retrofitConnection.server.deleteContact(userId, tmp.getUser_Name(), tmp.getUser_phNumber(), tmp.getUser_photo())
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("onResponse", "success");
                                            adapter.deleteItem(position);
                                        }
                                        else {
                                            Log.d("onResponse", new String(response.toString()));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("onFailure", t.toString());
                                    }
                                });
                            }
                        });
                builder.show();
                return false;
            }
        });

        //To Filtering
        EditText editTextFilter = view.findViewById(R.id.editTextFilter);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString();
                if (filterText.length() >0){
                    listView.setFilterText(filterText);
                }else{
                    listView.clearTextFilter();
                }
            }
        });

        RetrofitConnection retrofitConnection = new RetrofitConnection();
        retrofitConnection.server.getContactList(userId).enqueue(new Callback<List<ContactItem>>() {
            @Override
            public void onResponse(Call<List<ContactItem>> call, Response<List<ContactItem>> response) {
                if (response.isSuccessful()) {
                    contactItems = new ArrayList<>(response.body());
                    adapter = new ContactListAdapter(getContext(), contactItems);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<ContactItem>> call, Throwable t) {

            }
        });
    }


    public Bitmap loadContactPhoto(ContentResolver cr, long id, long photo_id){
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input != null)
            return resizingBitmap(BitmapFactory.decodeStream(input));
        else
            Log.d("PHOTO","first try failed to load photo");

        byte[] photoBytes = null;
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
        Cursor c = cr.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO},null,null,null );
        try{
            if(c.moveToFirst())
                photoBytes = c.getBlob(0);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            c.close();
        }

        if (photoBytes != null)
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.length));
        else
            Log.d("PHOTO","second try also failed");
        return null;
    }

    public Bitmap resizingBitmap(Bitmap oBitmap){
        if (oBitmap == null)
            return null;
        float width = oBitmap.getWidth();
        Log.d("size: ", "" + oBitmap.getWidth());
        float height = oBitmap.getHeight();
        float resizing_size = 270;
        Bitmap rBitmap = null;
        if (width < resizing_size){
            float mWidth = (float) (width/100);
            float fScale = (float) (resizing_size/mWidth);
            width *= (fScale/100);
            height *= (fScale/100);
        }else if (height < resizing_size){
            float mHeight = (float) (height/100);
            float fScale = (float) (resizing_size/mHeight);
            width *= (fScale/100);
            height *= (fScale/100);
        }
        Log.d("rBitmap",  + width + ", " + height);
        rBitmap = Bitmap.createScaledBitmap(oBitmap, (int) width, (int) height, true);
        return  rBitmap;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0 && resultCode == RESULT_OK){
            Context context = this.getContext();
            ContentResolver result = context.getContentResolver();
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID,
                    ContactsContract.Contacts._ID
            };
            String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
                    " COLLATE LOCALIZED ASC";

            Cursor cursor = result.query(data.getData(),
                    projection, null, null, sortOrder);

            cursor.moveToFirst();

            long photo_id = cursor.getLong(2);
            long person_id = cursor.getLong(3);
            Bitmap tmp = loadContactPhoto(getContext().getContentResolver(),person_id, photo_id);
            if (tmp == null) {
                Drawable drawable = context.getResources().getDrawable(R.mipmap.man);
                tmp = resizingBitmap(((BitmapDrawable)drawable).getBitmap());
            }

            final ContactItem contactItem = new ContactItem();
            contactItem.setUser_phNumber(cursor.getString(0));
            contactItem.setUser_Name(cursor.getString(1));
            contactItem.setUser_photo("");

            File f = new File(context.getExternalCacheDir(), "tmp");
            try {
                f.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                tmp.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = null;
                fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
                MultipartBody.Part body = MultipartBody.Part.createFormData("img", f.getName(), reqFile);

                RetrofitConnection retrofitConnection = new RetrofitConnection();
                retrofitConnection.server.createContact(userId, contactItem.getUser_Name(), contactItem.getUser_phNumber(), body).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            contactItem.setUser_photo(response.body());
                            adapter.addItem(contactItem);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }



}

