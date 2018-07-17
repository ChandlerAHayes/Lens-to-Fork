package picture.diary.lenstofork.Diary.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    /**
     * Resizes the image into a square image. The max dimensions are at most 1/2 of the given width
     * or 1/3 of the given width. Once the image has been resized and saved, image is inserted
     * into the given ImageView
     *
     * @param width the width of the DiaryFragment in pixels
     * @param height the height of the DiaryFragment in pixels
     * @param view the imageView of the current fragment (most likely NewEntryFragment
     */
    public void resizeAndInsertImage(int width, int height, final ImageView view){
        width *= 0.48;
        height *= 0.31;

        int minDimension = Math.min(width, height);

        Target customTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try{
                    // delete old file
                    new File(filepath).delete();

                    File resizedFile = createImageFile();
                    FileOutputStream outputStream = new FileOutputStream(resizedFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    // replace old file with newly resized file
                    filepath = resizedFile.getAbsolutePath();
                    imageFile = resizedFile;

                    // load resized image into ImageView
                    Picasso.get()
                            .load(new File(filepath))
                            .into(view);
                } catch(IOException e){
                    Log.e(tag, "Failed to overwrite file image: " + e.getMessage());
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        Picasso.get()
                .load(new File(filepath))
                .resize(minDimension, minDimension)
                .centerCrop()
                .into(customTarget);
    }

    /**
     * Puts the image that is located at imageFilePath in the ImageView
     *
     * Taken from: https://developer.android.com/training/camera/photobasics
     */
    public void setImageInView(ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // check if user didn't take picture
        if(photoH == -1 || photoW == -1){
            return;
        }


        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(filepath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    //-------- Setter & Getters
    public String getFilepath() {
        return filepath;
    }
}
