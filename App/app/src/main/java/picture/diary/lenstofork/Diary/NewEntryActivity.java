package picture.diary.lenstofork.Diary;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import Entry.CaptionColor;
import Entry.Entry;
import Entry.EntryHandler;
import picture.diary.lenstofork.R;
import picture.diary.lenstofork.Utils.DatabaseHandler;
import picture.diary.lenstofork.Utils.ImageHandler;

public class NewEntryActivity extends AppCompatActivity {
    //widgets
    private ImageView entryImage;
    private EditText titleTxt;
    private EditText captionTxt;
    private EditText descriptionTxt;
    private Button submitBttn;
    private ImageView imgColorCaption;

    //------- Variables
    private static EntryHandler entryHandler;
    private ImageHandler imageHandler;
    private String imageFilePath = "";
    private boolean canCopyImages = false;
    private DatabaseHandler database;
    private CaptionColor captionColor = CaptionColor.WHITE;

    //------- Constants
    public static final String TAG = "New Entry Activity";
    public static final String EXTRA_ENTRY_HANDLER = "Entry Handler";
    public static final int REQUEST_CODE_READ_PERMISSION = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        checksReadingPermission();

        //------ initialize database, entryHandler, imageHandler
        imageHandler = new ImageHandler(this, TAG);
        database = new DatabaseHandler(getApplicationContext());
        String dateString = getIntent().getStringExtra(EXTRA_ENTRY_HANDLER);
        getEntryHandler(dateString);

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.up_navigation_white);

        //-------- Initialize Widgets
        entryImage = (ImageView) findViewById(R.id.img_entry);
        entryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOptionsDialog();
            }
        });

        titleTxt = (EditText) findViewById(R.id.txt_entry_title);
        captionTxt = (EditText) findViewById(R.id.txt_entry_caption);
        descriptionTxt = (EditText) findViewById(R.id.txt_entry_description);

        submitBttn = (Button) findViewById(R.id.bttn_submit);
        submitBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEntry();
            }
        });

        imgColorCaption = (ImageView) findViewById(R.id.img_caption_color);
        imgColorCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle between white and black font for the caption color
                if(captionColor.equals(CaptionColor.WHITE)){
                    // switch to black font
                    captionColor = CaptionColor.BLACK;
                    imgColorCaption.setImageResource(R.drawable.colored_caption_black);
                }
                else{
                    captionColor = CaptionColor.WHITE;
                    imgColorCaption.setImageResource(R.drawable.colored_caption_white);
                }
            }
        });
    }

    //-------- Image Related Methods

    /**
     * Gives the user to add a photo by either taking a picture or selecting one from their gallery
     */
    private void imageOptionsDialog(){
        final Dialog dialog = new Dialog(NewEntryActivity.this);
        dialog.setContentView(R.layout.dialog_photo_picker);

        //------- Initialize Widgets
        // camera option
        ImageView cameraImg = dialog.findViewById(R.id.img_camera);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(NewEntryActivity.this);
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(NewEntryActivity.this);
                dialog.dismiss();
            }
        });

        // gallery option
        ImageView galleryImg = dialog.findViewById(R.id.img_gallery);
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(NewEntryActivity.this);
                dialog.dismiss();
            }
        });
        TextView galleryTxt = dialog.findViewById(R.id.txt_gallery);
        galleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(NewEntryActivity.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //--------- Helper Methods

    /**
     * Extracts the appropriate EntryHandler using the given date (dateString)
     *
     * @param dateString the date that corresponds to the wanted EntryHandler
     */
    private void getEntryHandler(String dateString){
        if(database.doesEntryHandlerExist(dateString)){
            // get EntryHandler from database
            entryHandler = database.getEntryHandler(dateString);
        }
        else{
            // create EntryHandler for today
            entryHandler = new EntryHandler(dateString);
        }
    }

    /**
     * Checks if the user has granted permissions to read from external storage. Results are
     * returned to onRequestPermissionResult.
     */
    private void checksReadingPermission(){
        // permission has not been granted
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission
                .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // permission has not been granted yet
            canCopyImages = false;

            // request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_PERMISSION);
        }
        // permission has been granted
        else{
            canCopyImages = true;
        }
    }

    /**
     * Takes in all the data the user entered (title, caption, and image) and uses to make a new
     * Entry object which is added to the current date's EntryHandler
     */
    private void submitEntry(){
        // create new entry
        String title = titleTxt.getText().toString();
        String caption = captionTxt.getText().toString();
        String description = descriptionTxt.getText().toString();
        imageFilePath = imageHandler.getFilepath();
        Entry entry = new Entry(imageFilePath, title, caption, description);
        entry.setCaptionColor(captionColor);

        // add new entry to database
        entryHandler.addEntry(entry);
        if(entryHandler.getNumberOfEntries() == 1){
            database.addEntries(entryHandler);
        }
        else{
            database.updateEntryHandler(entryHandler);
        }

        // go back to main page
        Intent intent = DiaryActivity.newInstance(this, entryHandler.getStringDate());
        startActivity(intent);
    }

    /**
     * Determines the dimensions of the image and loads it into the image view
     */
    private void loadImage(String filepath){
        int[] results = database.getDimensions(DiaryFragment.TAG);
        Double widthDouble = results[0] * .48;
        Double heightDouble = results[1] * .31;
        int minDimension = Math.min(widthDouble.intValue(), heightDouble.intValue());
        imageHandler.loadIntoImageView(minDimension, minDimension, filepath, entryImage);
    }

    //--------- Fragment Methods

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageHandler.RESULT_CODE_CAMERA){
            imageHandler.addNewImageToGallery();
            String filepath = imageHandler.getFilepath();
            loadImage(filepath);
        }
        if(requestCode == ImageHandler.RESULT_CODE_GALLERY){
            if(data != null){
                Uri imgUri = data.getData();
                imageHandler.handleGalleryResults(imgUri, getApplicationContext(), canCopyImages);
                String filepath = imageHandler.getFilepath();
                loadImage(filepath);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == REQUEST_CODE_READ_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                canCopyImages = true;
            }
            else{
                // permission denied
                canCopyImages = false;
            }
        }
    }

    public static Intent newInstance(Activity activity, String entryHandlerDate){
        Intent intent = new Intent(activity, NewEntryActivity.class);
        intent.putExtra(EXTRA_ENTRY_HANDLER, entryHandlerDate);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return true;
        }
    }
}
