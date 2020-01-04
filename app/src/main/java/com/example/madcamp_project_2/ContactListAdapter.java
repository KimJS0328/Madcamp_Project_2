package com.example.madcamp_project_2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<ContactItem> m_oData = null;
    private int nListCnt = 0;
    private Context mContext;
    public ContactListAdapter(Context context, ArrayList<ContactItem> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
        this.mContext = context;
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.contact_listview, parent, false);
        }


        ImageView imageView = (ImageView) convertView.findViewById(R.id.contact_photo);
        TextView nameView = (TextView) convertView.findViewById(R.id.contact_name);
        TextView phoneNumView = (TextView) convertView.findViewById(R.id.contact_phonenum);

        Bitmap tmp = loadContactPhoto(mContext.getContentResolver(),m_oData.get(position).getPerson_id(),m_oData.get(position).getPhoto_id());

        if (tmp == null){
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.man);
            Bitmap bitmap = resizingBitmap(((BitmapDrawable)drawable).getBitmap());
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageBitmap(tmp);
        }
        nameView.setText(m_oData.get(position).getUser_Name());
        phoneNumView.setText(m_oData.get(position).getPhNumberChanged());
        return convertView;
    }

    //button이 눌러졋을 떄 실행되는 onClick함수
    public  void  onClick(View v){

    }


    //to return bitmap contact image
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


}
