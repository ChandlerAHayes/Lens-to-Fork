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
import android.widget.ImageView;
import android.widget.TextView;

import Entry.Entry;
import Entry.EntryHandler;
import picture.diary.lenstofork.Diary.Utils.DatabaseHandler;
import picture.diary.lenstofork.Diary.Utils.ImageHandler;
import picture.diary.lenstofork.R;

public class NewEntryFragment extends Fragment {
    //widgets
    private ImageView entryImage;
    private TextView titleTxt;
    private TextView captionTxt;
    private Button submitBttn;

    //------- Variables
    private static EntryHandler entryHandler;
    private ImageHandler imageHandler;
    private String imageFilePath = "";
    private boolean canCopyImages = false;


    //------- Constants
    public static final String TAG = "New Entry Fragment";
    public static final String ARG_ENTRY_HANDLER = "Entry Handler";
    public static final int REQUEST_CODE_READ_PERMISSION = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageHandler = new ImageHandler(getActivity(), TAG);

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

        titleTxt = (TextView) view.findViewById(R.id.txt_entry_title);
        captionTxt = (TextView) view.findViewById(R.id.txt_entry_caption);

        submitBttn = (Button) view.findViewById(R.id.bttn_submit);
        submitBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEntry();
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
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        if(databaseHandler.doesEntryHandlerExist(dateString)){
            // get EntryHandler from database
            entryHandler = databaseHandler.getEntryHandler(dateString);
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
        imageFilePath = imageHandler.getFilepath();
        Entry entry = new Entry(imageFilePath, title, caption);
        entry.getImageFilePath();

        // add new entry to database
        entryHandler.addEntry(entry);
        DatabaseHandler database = new DatabaseHandler(getContext());
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
            DimensionsDiaryFragment dimensions = DimensionsDiaryFragment.getInstance();
            imageHandler.addNewImageToGallery();
            imageHandler.resizeAndInsertImage(dimensions.getWidth(), dimensions.getHeight(),
                    entryImage);
        }
        if(requestCode == ImageHandler.RESULT_CODE_GALLERY){
            if(data != null){
                DimensionsDiaryFragment dimensions = DimensionsDiaryFragment.getInstance();

                Uri imgUri = data.getData();
                imageHandler.handleGalleryResults(imgUri, getContext(), canCopyImages);
                if(canCopyImages){
                    imageHandler.resizeAndInsertImage(dimensions.getWidth(),
                            dimensions.getHeight(), entryImage);
                }
                else{
                    imageHandler.setImageInView(entryImage);
                }
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
