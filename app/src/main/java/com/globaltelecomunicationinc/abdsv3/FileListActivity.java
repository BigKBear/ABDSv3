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

import static android.R.id.input;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener {

    //Set the request codes for the results returned from Photos, Videos, Music and All files
    final int GALLERY_REQUEST = 22131;
    final int REQUEST_TAKE_GALLERY_VIDEO = 42831;
    final int REQUEST_GET_MUSIC = 1;
    final int FILE_SELECT_CODE = 22341;

    //global variable for the selected file NAME for encryption of decryption
    String selectedFileName ="";
    //global variable for the selected file PATH for encryption of decryption
    private String selectedFilePath;

    SharedPreferences prefs = null;
    //Lets us know if we are in encryption mode or decryption mode
    int mode = 2;

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

        Environment.getExternalStorageDirectory();

        File folder1 = new File(Environment.getExternalStorageDirectory() + "/map");
        boolean success = true;
        if (!folder1.exists()) {
            success = folder1.mkdir();
        }
        if (success) {
            // Do something on success
            Toast.makeText(getApplicationContext(),"SDcard Folder created "+ folder1.getPath(),Toast.LENGTH_LONG).show();
        } else {
            // Do something else on failure
            Toast.makeText(getApplicationContext(),"SDcard Folder ALREADY exists ",Toast.LENGTH_LONG).show();
        }

        //First check if the sd card is mounted or not
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            // sd card mounted
            Log.d("SDCard", "SDcard is present");
            Toast.makeText(getApplicationContext(),"SDcard is present",Toast.LENGTH_LONG).show();
        }

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator +"ABDSv3/");
        if(!folder.exists())

        {
            folder.mkdirs();
            Log.d("SDcard", "Folder created");
            Toast.makeText(getApplicationContext(),"SDcard Folder created "+ folder.getPath(),Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d("SDCard", "Folder already exists");
            Toast.makeText(getApplicationContext(),"SDcard Folder ALREADY exists "+ folder.getPath(),Toast.LENGTH_LONG).show();
        }

        prefs = this.getSharedPreferences("com.globaltelecomunicationinc.abdsv3", MODE_PRIVATE);
        btnEncryptDecrypt.setText("Decrypt");

        tvFileListLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        if (mode == 2) {
            tvFileListLabel.setText("All " + prefs.getString("username", "") + "'s Decrypted files");
            btnEncryptDecrypt.setText("Encrypt");
        } else if(mode ==1 ) {
            tvFileListLabel.setText("All " + prefs.getString("username", "") + "'s Encrypted files");
            btnEncryptDecrypt.setText("Decrypt");
        }else{
            Toast.makeText(getApplicationContext(),
                    "Mode not set restart the app!", Toast.LENGTH_SHORT).show();
        }
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
                if(mode == 2){
                    startActivityForResult((galleryPhoto.openGalleryIntent()), GALLERY_REQUEST);
                    //TODO: copy selected file to selected for encryption folder in the ABDSv2 if mode is 2(decrypted files seen)
                }else if(mode == 1){
                    //Open encrypted Photos only
                    Toast.makeText(getApplicationContext(),
                            "Show encrypted Photos only", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Mode not set!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnMusic:
                if(mode == 2){
                    Intent musicLibraryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(musicLibraryIntent.createChooser(musicLibraryIntent,"Select Music file to Encrypt"), REQUEST_GET_MUSIC);
                    //TODO: copy selected file to selected for encryption folder in the ABDSv2 if mode is 2(decrypted files seen)
                }else if(mode == 1){
                    //Open encrypted Music only
                    Toast.makeText(getApplicationContext(),
                            "Show encrypted Music only", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Mode not set!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnVideos:
                if(mode == 2){
                    //Intent photoLibraryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent videoLibraryIntent = new Intent();
                    videoLibraryIntent.setType("video/*");
                    videoLibraryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    try{
                        startActivityForResult(videoLibraryIntent.createChooser(videoLibraryIntent,"Select Video file to Encrypt"),REQUEST_TAKE_GALLERY_VIDEO);
                        //TODO: copy selected file to selected for encryption folder in the ABDSv2 if mode is 2(decrypted files seen)
                    }catch(android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog
                        Toast.makeText(this, "Please install a File Manager.",
                                Toast.LENGTH_SHORT).show();
                    }
                }else if(mode == 1){
                    //Open encrypted Videos only
                    Toast.makeText(getApplicationContext(),
                            "Show encrypted Videos only", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Mode not set!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnAllFiles:
                if(mode == 2){
                    //Set all files for encryption
                    if(isExternalStorageAvailable()){
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select a File to Encrypt"),
                                    FILE_SELECT_CODE);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a Dialog
                            Toast.makeText(this, "Please install a File Manager.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "NO SDcard Present", Toast.LENGTH_SHORT
                        ).show();
                    }
                }else if(mode == 1){
                    //Open encrypted folder with files of each type
                    Toast.makeText(getApplicationContext(),
                            "Show all encrypted Files only", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Mode not set!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnEncryptDecrypt:
                File folder = new File(Environment.getExternalStorageDirectory() + File.separator +"ABDSv3/");
                // create a File object for the parent directory
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString()+"/ABDSv3");
                // have the object build the directory structure, if needed.
                wallpaperDirectory.mkdirs();

                if(!wallpaperDirectory.exists())
                {if (!wallpaperDirectory.mkdirs()) {

                    Toast.makeText(getApplicationContext(),"SDcard Folder"+
                            Environment.getExternalStorageDirectory() + File.separator +"ABDSv3/"
                            +"Does not exist.",Toast.LENGTH_LONG).show();
                }}else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileListActivity.this);
                    alertDialog.setTitle("PASSWORD");
                    alertDialog.setMessage("Enter Password");

                    final EditText input = new EditText(FileListActivity.this);
                    //final EditText file_name = new EditText(FileListActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);


                    //file_name.setLayoutParams(lp);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //file_name.setInputType(InputType.TYPE_CLASS_TEXT);
                    //alertDialog.setView(input); // uncomment this line
                    alertDialog.setView(input);
                    //alertDialog.setView(file_name);
                    alertDialog.setIcon(R.drawable.key);

                    alertDialog.setPositiveButton("Enter",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String password = input.getText().toString();
                                    //String filename = file_name.getText().toString();
                                    //if (password.matches("")) {
                                    if (prefs.getString("password", "").matches(password)) {
                                        File filepath = Environment.getExternalStorageDirectory();
                                        File downloadpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                        //file name that the images will be saved under
                                        String inputPath = filepath.getAbsolutePath().toString() + "/ABDSv3";

                                        String outputPath = downloadpath.getAbsolutePath().toString() + "/";

                                        String inputFile = selectedFileName;//TODO:ask for file name to save file as from the user
                                        //String inputFile = filename;
                                        //moveFile(inputPath,  inputFile, outputPath);
                                        if (mode == 2) {
                                            encryptFile(inputPath, inputFile, outputPath + "/Encrypted/");
                                        } else if (mode == 1) {
                                            decryptFile(outputPath, inputFile, outputPath + "/Decrypted/");
                                        } else {
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
                }
                break;
            default:
                finish();
                break;
        }
    }

    public void CheckIfFileExistAndCreateIT(File file){
        boolean success = true;
        if (!file.exists()) {
            success = file.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        OutputStream output;
        switch (requestCode) {
            // Displays all the photos a user has on their device for them to select one
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    if(mode == 2){
                        Bitmap bitmap = null;
                        Uri selectedImageUri = data.getData();
                        String photoPath = getPath(selectedImageUri).toString();
                        selectedFileName = photoPath;
                        try{
                            bitmap = ImageLoader.init().from(selectedFileName).requestSize(512, 512).getBitmap();
                            //ivImage.setImageBitmap(bitmap);
                            File filepath = Environment.getExternalStorageDirectory();
                            Log.d("FileListActivity", "Can write: " + Environment.getExternalStorageDirectory().canWrite());
                            //file name that the images will be saved under
                            File dir = new File(filepath.getAbsolutePath() +"/ABDSv3");
                            dir.mkdirs();


                            //File file = new File(dir, getFileName());
                            File f = new File(photoPath);

                            File file = new File(dir, f.getName());

                            selectedFileName = f.getName();

                            Toast.makeText(getApplicationContext(),
                                    "Image saved to SD Card" + selectedFileName+ "  "+ file.canRead()+ "  "+file.canWrite(), Toast.LENGTH_SHORT
                            ).show();
                            //Saves the selected image to the ABDSv3 folder
                            try{
                                output = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                                output.flush();
                                output.close();
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while saving the photo", Toast.LENGTH_SHORT
                                ).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong while selecting the photo", Toast.LENGTH_SHORT
                            ).show();
                        }
                    }else if(mode == 1){
                        //Open encrypted Photos only

                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Mode not set!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("No Gallery", "request");
                }
                break;
            // Displays all the music a user has on their device for them to select one
            case REQUEST_GET_MUSIC:
                if (resultCode == RESULT_OK) {
                    if(mode == 2){
                        Uri selectedMusicUri = data.getData();
                        selectedFilePath = getPath(selectedMusicUri);
                        try{
                            if (selectedFilePath != null) {
                                File f = new File(selectedFilePath);
                                selectedFileName = f.getName();
                                Toast.makeText(getApplicationContext(),
                                        "Music " + selectedFileName + " selected", Toast.LENGTH_SHORT
                                ).show();
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "You have not selected a file with a valid location.", Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong while selecting the music", Toast.LENGTH_SHORT
                            ).show();
                        }
                    }else if(mode == 1){
                        //Open encrypted Music only
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Mode not set!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.i("No Music", "request");
                }
                break;

            // Displays all the videos a user has on their device for them to select one
            case REQUEST_TAKE_GALLERY_VIDEO:
                if (resultCode == RESULT_OK) {
                    if(mode == 2){
                        Uri selectedVideoUri = data.getData();

                        // OI FILE Manager
                        String filemanagerstring = selectedVideoUri.getPath();

                        // MEDIA GALLERY
                        selectedFilePath = getPath(selectedVideoUri);
                        try{
                            if (selectedFilePath != null) {
                                File f = new File(selectedFilePath);
                                selectedFileName = f.getName();
                                Toast.makeText(getApplicationContext(),
                                        "Video " + selectedFileName + " selected", Toast.LENGTH_SHORT
                                ).show();
                            }else{

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong while selecting the video", Toast.LENGTH_SHORT
                            ).show();
                        }
                    }else if(mode == 1){
                        //Open encrypted Video only

                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Mode not set!", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Log.i("No Video", "request");
                }
                break;

            //use the File explorer the user has on their device to select a file for encryption or decryption for them to select one
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    if(mode == 2){
                        try{
                            // Get the Uri of the selected file
                            Uri uri = data.getData();
                            Log.e("TAG", "File Uri: " + uri.toString());
                            // Get the path
                            selectedFilePath = getPath(uri);
                            Log.e("TAG", "File Path: " + selectedFilePath);
                            if (selectedFilePath != null) {
                                // Get the file instance
                                File f = new File(selectedFilePath);
                                selectedFileName = f.getName();
                                Toast.makeText(getApplicationContext(),
                                        "User selected " + selectedFileName + " file from file explorer.", Toast.LENGTH_SHORT
                                ).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong while selecting the video", Toast.LENGTH_SHORT
                            ).show();
                        }
                    }else if(mode == 1){
                        //Open encrypted files folder only
                        Toast.makeText(getApplicationContext(),
                                "Write code to open encrypted files folder", Toast.LENGTH_SHORT
                        ).show();
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Mode not set!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("No File", "request");
                }
                break;
        }
    }

    /*
    * Function that checks if external storage is available on the user device
    */
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

    /*
     * Function to retrieve the path of an image URI
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

    /*
    *function created to encrypt a file once given the input path, file name, and output path
     */
    void encryptFile(String inputPath,String inputFile, String outputPath) {
        try {
            //Checks if an empty string has been passed to the function
            if (inputFile == null){
                Toast.makeText(getApplicationContext(),
                        "No file name was given", Toast.LENGTH_SHORT
                ).show();
            }else if (inputPath == null) {
                Toast.makeText(getApplicationContext(),
                    "No input path was provided", Toast.LENGTH_SHORT
                ).show();
            }else if(outputPath == null){

                Toast.makeText(getApplicationContext(),
                        "No output path was provided", Toast.LENGTH_SHORT
                ).show();
            }else{
                //create output directory if it doesn't exist
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // Here you read the cleartext.
                FileInputStream fis = new FileInputStream(inputPath + inputFile);
                // This stream write the encrypted text. This stream will be wrapped by another stream.
                FileOutputStream fos = new FileOutputStream(outputPath + inputFile);

                // Length is 16 byte
                SecretKeySpec secretKey = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
                // Create cipher
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
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
                        "file " + inputFile + " encrypted and saved to SD card", Toast.LENGTH_LONG
                ).show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "Something went wrong encrypting", Toast.LENGTH_SHORT
            ).show();
            Log.e("tag", e.getMessage());
        }
    }

    void decryptFile(String inputPath, String inputFile, String outputPath){
        try {
            FileInputStream fis = new FileInputStream(outputPath + inputFile);

            FileOutputStream fos = new FileOutputStream(inputPath + inputFile );
            SecretKeySpec secretKey = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
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
                    "file "+ inputFile +" decrypted and saved to SD card", Toast.LENGTH_LONG
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

    /*
    public String getFileName(){
        final String[] password = {""};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileListActivity.this);
        alertDialog.setTitle("File name?");
        alertDialog.setMessage("Enter File name you want to save the file as");

        final EditText input = new EditText(FileListActivity.this);
        //final EditText file_name = new EditText(FileListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);


        //file_name.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //file_name.setInputType(InputType.TYPE_CLASS_TEXT);
        //alertDialog.setView(input); // uncomment this line
        alertDialog.setView(input);
        //alertDialog.setView(file_name);
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Enter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        password[0] = input.getText().toString();
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
        return password[0];
    }*/


    /*
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    */

    /*
    public void ListDir(File f){
        List<String> fileList = new ArrayList<String>();
        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files){
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        setListAdapter(directoryList);
    }
    */
}