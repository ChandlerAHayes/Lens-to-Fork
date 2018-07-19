package picture.diary.lenstofork.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHandler {
    private Activity activity;
    private String tag;

    // file variables
    private String filepath = "";
    private File imageFile; // the app's copy of the image to be added

    //------- Constants
    public static final int RESULT_CODE_CAMERA = 1;
    public static final int RESULT_CODE_GALLERY = 2;

    public ImageHandler(Activity activity, String tag){
        this.activity = activity;
        this.tag = tag;
    }


    //--------- Select a New Image Using Device's Camera

    /**
     * Starts the processing of taking a new picture using the device's camera
     *
     * @param fragment the fragment calling the method
     */
    public void takeNewPicture(Fragment fragment){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(activity.getPackageManager()) != null){
            imageFile = createImageFile();
            if(imageFile != null){
                Uri imageUri = FileProvider.getUriForFile(fragment.getContext(),
                        "lenstofork.android.fileprovider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                fragment.startActivityForResult(takePictureIntent, RESULT_CODE_CAMERA);
            }
        }
    }

    /**
     * Adds the new image to the gallery
     */
    public void addNewImageToGallery(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filepath);
        Uri imageUri = Uri.fromFile(file);
        mediaScanIntent.setData(imageUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    //--------- Select an Image Using Device's Gallery

    /**
     * Starts the process of selecting an image from the device's gallery
     *
     * @param fragment the fragment calling this method
     */
    public void selectImage(Fragment fragment){
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(Intent.createChooser(pickImageIntent, "SELECT FILE"),
                RESULT_CODE_GALLERY);
    }

    /**
     * Handles the data that is given when the user selects an image from the gallery and handles
     * copying of the file into the app's folder
     *
     * @param imgUri the uri containing the image the user selected
     * @param context context of the fragment/activity calling it
     * @param canCopyImages true if the app can copy the image, false otherwise
     */
    public void handleGalleryResults(Uri imgUri, Context context, boolean canCopyImages){
        if(canCopyImages){
            File sourceFile = new File(getAbsoluteFilePath(imgUri, context));
            File destinationFile = createImageFile();
            copyImage(sourceFile, destinationFile);
        }
        else{
            // can't copy the image into app folders. Therefore, store image's original filepath
            filepath = getAbsoluteFilePath(imgUri, context);
        }
    }

    /**
     * Returns the absolute file path of the selected image.
     *
     * @param uri the uri that contains the data for the selected image
     * @return the absolute file path of the selected image
     */
    private String getAbsoluteFilePath(Uri uri, Context context){
        String filepath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null,
                null);

        // extract filepath
        if(cursor != null){
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndexOrThrow(proj[0]);
                filepath = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return filepath;
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
            Log.e(tag, "Original file does not exist: " + e.getMessage());
        } catch (IOException e){
            Log.e(tag, "IOException: " + e.getMessage());
        }
    }

    //--------- Helpers

    /**
     * Creates an empty file to contain the new image that the user either selects from the gallery
     * or from taking a new picture using the device's camera
     *
     * @return the newly created file to contain the selected image
     */
    public File createImageFile(){
        // create the image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // create the file
        File image = null;
        try{
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            filepath = image.getAbsolutePath();
        } catch(IOException e){
            Log.e(tag, "Failed to created new image file: " + e.getMessage());
        }


        return image;
    }


    public void loadIntoImageView(int width, int height, String filepath, ImageView view){
        Picasso.get()
                .load(new File(filepath))
                .resize(width, height)
                .centerCrop()
                .into(view);
    }

    //-------- Setter & Getters
    public String getFilepath() {
        return filepath;
    }
}
