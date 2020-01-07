package com.example.madcamp_project_2;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment implements MainActivity.onBackPressedListener {

    private View view;
    private RelativeLayout bigView;
    private ImageView bigImg;
    private Button backButton;
    private Button shareButton;
    private ImageButton addButton;
    private LinearLayout buttonLayout;
    private ArrayList<String> imgList = null;
    String userId;

    private GalleryFragment p2me = this;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;

        bigView = view.findViewById(R.id.bigView);
        bigImg = view.findViewById(R.id.imgView);
        buttonLayout = view.findViewById(R.id.ButtonLayout);
        backButton = view.findViewById(R.id.Back);
        shareButton = view.findViewById(R.id.Share);
        addButton = view.findViewById(R.id.addImage);
        userId = ((MainActivity)requireContext()).userId;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });

        bigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonLayout.getVisibility() == View.VISIBLE)
                    buttonLayout.setVisibility(View.GONE);
                else
                    buttonLayout.setVisibility(View.VISIBLE);
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigView.setVisibility(View.GONE);
                ((MainActivity)requireContext()).setOnBackPressedListener(null);
            }
        });

        /*shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                Uri imgUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName().concat(".provider"), new File(bigImg.getContentDescription().toString()));
                intent.putExtra(Intent.EXTRA_STREAM, imgUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share image"));
            }
        });*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                setView();
            }
        }
        else {
            setView();
        }
    }

    private void setRecyclerView() {
        final RecyclerView rcView = view.findViewById(R.id.recyclerView);
        rcView.setItemViewCacheSize(20);
        rcView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        final GalleryAdapter adapter = new GalleryAdapter(imgList, getContext());
        adapter.setOnImgClickListener(new GalleryAdapter.OnImgClickListener() {
            @Override
            public void onImgClick(String image) {
                Picasso.with(getContext())
                        .load("http://192.249.19.250:7680/upload/" + image)
                        .into(bigImg);
                buttonLayout.setVisibility(View.VISIBLE);
                bigView.setVisibility(View.VISIBLE);
                ((MainActivity)requireContext()).setOnBackPressedListener(p2me);
            }
        });

        adapter.setOnImgLongClickListener(new GalleryAdapter.OnImgLongClickListener() {
            @Override
            public void onImgLongClick(final String image) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                builder.setMessage("Are you sure you want to delete this event?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RetrofitConnection retrofitConnection = new RetrofitConnection();
                                retrofitConnection.server.deleteImage(userId, image).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("onResponse", "success");
                                            imgList.remove(image);
                                            setRecyclerView();
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

            }
        });

        rcView.setAdapter(adapter);
    }



    private void setView() {

        final RetrofitConnection retrofitConnection = new RetrofitConnection();


        retrofitConnection.server.getImgList(userId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    imgList = new ArrayList<>(response.body());
                    setRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });

    }

    @Override
    public void onBack() {
        if (bigView.getVisibility() == View.VISIBLE) {
            bigView.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
            ((MainActivity)requireContext()).setOnBackPressedListener(null);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 2 && resultCode == RESULT_OK){
            File file = new File(getPath(getContext(), data.getData()));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("img", file.getName(), requestFile);

            RetrofitConnection retrofitConnection = new RetrofitConnection();
            Call<String> call = retrofitConnection.server.uploadImage(userId, body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        setView();
                    }
                    else {
                        Log.d("onResponse", "failure");
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                }
            });
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}
