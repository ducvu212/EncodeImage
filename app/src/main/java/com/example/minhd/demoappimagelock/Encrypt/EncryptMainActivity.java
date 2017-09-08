package com.example.minhd.demoappimagelock.Encrypt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.minhd.demoappimagelock.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.minhd.demoappimagelock.Main2Activity.ivImage;


public class EncryptMainActivity extends Activity implements View.OnClickListener {

    static final int ACTIVITY_SELECT_IMAGE = 0;

    static final String TAG = "ImageHider";

    SharedPreferences mPreferences;
    static final String PASSWORD_HASH_PREF = "passwordhash";
    private AESImageAdapter mImageAdapter;
    private GridView mAdapterView;
    private final int PASSWORD_LENGTH = 16;
    private static final int SELECT_IMAGE = 111;
    private static final int READ_REQUEST_CODE = 42;
    private FloatingActionButton fabPhoto, fabImportPhoto, fabFile, fabAddAlbum;
    private FloatingActionsMenu menu;
    private final int REQUEST_CODE_CAM = 123;
    private Context TheThis;
    static int IMAGE_PREVIEW_HEIGHT_DP = 100;
    static int IMAGE_PREVIEW_WIDTH_DP = 100;

    int mImagePreviewHeightPixels;
    int mImagePreviewWidthPixels;

    private byte[] mPasswordBytes = {0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0};
    private String mStoredPasswordHash;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        ActionBar actionBar = getActionBar();
//        if (actionBar == null)
//            throw new NullPointerException("Action bar is null");
        fullAct();
        setContentView(R.layout.main);
        inits();

        mPreferences = getSharedPreferences("ImageHider", MODE_PRIVATE);

        mImagePreviewHeightPixels = (int) (IMAGE_PREVIEW_HEIGHT_DP * getResources().getDisplayMetrics().density + 0.5f);
        mImagePreviewWidthPixels = (int) (IMAGE_PREVIEW_WIDTH_DP * getResources().getDisplayMetrics().density + 0.5f);

        mAdapterView = (GridView) findViewById(R.id.gridGallery);
        mAdapterView.setColumnWidth(mImagePreviewWidthPixels);
        mAdapterView.setMinimumHeight(200);
        mAdapterView.setNumColumns(3);
        mAdapterView.setOnItemClickListener(new ImagePreviewClickListener());
        mAdapterView.setVerticalSpacing(8);
        mAdapterView.setHorizontalSpacing(8);
        mAdapterView.setGravity(Gravity.CENTER_HORIZONTAL);

        mHandler = new Handler();

        mImageAdapter = new AESImageAdapter(this, mPasswordBytes, mHandler,
                mImagePreviewHeightPixels, mImagePreviewWidthPixels);
        mAdapterView.setAdapter(mImageAdapter);

//        btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
//        btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
//            }
//        });

//		if (!mPreferences.contains(PASSWORD_HASH_PREF)) {
//			initialPasswordDialog();
//		} else {
//			mStoredPasswordHash = mPreferences.getString(PASSWORD_HASH_PREF, null);
//			passwordDialog();
//		}
    }

    private void inits() {
        fabPhoto = (FloatingActionButton) findViewById(R.id.fab_cam);
        fabImportPhoto = (FloatingActionButton) findViewById(R.id.fab_photo);
        fabFile = (FloatingActionButton) findViewById(R.id.fab_file);
        fabAddAlbum = (FloatingActionButton) findViewById(R.id.fab_add);

        fabAddAlbum.setOnClickListener(this);
        fabImportPhoto.setOnClickListener(this);
        fabFile.setOnClickListener(this);
        fabPhoto.setOnClickListener(this);
    }

    private void fullAct() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_encrypt_main, menu);
//        return true;
//    }

