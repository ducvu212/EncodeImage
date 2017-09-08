package com.example.minhd.demoappimagelock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.minhd.demoappimagelock.Encrypt.EncryptMainActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.minhd.demoappimagelock.CustomDialog.name;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_IMAGE = 111;
    private static final int READ_REQUEST_CODE = 42;
    private FloatingActionButton fabPhoto, fabImportPhoto, fabFile, fabAddAlbum;
    private FloatingActionsMenu menu;
    int REQUEST_CODE_CAM = 123;
    int REQUEST_CODE_GALLERY = 124;
    public static ImageView ivImage;
    private Button ivMenu ;
    private LinearLayout lineaAlbum;
    private Context TheThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullAct();
        setContentView(R.layout.activity_main2);

        inits();
    }

    private void fullAct() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void inits() {
        fabPhoto = (FloatingActionButton) findViewById(R.id.fab_cam);
        fabImportPhoto = (FloatingActionButton) findViewById(R.id.fab_photo);
        fabFile = (FloatingActionButton) findViewById(R.id.fab_file);
        fabAddAlbum = (FloatingActionButton) findViewById(R.id.fab_add);
        final View menuItemView = findViewById(R.id.iv_menu);
        ivMenu = (Button) findViewById(R.id.iv_menu) ;
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(Main2Activity.this, ivMenu);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {


                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
        ivImage = (ImageView) findViewById(R.id.iv_photo);
        lineaAlbum = (LinearLayout) findViewById(R.id.album);

        fabAddAlbum.setOnClickListener(this);
        fabImportPhoto.setOnClickListener(this);
        fabFile.setOnClickListener(this);
        fabPhoto.setOnClickListener(this);

        ivImage.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fab_add:

                CustomDialog dialog = new CustomDialog(this) ;
                dialog.show();
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

            case R.id.iv_photo:

//                Intent iGallery = new Intent();
//                iGallery.setClass(getBaseContext(), GalleryActivity.class);
//                startActivityForResult(iGallery, REQUEST_CODE_GALLERY);
                Intent intent1 = new Intent(Main2Activity.this, EncryptMainActivity.class);
                startActivity(intent1);

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

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
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
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ivImage.setImageBitmap(bitmap);
            SaveImage(this, bitmap);

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
