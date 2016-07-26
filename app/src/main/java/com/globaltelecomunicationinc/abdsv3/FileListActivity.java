package com.globaltelecomunicationinc.abdsv3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;
    final int GALLERY_REQUEST = 22131;
    private String selectedImagePath;
    String selectedPhoto;

    SharedPreferences prefs = null;
    int mode;

    ImageView ivGallery;
    GalleryPhoto galleryPhoto;
    TextView tvFileListLabel;
    Button btnEncryptedFiles, btnDencryptedFiles, btnMusic, btnVideos, btnAllFiles, btnEncryptDecrypt;

    public void initViews() {
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        ivGallery = (ImageView) findViewById(R.id.ivGallery);
        tvFileListLabel = (TextView) findViewById(R.id.tvFileListLabel);

        btnEncryptedFiles = (Button) findViewById(R.id.btnEncryptedFiles);
        btnDencryptedFiles = (Button) findViewById(R.id.btnDencryptedFiles);
        btnMusic = (Button) findViewById(R.id.btnMusic);
        btnVideos = (Button) findViewById(R.id.btnVideos);
        btnAllFiles = (Button) findViewById(R.id.btnAllFiles);
        btnEncryptDecrypt = (Button) findViewById(R.id.btnEncryptDecrypt);
    }

    //set Listeners
    private void setListeners() {
        ivGallery.setOnClickListener(this);
        btnEncryptedFiles.setOnClickListener(this);
        btnDencryptedFiles.setOnClickListener(this);
        btnMusic.setOnClickListener(this);
        btnVideos.setOnClickListener(this);
        btnAllFiles.setOnClickListener(this);
        btnEncryptDecrypt.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        initViews();
        setListeners();

        prefs = this.getSharedPreferences("com.globaltelecomunicationinc.abdsv3", MODE_PRIVATE);
        btnEncryptDecrypt.setText("Decrypt");
        mode = 2;

        tvFileListLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        if (mode == 2) {
            tvFileListLabel.setText("All " + prefs.getString("username", "") + "'s Decrypted files");
            btnEncryptDecrypt.setText("Encrypt");
        } else {
            tvFileListLabel.setText("All " + prefs.getString("username", "") + "'s Encrypted files");
            btnEncryptDecrypt.setText("Decrypt");
        }

        /*ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEncryptedFiles:
                mode = 1;
                tvFileListLabel.setText("All " + prefs.getString("username", "") + "'s Encrypted files");
                btnEncryptDecrypt.setText("Decrypt");
                break;
            case R.id.btnDencryptedFiles:
                mode = 2;
                tvFileListLabel.setText("All " + prefs.getString("username", "") + "'s Decrypted files");
                btnEncryptDecrypt.setText("Encrypt");
                break;
            case R.id.ivGallery:
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
                //TODO: copy selected file to selected for encryption folder in the ABDSv2 if mode is 2(decrypted files seen)
                break;
            case R.id.btnMusic:
                /*try {
                    Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                    startActivity(intent);
                }
                    catch (ActivityNotFoundException anfe) {
                        // Handle no component found here (e.g. show a toast or dialog)
                    }*/
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
                //TODO: copy selected file to selected for encryption folder in the ABDSv2 if mode is 2(decrypted files seen)
                break;
            case R.id.btnVideos:
                Intent photoLibraryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoLibraryIntent.setType("video/*");
                photoLibraryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(photoLibraryIntent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);

//                startActivityForResult(photoLibraryIntent, 1);
                //TODO: copy selected file to selected for encryption folder in the ABDSv2 if mode is 2(decrypted files seen)
                break;
            case R.id.btnAllFiles:
                //Set all files for encryption
                if(isExternalStorageAvailable()){
                    Toast.makeText(getApplicationContext(),
                            "SD card present", Toast.LENGTH_SHORT
                    ).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "NO SDcard", Toast.LENGTH_SHORT
                    ).show();
                }
                break;
            case R.id.btnEncryptDecrypt:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileListActivity.this);
                alertDialog.setTitle("PASSWORD");
                alertDialog.setMessage("Enter Password");

                final EditText input = new EditText(FileListActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //alertDialog.setView(input); // uncomment this line
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.key);

                alertDialog.setPositiveButton("Enter",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String password = input.getText().toString();
                                //if (password.matches("")) {
                                if (prefs.getString("password", "").matches(password)) {
                                    File filepath = Environment.getExternalStorageDirectory();
                                    File downloadpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                    //file name that the images will be saved under
                                    String inputPath = filepath.getAbsolutePath().toString() + "/ABDStoencode/";

                                    String outputPath = downloadpath.getAbsolutePath().toString() + "/";

                                    String inputFile = "myimage.png";
                                    //moveFile(inputPath,  inputFile, outputPath);
                                    if(mode == 2){
                                        encryptFile(inputPath, inputFile, outputPath);
                                    }else if(mode == 1){
                                        decryptFile(inputPath, inputFile, outputPath);
                                    }else{
                                        Toast.makeText(getApplicationContext(),
                                                "Mode not set!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                               /* }else {
                                    Toast.makeText(getApplicationContext(),
                                            "No Password Entered!", Toast.LENGTH_SHORT).show();
                                }*/
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();


               /* if (prefs.getString("password", "").matches(LoginPassword)) {
                    File filepath = Environment.getExternalStorageDirectory();
                    File downloadpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    //file name that the images will be saved under
                    String inputPath = filepath.getAbsolutePath().toString() + "/ABDStoencode/";

                    String outputPath = downloadpath.getAbsolutePath().toString() + "/";

                    String inputFile = "myimage.png";
                    //moveFile(inputPath,  inputFile, outputPath);
                    if(btnEncryptDecrypt.getText().toString().matches("ENCRYPT")){
                        encryptFile(inputPath, inputFile, outputPath);
                    }else{
                        decryptFile(inputPath, inputFile, outputPath);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "passwords did not match", Toast.LENGTH_SHORT
                    ).show();
                }*/
                break;
            default:
                finish();
                break;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable == true
                && mExternalStorageWriteable == true) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        OutputStream output;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    Uri selectedImageUri = data.getData();
                    String photoPath = getPath(selectedImageUri).toString();
                    selectedPhoto = photoPath;
                /*Uri photoPath = galleryPhoto.getPhotoUri();
                selectedPhoto = photoPath;
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                selectedPhoto = galleryPhoto.getPath();*/
                    try{
                        bitmap = ImageLoader.init().from(selectedPhoto).requestSize(512, 512).getBitmap();
                        //ivImage.setImageBitmap(bitmap);
                        File filepath = Environment.getExternalStorageDirectory();
                        //file name that the images will be saved under
                        File dir = new File(filepath.getAbsolutePath() + "/ABDSv3toencode");
                        dir.mkdirs();

                        File file = new File(dir, "myimage.png");
                        File f = new File(photoPath);

                        String imageName = f.getName();

                        Toast.makeText(getApplicationContext(),
                                "Image saved to SD Card", Toast.LENGTH_SHORT
                        ).show();
                        try{
                            output = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                            output.flush();
                            output.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),
                                "Something went wrong while choosing photos", Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    Log.i("No Gallery", "request");
                }
                break;
            case REQUEST_TAKE_GALLERY_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    String videoPath = getPath(selectedImageUri).toString();
                    Log.i("In put path",videoPath);
                    try{
                        // OI FILE Manager
                        String filemanagerstring = selectedImageUri.getPath();
                        Log.i("Input file Manager",filemanagerstring);
                        // MEDIA GALLERY
                        selectedImagePath = getPath(selectedImageUri);
                        if (selectedImagePath != null) {
                            File filepath = Environment.getExternalStorageDirectory();
                            String inputPath = filepath.getAbsolutePath().toString() + "/ABDSv3toencode/";

                            File downloadpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            String outputPath = downloadpath.getAbsolutePath().toString() + "/";
                            Log.i("Output path",inputPath);
                            moveFile( videoPath, filemanagerstring,  inputPath);
                            /*Intent intent = new Intent(FileListActivity.this,
                                    VideoplayAvtivity.class);
                            intent.putExtra("path", selectedImagePath);
                            startActivity(intent);*//*

                            File filepath = Environment.getExternalStorageDirectory();
                            //file name that the images will be saved under
                            File dir = new File(filepath.getAbsolutePath() + "/ABDSv3toencode");
                            dir.mkdirs();

                            File file = new File(dir, "myvideo.mp4");
                            File f = new File(filemanagerstring);

                            String imageName = f.getName();*/
                        }else{
                            Log.i("No Video", "request");
                        }

                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(),
                                "Something went wrong while choosing video", Toast.LENGTH_SHORT
                        ).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong while making video request", Toast.LENGTH_SHORT
                    ).show();
                }


                break;
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    void encryptFile(String inputPath, String inputFile, String outputPath) {
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            // Here you read the cleartext.
            FileInputStream fis = new FileInputStream(inputPath +"/"+ inputFile);
            // This stream write the encrypted text. This stream will be wrapped by another stream.
            FileOutputStream fos = new FileOutputStream(outputPath +"/"+ inputFile);

            // Length is 16 byte
            SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
            // Create cipher
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            // Write bytes
            int b;
            byte[] d = new byte[8];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();
            Toast.makeText(getApplicationContext(),
                    "files encrypted and saved to SD card", Toast.LENGTH_LONG
            ).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "Something went wrong encrypting", Toast.LENGTH_SHORT
            ).show();
            Log.e("tag", e.getMessage());
        }
    }

    void decryptFile(String inputPath, String inputFile, String outputPath){
        try {
            FileInputStream fis = new FileInputStream(outputPath +"/"+ inputFile);

            FileOutputStream fos = new FileOutputStream(inputPath +"/decrypted" + inputFile );
            SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[8];
            while((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();
            Toast.makeText(getApplicationContext(),
                    "files decrypted and saved to SD card", Toast.LENGTH_LONG
            ).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "Something went wrong decrypting", Toast.LENGTH_SHORT
            ).show();
            Log.e("tag", e.getMessage());
        }
    }

    void moveFile(String inputPath, String inputFile, String outputPath){
        /*// Here you read the cleartext.
        FileInputStream fis = new FileInputStream("data/cleartext");
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream("data/encrypted");

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();*/
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            //new File(inputPath + inputFile).delete();
        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }
    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/

    /*public void ListDir(File f){
        List<String> fileList = new ArrayList<String>();
        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files){
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        setListAdapter(directoryList);
    }*/

}