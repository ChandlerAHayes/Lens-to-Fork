package picture.diary.lenstofork.Diary;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class NewEntryFragment extends Fragment {
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
    public static final String TAG = "New Entry Fragment";
    public static final String ARG_ENTRY_HANDLER = "Entry Handler";
    public static final int REQUEST_CODE_READ_PERMISSION = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageHandler = new ImageHandler(getActivity(), TAG);
        database = new DatabaseHandler(getContext());

        checksReadingPermission();
    }

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
                imageOptionsDialog();
            }
        });

        titleTxt = (EditText) view.findViewById(R.id.txt_entry_title);
        captionTxt = (EditText) view.findViewById(R.id.txt_entry_caption);
        descriptionTxt = (EditText) view.findViewById(R.id.txt_entry_description);

        submitBttn = (Button) view.findViewById(R.id.bttn_submit);
        submitBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEntry();
            }
        });

        imgColorCaption = (ImageView) view.findViewById(R.id.img_caption_color);
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

        return view;
    }

    //-------- Image Related Methods
    /**
     * Gives the user to add a photo by either taking a picture or selecting one from their gallery
     */
    private void imageOptionsDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_photo_picker);

        //------- Initialize Widgets
        // camera option
        ImageView cameraImg = dialog.findViewById(R.id.img_camera);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(NewEntryFragment.this);
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(NewEntryFragment.this);
                dialog.dismiss();
            }
        });

        // gallery option
        ImageView galleryImg = dialog.findViewById(R.id.img_gallery);
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(NewEntryFragment.this);
                dialog.dismiss();
            }
        });
        TextView galleryTxt = dialog.findViewById(R.id.txt_gallery);
        galleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(NewEntryFragment.this);
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
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // permission has not been granted yet
            canCopyImages = false;

            // request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission
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
        Intent intent = DiaryActivity.newInstance(getActivity(), entryHandler.getStringDate());
        startActivity(intent);
    }

    //--------- Fragment Methods

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageHandler.RESULT_CODE_CAMERA){
            imageHandler.addNewImageToGallery();
            String filepath = imageHandler.getFilepath();

            // resize and insert image into ImageView
            int[] results = database.getDimensions(DiaryFragment.TAG);
            Double widthDouble = results[0] * .48;
            Double heightDouble = results[1] * .31;
            int minDimension = Math.min(widthDouble.intValue(), heightDouble.intValue());
            imageHandler.loadIntoImageView(minDimension, minDimension, filepath, entryImage);
        }
        if(requestCode == ImageHandler.RESULT_CODE_GALLERY){
            if(data != null){
                Uri imgUri = data.getData();
                imageHandler.handleGalleryResults(imgUri, getContext(), canCopyImages);
                String filepath = imageHandler.getFilepath();

                // resize and insert image into ImageView
                int[] results = database.getDimensions(DiaryFragment.TAG);
                Double widthDouble = results[0] * .48;
                Double heightDouble = results[1] * .31;
                int minDimension = Math.min(widthDouble.intValue(), heightDouble.intValue());
                imageHandler.loadIntoImageView(minDimension, minDimension, filepath, entryImage);
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

    public static NewEntryFragment newInstance(String entryHandlerDate){
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_HANDLER, entryHandlerDate);

        NewEntryFragment fragment = new NewEntryFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