//	private void passwordDialog() {
//		PasswordDialogFragment passwordDialog = new PasswordDialogFragment(getString(R.string.password_instruction), false, new PasswordDialogFragment.PasswordDialogListener() {
//			public void onDialogPositiveClick(PasswordDialogFragment dialog) {
//				dialog.dismiss();
//				boolean passwordCorrect = false;
//				try {
//					passwordCorrect = Password.check(dialog.password(), mStoredPasswordHash);
//				} catch (Exception e) {}
//				if (passwordCorrect) {
//					mImageAdapter = new AESImageAdapter(MainActivity.this, mPasswordBytes, new Handler(),
//							mImagePreviewHeightPixels, mImagePreviewWidthPixels);
//					mAdapterView.setAdapter(mImageAdapter);
//				} else {
//					retryPasswordDialog();
//				}
//				mPasswordBytes = formatPassword(dialog.password());
//				mImageAdapter = new AESImageAdapter(MainActivity.this, mPasswordBytes, new Handler(),
//						mImagePreviewHeightPixels, mImagePreviewWidthPixels);
//				mAdapterView.setAdapter(mImageAdapter);
//			}
//		});
//		passwordDialog.show(getFragmentManager(), "PasswordDialogFragment");
//	}
//
//	private void retryPasswordDialog() {
//		PasswordDialogFragment passwordDialog = new PasswordDialogFragment(getString(R.string.retry_password_instruction), false, new PasswordDialogFragment.PasswordDialogListener() {
//			public void onDialogPositiveClick(PasswordDialogFragment dialog) {
//				dialog.dismiss();
//				boolean passwordCorrect = false;
//				try {
//					passwordCorrect = Password.check(dialog.password(), mStoredPasswordHash);
//				} catch (Exception e) {}
//				if (passwordCorrect) {
//					mImageAdapter = new AESImageAdapter(MainActivity.this, mPasswordBytes, new Handler(),
//							mImagePreviewHeightPixels, mImagePreviewWidthPixels);
//					mAdapterView.setAdapter(mImageAdapter);
//				} else {
//					retryPasswordDialog();
//				}
//				mPasswordBytes = formatPassword(dialog.password());
//				mImageAdapter = new AESImageAdapter(MainActivity.this, mPasswordBytes, new Handler(),
//						mImagePreviewHeightPixels, mImagePreviewWidthPixels);
//				mAdapterView.setAdapter(mImageAdapter);
//			}
//		});
//		passwordDialog.show(getFragmentManager(), "PasswordDialogFragment");
//	}
//
//	private void initialPasswordDialog() {
//		InitialPasswordDialogFragment passwordDialog = new InitialPasswordDialogFragment(getString(R.string.initial_password_instruction), false, new InitialPasswordDialogFragment.PasswordDialogListener() {
//			public void onDialogPositiveClick(InitialPasswordDialogFragment dialog) {
//				dialog.dismiss();
//				String saltedHash;
//				try {
//					saltedHash = Password.getSaltedHash(dialog.password());
//				} catch (Exception e) {
//					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//					builder.setMessage(R.string.hashing_error);
//					builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							MainActivity.this.finish();
//						}
//					});
//					builder.create().show();
//					return;
//				}
//				mPreferences.edit().putString(PASSWORD_HASH_PREF, saltedHash).commit();
//				mPasswordBytes = formatPassword(dialog.password());
//				mImageAdapter = new AESImageAdapter(MainActivity.this, mPasswordBytes, new Handler(),
//						mImagePreviewHeightPixels, mImagePreviewWidthPixels);
//				mAdapterView.setAdapter(mImageAdapter);
//			}
//		});
//		passwordDialog.show(getFragmentManager(), "PasswordDialogFragment");
//	}
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Open Android's image chooser activity, to allow
//        // the user to choose an image
//        if (item.getItemId() == R.id.add) {
//            Intent i = new Intent(Intent.ACTION_PICK,
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
//            return true;
//        } else if (item.getItemId() == R.id.quit) {
//            finish();
//            return true;
//        }
//        return false;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPasswordBytes = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            // the user has chosen an image to encrypt
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    final String newFilePath = AESManager.encryptedFolderString() + filePath;
                    Log.d("TAGGGGGGGGGGGG", newFilePath);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(String.format(getString(R.string.confirm_encrypt), filePath));
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AESManager.encrypt(mPasswordBytes, filePath, newFilePath, EncryptMainActivity.this, null);
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, null);
                    builder.create().show();
                }

                break;

            case SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    if (imageReturnedIntent != null) {
                        try {

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(),
                                    imageReturnedIntent.getData());
                            Log.d("TAGGG", bitmap.toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_CAM:
                if (resultCode == RESULT_OK && imageReturnedIntent != null) {
                    File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                    Bitmap bitmap1 = decodeSampledBitmapFromFile(file.getAbsolutePath(), 3000, 1400);
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ivImage.setImageBitmap(bitmap1);
                    SaveImage(this, bitmap1);

                }

                break;

            case  READ_REQUEST_CODE:
                if( resultCode == Activity.RESULT_OK) {
                // The document selected by the user won't be returned in the intent.
                // Instead, a URI to that document will be contained in the return intent
                // provided to this method as a parameter.
                // Pull that URI using resultData.getData().
                Uri uri = null ;
                if (imageReturnedIntent != null) {
                    uri = imageReturnedIntent.getData();
                    Log.i("TAG", "Uri: " + uri.toString());
                }
            }

            default:
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    class ImagePreviewClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String filename = (String) mImageAdapter.getItem(position);
//            File file = new File(filename);
//            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
//            Uri data = Uri.parse("file://" + file.getAbsolutePath());
//            intent.setDataAndType(data, "image/*");
//            startActivity(intent);

            Intent imageIntent = new Intent(EncryptMainActivity.this, ImageActivity.class);
            imageIntent.setAction(Intent.ACTION_VIEW);
            imageIntent.putExtra(ImageActivity.BUNDLE_FILENAME, filename);
            imageIntent.putExtra(ImageActivity.BUNDLE_PASSWORD, mPasswordBytes);
            startActivity(imageIntent);

        }

    }

    public byte[] formatPassword(String password) {
        byte[] passwordBytes = password.getBytes();
        byte[] newPassword = new byte[16];
        if (passwordBytes.length > PASSWORD_LENGTH) {
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                newPassword[i] = passwordBytes[i];
            }
        } else {
            for (int i = 0; i < passwordBytes.length; i++) {
                newPassword[i] = passwordBytes[i];
            }
            for (int i = passwordBytes.length; i < PASSWORD_LENGTH; i++) {
                newPassword[i] = 0;
            }
        }
        return newPassword;
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
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);

                break;

            case R.id.fab_cam:
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + ".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                Log.d("TAGGGGG", "file " + file) ;
                startActivityForResult(intent, REQUEST_CODE_CAM);

                break;

            case R.id.iv_menu:

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
            ImageToSave.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
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

