package picture.diary.lenstofork.Diary;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import Entry.Entry;
import Entry.EntryHandler;
import picture.diary.lenstofork.Diary.Utils.DatabaseHandler;
import picture.diary.lenstofork.R;

public class NewEntryFragment extends Fragment {
    //widgets
    private ImageView entryImage;
    private TextView titleTxt;
    private TextView captionTxt;
    private Button submitBttn;

    //------- Variables
    private static EntryHandler handler;
    private String imageFilePath = "";
    // variables for choosing an image from the phone's gallery
    private File sourceFile;
    private File destinationFile;


    //------- Constants
    public static final String TAG = "New Entry Fragment";
    public static final String ARG_ENTRY_HANDLER = "Entry Handler";
    public static final int RESULT_CODE_CAMERA = 1;
    public static final int RESULT_CODE_GALLERY = 2;
    public static final int REQUEST_CODE_READ_PERMISSION = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_entry, container, false);

        // get extras from intents
        Bundle args = getArguments();
        String dateString = args.getString(ARG_ENTRY_HANDLER);
        getEntryHandler(dateString);

        //-------- Initialize Widgets
        entryImage = (ImageView) view.findViewById(R.id.img_entry);
        entryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureDialog();
            }
        });

        titleTxt = (TextView) view.findViewById(R.id.txt_entry_title);
        captionTxt = (TextView) view.findViewById(R.id.txt_entry_caption);

        submitBttn = (Button) view.findViewById(R.id.bttn_submit);
        submitBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new entry
                String title = titleTxt.getText().toString();
                String caption = captionTxt.getText().toString();
                Entry entry = new Entry(imageFilePath, title, caption);
                entry.getImageFilePath();

                // add new entry to database
                handler.addEntry(entry);
                handler.addEntry(entry);
                Entry[] entries = handler.getEntries();
                DatabaseHandler database = new DatabaseHandler(getContext());
                if(handler.getNumberOfEntries() == 1){
                    database.addEntries(handler);
                }
                else{
                    database.updateEntryHandler(handler);
                }

                // go back to main page
                Intent intent = DiaryActivity.newInstance(getActivity(), handler.getStringDate());
                startActivity(intent);
            }
        });

        return view;
    }

    //--------- Helper Methods

    /**
     * Gives the user to add a photo by either taking a picture or selecting one from their gallery
     */
    private void pictureDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_photo_picker);

        //------- Initialize Widgets
        // camera option
        ImageView cameraImg = dialog.findViewById(R.id.img_camera);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeNewPicture();
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeNewPicture();
                dialog.dismiss();
            }
        });

        // gallery option
        ImageView galleryImg = dialog.findViewById(R.id.img_gallery);
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
                dialog.dismiss();
            }
        });
        TextView galleryTxt = dialog.findViewById(R.id.txt_gallery);
        galleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Puts the image that is located at imageFilePath in the imageview
     */
    private void setImageInView() {
        // Get the dimensions of the View
        int targetW = entryImage.getWidth();
        int targetH = entryImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmOptions);
        entryImage.setImageBitmap(bitmap);
    }

    private void getEntryHandler(String dateString){
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        if(databaseHandler.doesEntryHandlerExist(dateString)){
            // get EntryHandler from database
            handler = databaseHandler.getEntryHandler(dateString);
        }
        else{
            // create EntryHandler for today
            handler = new EntryHandler(dateString);
        }
    }

    //--------- Select a New Image

    /**
     * Creates an empty file for the new image to go. It also creates the absolute file path and
     * stores it in the the class variable imageFilePath
     *
     * @return the file that the new image will be stored in
     * @throws IOException occurs if there is an IOException when creating a file
     */
    private File createImageFile() throws IOException{
        // create the image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // save the file's absolute file path
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    /**
     * Adds the new image to the gallery
     */
    private void addNewImageToGallery(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imageFilePath);
        Uri imageUri = Uri.fromFile(file);
        mediaScanIntent.setData(imageUri);
        getActivity().sendBroadcast(mediaScanIntent);
        setImageInView();
    }

    /**
     * Lets the user to take a picture using the phone's camera
     */
    private void takeNewPicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            // create file to hold image
            File imageFile = null;
            try{
                imageFile = createImageFile();
            } catch(IOException e){
                Log.e(TAG, "Failed to create image file: " + e.getMessage());
            }

            if(imageFile != null){
                Uri imageUri = FileProvider.getUriForFile(getContext(),
                        "lenstofork.android.fileprovider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, RESULT_CODE_CAMERA);
            }
        }
    }

    //-------- Select an Existing Image from Gallery
    /**
     * Lets the user choose a picture from their gallery
     */
    private void chooseImage(){
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(pickImageIntent, "SELECT FILE"),
                RESULT_CODE_GALLERY);
    }

    /**
     * Returns the absolute file path of the selected image.
     *
     * @param uri the uri that contains the data for the selected image
     * @return the absolute file path of the selected image
     */
    private String getAbsoluteFilePath(Uri uri){
        String result = null;
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, proj,
                null, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return result;
    }

    /**
     * Copies the image within the sourceFile to the destinationFile
     *
     * @param sourceFile the file to be copied
     * @param destinationFile the file to have the copied contents
     */
    private void copyImage(File sourceFile, File destinationFile){
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;

        try{
            sourceChannel = new FileInputStream(sourceFile).getChannel();
            destinationChannel = new FileOutputStream(destinationFile).getChannel();

            if(destinationChannel != null && sourceChannel != null){
                destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            }

            if(sourceChannel != null){
                sourceChannel.close();
            }
            if(destinationChannel != null){
                destinationChannel.close();
            }
        } catch (FileNotFoundException e){
            Log.e(TAG, "Original file does not exist: " + e.getMessage());
        } catch (IOException e){
            Log.e(TAG, "IOException: " + e.getMessage());
        }

    }


    //--------- Fragment Methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_CODE_CAMERA){
            addNewImageToGallery();
        }
        if(requestCode == RESULT_CODE_GALLERY){
            Uri imgUri = data.getData();
            destinationFile = null;
            sourceFile = null;
            try{
                destinationFile = createImageFile();

                // create destination file
                sourceFile = new File(getAbsoluteFilePath(imgUri));
                checksReadingPermission();
            } catch (IOException e){
                Log.e(TAG, "Failed to create new image file");
            }
        }
    }

    /**
     * Checks if the user has granted permissions to read from external storage. Results are
     * returned to onRequestPermissionResult.
     */
    private void checksReadingPermission(){
        // permission has not been granted
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_PERMISSION);
        }
        // permission has been granted
        else{
            // copy the image and place it within the ImageView
            copyImage(sourceFile, destinationFile);
            setImageInView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == REQUEST_CODE_READ_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // permission granted
                copyImage(sourceFile, destinationFile);
            }
            else{
                // permission denied
                /**
                 * Therefore, do not copy the file and make the the user responsible for store & not
                 * deleting their image. This means that there is nothing left to do.
                 */
                return;
            }
        }
    }

    public static NewEntryFragment newInstance(String entryHandlerDate){
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_HANDLER, entryHandlerDate);

        NewEntryFragment fragment = new NewEntryFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
