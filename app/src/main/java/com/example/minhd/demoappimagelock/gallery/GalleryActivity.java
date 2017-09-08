package com.example.minhd.demoappimagelock.gallery;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.minhd.demoappimagelock.R;
import com.example.minhd.demoappimagelock.gallery.utils.BitmapUtils;
import com.example.minhd.demoappimagelock.gallery.utils.ItemGallaryImage;
import com.example.minhd.demoappimagelock.gallery.utils.PairInt;
import com.example.minhd.demoappimagelock.gallery.utils.PermissionUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.minhd.demoappimagelock.Main2Activity.ivImage;

public class GalleryActivity extends BaseGalleryActivity implements GalleryImageAdapter.IGalleryImageAdapter, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static final String KEY_RETURE_GALLERY = "KEY_RETURE_GALLERY";
    private static final int COUNT_OF_PAGE_DEFAULT_LOAD_GALLARY = 30;
    private RecyclerView rcImage;
    private SwipeRefreshLayout refresh;
    private GalleryImageAdapter galleryImageAdapter;
    private int totalImage;
    private List<ItemGallaryImage> mGallerys;
    private AsyncTask<Integer, Void, Void> mAsyncTask;
    private boolean mReload;
    private boolean mLoadMore;
    private int mWidhtImage;
    private ImageView ivMain;
    private static final int SELECT_IMAGE = 111;
    private static final int READ_REQUEST_CODE = 42;
    private FloatingActionButton fabPhoto, fabImportPhoto, fabFile, fabAddAlbum;
    int REQUEST_CODE_CAM = 123;
    int REQUEST_CODE_GALLERY = 124;
    private Context TheThis;
    private Bitmap bitmap ;
    private static Bitmap bitmap1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_main);
        findViewByIds();
        initComponents();
        setEvents();
        if (bitmap1 != null) {
            ivMain.setImageBitmap(bitmap1);
        }
        else {
            Picasso.with(getBaseContext())
                    .load(new File(mGallerys.get(0).getPathFile()))
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .resize(300, 300)
                    .into(ivMain);
        }

    }

    private void findViewByIds() {
        rcImage = (RecyclerView) findViewById(R.id.rc_img);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refres);
        ivMain = (ImageView) findViewById(R.id.iv_main);
        fabPhoto = (FloatingActionButton) findViewById(R.id.fab_cam);
        fabImportPhoto = (FloatingActionButton) findViewById(R.id.fab_photo);
        fabFile = (FloatingActionButton) findViewById(R.id.fab_file);
        fabAddAlbum = (FloatingActionButton) findViewById(R.id.fab_add);

    }

    private void initComponents() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidhtImage = displayMetrics.widthPixels / 3;


        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == mGallerys.size()) {
                    return 3;
                }
                return 1;
            }
        });
        galleryImageAdapter = new GalleryImageAdapter(this);
        rcImage.setLayoutManager(manager);
        rcImage.setAdapter(galleryImageAdapter);

        if (PermissionUtils.checkPermissionStore(this, 101)) {
            grantPermission();
        }
    }

    private void grantPermission() {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor == null) {
            return;
        }
        totalImage = cursor.getCount();
        cursor.close();

        mReload = true;
        refresh.setRefreshing(true);
        mGallerys = new ArrayList<>();
        loadMore(0);
    }


    private void setEvents() {
        refresh.setOnRefreshListener(this);

        fabAddAlbum.setOnClickListener(this);
        fabImportPhoto.setOnClickListener(this);
        fabFile.setOnClickListener(this);
        fabPhoto.setOnClickListener(this);

    }

    @Override
    public void onRefresh() {

        if (mReload || mLoadMore) {
            refresh.setRefreshing(false);
            return;
        }
        if (totalImage == 0) {
            refresh.setRefreshing(false);
            return;
        }
        mReload = true;
        loadMore(0);
        Picasso.with(getBaseContext())
                .load(new File(mGallerys.get(0).getPathFile()))
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .resize(300, 300)
                .into(ivMain);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public int getCount() {
        if (mGallerys == null) {
            return 0;
        }
        if (hasLoadMore()) {
            return mGallerys.size() + 1;
        }
        return mGallerys.size();
    }

    @Override
    public ItemGallaryImage getData(int position) {
        return mGallerys.get(position);
    }

    @Override
    public boolean hasLoadMore() {
        if (mGallerys.size() >= totalImage) {
            return false;
        }
        return true;
    }

    @Override
    public void loadMore() {
        if (mLoadMore || mReload) {
            return;
        }
        mLoadMore = true;
        loadMore(mGallerys.size());
        Picasso.with(getBaseContext())
                .load(new File(mGallerys.get(0).getPathFile()))
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .resize(300, 300)
                .into(ivMain);
    }

    public void loadMore(int current) {
        if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        mAsyncTask = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().encodedQuery("limit=" + params[0] + ","
                                + COUNT_OF_PAGE_DEFAULT_LOAD_GALLARY).build(),
                        new String[]{MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media._ID}, null, null,
                        MediaStore.Images.Media.DATE_ADDED + " DESC");
                if (cursor == null) return null;
                cursor.moveToFirst();
                String pathFile;
                PairInt pairInt;
                final int columnData = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                final int columnId = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                long id;
                if (mReload) {
                    mGallerys.clear();
                }
                while (!cursor.isAfterLast()) {
                    pathFile = cursor.getString(columnData);
                    if (pathFile != null && new File(pathFile).exists()) {
                        pairInt = BitmapUtils.calculateResizeImage(pathFile, mWidhtImage);
                        id = cursor.getLong(columnId);
                        mGallerys.add(new ItemGallaryImage(pairInt, pathFile, id));

                    }
                    cursor.moveToNext();
                }
                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mReload) {
                    mReload = false;
                    refresh.setRefreshing(false);
                } else {
                    mLoadMore = false;
                }

                galleryImageAdapter.notifyDataSetChanged();
            }
        };
        mAsyncTask.execute(current);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        if (requestCode == 101) {
            grantPermission();
        }
    }

    @Override
    public void onClickItemImage(int position) {
        Intent intent = new Intent();
        try {
            intent.putExtra(KEY_RETURE_GALLERY, mGallerys.get(position));
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {
        }

    }

    @Override
    protected void onDestroy() {
        if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fab_add:
                Toast.makeText(this, "ADD", Toast.LENGTH_SHORT).show();
                break;

            case R.id.fab_file:
                performFileSearch();
                break;

            case R.id.fab_photo:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, SELECT_IMAGE);

                break;

            case R.id.fab_cam:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, REQUEST_CODE_CAM);

                break;


            default:

        }
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
                        Log.d("TAGGG", bitmap.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE_CAM && resultCode == RESULT_OK && data != null) {
            bitmap1 = (Bitmap) data.getExtras().get("data");
            ivImage.setImageBitmap(bitmap1);
            SaveImage(this, bitmap1);

        }

        if (requestCode == 101) {
            if (PermissionUtils.checkPermissionStoreMediaNotShowDialog(this)) {
                grantPermission();
            }
        }

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("TAG", "Uri: " + uri.toString());
            }
        }
    }

    public void SaveImage(Context context, Bitmap ImageToSave) {
        TheThis = context;
        ivImage.setImageBitmap(ImageToSave);
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/DCIM/Camera";
        String CurrentDateAndTime = getCurrentDateAndTime();
        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "IMG_" + CurrentDateAndTime + ".jpg");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            ImageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MakeSureFileWasCreatedThenMakeAvabile(file);
            AbleToSave();
        } catch (FileNotFoundException e) {
            UnableToSave();
        } catch (IOException e) {
            UnableToSave();
        }
    }


    private void MakeSureFileWasCreatedThenMakeAvabile(File file) {
        MediaScannerConnection.scanFile(TheThis,
                new String[]{file.toString()},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private void UnableToSave() {
        Toast.makeText(TheThis, "Picture cannot to gallery", Toast.LENGTH_SHORT).show();
    }

    private void AbleToSave() {
        Toast.makeText(TheThis, "Picture saved be saved", Toast.LENGTH_SHORT).show();
    }

}