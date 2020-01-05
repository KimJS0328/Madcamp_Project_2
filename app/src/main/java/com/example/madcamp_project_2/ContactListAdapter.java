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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactListAdapter extends BaseAdapter implements Filterable {
    LayoutInflater inflater = null;
    private ArrayList<ContactItem> m_oData = null;
    private ArrayList<ContactItem> filtered_m_oData = m_oData;
    private Context mContext;

    Filter listFilter;

    public ContactListAdapter(Context context, ArrayList<ContactItem> _oData)
    {
        m_oData = _oData;
        filtered_m_oData = _oData;
        this.mContext = context;
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return filtered_m_oData.size();
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
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_listview, parent, false);
        }


        ImageView imageView = (ImageView) convertView.findViewById(R.id.contact_photo);
        TextView nameView = (TextView) convertView.findViewById(R.id.contact_name);
        TextView phoneNumView = (TextView) convertView.findViewById(R.id.contact_phonenum);

        Bitmap tmp = loadContactPhoto(mContext.getContentResolver(),filtered_m_oData.get(position).getPerson_id(),filtered_m_oData.get(position).getPhoto_id());

        if (tmp == null){
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.man);
            Bitmap bitmap = resizingBitmap(((BitmapDrawable)drawable).getBitmap());
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageBitmap(tmp);
        }
        nameView.setText(filtered_m_oData.get(position).getUser_Name());
        phoneNumView.setText(filtered_m_oData.get(position).getPhNumberChanged());
        return convertView;
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

    private class ListFilter extends Filter {
        @Override
        protected  FilterResults performFiltering(CharSequence constraint){
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = m_oData;
                results.count = m_oData.size();
            }else {
                ArrayList<ContactItem> itemList = new ArrayList<ContactItem>();

                for (ContactItem item : m_oData){
                    if (item.getUser_Name().toUpperCase().contains(constraint.toString().toUpperCase()))
                        itemList.add(item);
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            //update listview by filtered data list.
            filtered_m_oData = (ArrayList<ContactItem>) results.values;

            //notify
            if (results.count > 0){
                notifyDataSetChanged();
            }else{
                notifyDataSetInvalidated();
            }

        }
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }






}
