package com.example.lee.playinseoul.StampMap;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.SQLManager;
import com.example.lee.playinseoul.SharePostBoard.imageRotater;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xyom on 2016-09-03.
 */
public class writeContentActivity extends AppCompatActivity {

    BackPressEditText textEdt;
    EditText titleEdt;
    View okBtn;
    ImageView selectedImage;
    TextView titleText;
    TextView contentText;
    RatingBar ratingBar;


    double longitude,latitude;
    String imgRealPath="";

    int request;

    private static final String TYPE_IMAGE = "image/*";
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private String mCameraPhotoPath;

    private String oldtitle;

    private String image_path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_content_layout);

        titleEdt=(EditText)findViewById(R.id.writeTitle);
        textEdt = (BackPressEditText) findViewById(R.id.editContent);
        okBtn = findViewById(R.id.write_ok);
        selectedImage=(ImageView)findViewById(R.id.write_image);
        titleText=(TextView) findViewById(R.id.titleText);
        contentText=(TextView)findViewById(R.id.contentText);
        ratingBar=(RatingBar)findViewById(R.id.write_rating);


        textEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                titleEdt.setVisibility(View.GONE);
                selectedImage.setVisibility(View.GONE);
                contentText.setVisibility(View.GONE);
                titleText.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //글작성을 완료하는 부분. okBtn onClickListner에 넣는다.
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    SQLManager sqlm = new SQLManager(getApplicationContext(), "markerInfo.db", null, 1);
                    if(request==1) {
                        if(titleEdt.getText().toString().equals(""))
                        {
                            Toast.makeText(writeContentActivity.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        }
                        else if(textEdt.getText().toString().equals(""))
                        {
                            Toast.makeText(writeContentActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        }
                        else if(imgRealPath.equals(""))
                        {
                            Toast.makeText(writeContentActivity.this, "사진을 선택해 주세요", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            sqlm.Insert(titleEdt.getText().toString(), latitude, longitude, textEdt.getText().toString(), imgRealPath, ratingBar.getRating() + "");
                            Toast.makeText(writeContentActivity.this, "글작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            writeContentActivity.this.finish();
                        }
                    }
                    else if(request==2)
                    {
                        if(titleEdt.getText().toString().equals(""))
                        {
                            Toast.makeText(writeContentActivity.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        }
                        else if(textEdt.getText().toString().equals(""))
                        {
                            Toast.makeText(writeContentActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(image_path.equals("temp"))
                            {
                                Toast.makeText(writeContentActivity.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (imgRealPath.equals(""))
                                    imgRealPath = image_path;
                                sqlm.Update(oldtitle, titleEdt.getText().toString(), textEdt.getText().toString(), imgRealPath, ratingBar.getRating() + "");
                                Toast.makeText(writeContentActivity.this, "글작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                writeContentActivity.this.finish();
                            }
                        }
                    }

            }
        });
        selectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        Intent intent = getIntent();
        request=intent.getIntExtra("request",0);

        if(request==1) //처음 글 요청시.
        {
            longitude=intent.getDoubleExtra("longitude",0);
            latitude=intent.getDoubleExtra("latitude",0);
        }
        else if(request==2) // 수정하기 요청시
        {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String rating = intent.getStringExtra("rating");
            image_path = intent.getStringExtra("image_path");

            oldtitle=title;
            titleEdt.setText(title);
            textEdt.setText(content);
            ratingBar.setRating(Float.parseFloat(rating));
            titleText.setVisibility(View.VISIBLE);
            titleEdt.setVisibility(View.VISIBLE);
            contentText.setVisibility(View.VISIBLE);
            selectedImage.setVisibility(View.VISIBLE);

            try {
                Cursor c = getContentResolver().query(Uri.parse(image_path), null, null, null, null);
                c.moveToNext();
                final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                try {
                    selectedImage.setImageBitmap(imageRotater.SafeDecodeBitmapFile(absolutePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(writeContentActivity.this, "사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }


    }



    //경로를 얻어온 결과.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //사진을 선택해서 가져와 보여주는 과정
        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Uri result = getResultUri(data);
                imgRealPath=result.toString();

                Cursor c = getContentResolver().query(result, null, null, null, null);
                c.moveToNext();
                final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                try {
                    selectedImage.setImageBitmap(imageRotater.SafeDecodeBitmapFile(absolutePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(writeContentActivity.this, "사진 선택이 완료되었습니다.", Toast.LENGTH_SHORT).show();

            } else {
                Uri result = getResultUri(data);
                imgRealPath=result.toString();

                Cursor c = getContentResolver().query(result, null, null, null, null);
                c.moveToNext();
                final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                // uri로 비트맵 가져와 set하기.
                try {
                    selectedImage.setImageBitmap(imageRotater.SafeDecodeBitmapFile(absolutePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //

                Toast.makeText(writeContentActivity.this, "사진 선택이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }












    private void imageChooser() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(getClass().getName(), "Unable to create Image File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:"+photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType(TYPE_IMAGE);

        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent[] intentArray;
        if(takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_INTENT,galleryIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }

    private Uri getResultUri(Intent data) {
        Uri result = null;
        if(data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if(mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePath = data.getDataString();
            } else {
                filePath = "file:" + RealPathUtil.getRealPath(this, data.getData());
            }
            result = Uri.parse(filePath);
        }

        return result;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }
}




















class RealPathUtil {

    public static String getRealPath(Context context, Uri uri) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(context, uri);
        }

        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = RealPathUtil.getRealPathFromURI_API11to18(context, uri);
        }

        // SDK > 19 (Android 4.4)
        else {
            realPath = RealPathUtil.getRealPathFromURI_API19(context, uri);
        }

        return realPath;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

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
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}


