package picture.diary.lenstofork.Diary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import Entry.EntryHandler;
import Entry.Entry;
import picture.diary.lenstofork.Diary.Utils.DatabaseHandler;
import picture.diary.lenstofork.Diary.Utils.ImageHandler;
import picture.diary.lenstofork.R;

public class DetailFragment extends Fragment {
    //------ Widgets
    private ImageView image;
    private TextView headerImage;
    private Button submitBttn;
    // title
    private TextView titleTxt;
    private EditText titleEditTxt;
    // caption
    private TextView captionTxt;
    private EditText captionEditTxt;
    private TextView headerCaption;
    // description
    private TextView descriptionTxt;
    private EditText descriptionEditTxt;
    private TextView headerDescription;

    // variables
    private boolean isInEditMode = false;
    private DatabaseHandler database;
    private EntryHandler entryHandler;
    private Entry entry;
    private int position = -1;
    private ImageHandler imageHandler;
    private boolean canCopyImages = false;

    // Entry attributes
    private String filepath = "";
    private String title = "";
    private String caption = "";
    private String description = "";

    // constants
    public static final String TAG = "DetailFragment";
    private static final String ARG_ENTRY_HANDLER = "Arg Entry Handler";
    private static final String ARG_ENTRY_POSITION = "Arg Entry Position";
    private static final int REQUEST_CODE_READ_PERMISSION = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get Entry from database
        database = new DatabaseHandler(getContext());
        Bundle arguments = getArguments();
        entryHandler = database.getEntryHandler(arguments.getString(ARG_ENTRY_HANDLER));
        position = arguments.getInt(ARG_ENTRY_POSITION);
        entry = entryHandler.getEntry(position);

        // put date in Toolbar
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yy");
        getActivity().setTitle("Details: " + sdf.format(entryHandler.getDate().getTime()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // helps determine if the user has updated the entry by comparing with original values
        title = entry.getTitle();
        caption = entry.getCaption();
        description = entry.getDescription();
        filepath = entry.getImageFilePath();

        //------- Set Up Widgets
        titleTxt = (TextView) view.findViewById(R.id.txt_title);
        titleEditTxt = (EditText) view.findViewById(R.id.edit_txt_title);

        captionTxt = (TextView) view.findViewById(R.id.txt_caption);
        captionEditTxt = (EditText) view.findViewById(R.id.edit_txt_caption);
        headerCaption = (TextView) view.findViewById(R.id.header_caption);

        descriptionTxt = (TextView) view.findViewById(R.id.txt_description);
        descriptionEditTxt = (EditText) view.findViewById(R.id.edit_txt_description);
        headerDescription = (TextView) view.findViewById(R.id.header_description);

        configureTextViews();

        image = (ImageView) view.findViewById(R.id.img);
        image.setImageBitmap(entry.getImage());
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler = new ImageHandler(getActivity(), TAG);
                imageOptionsDialog();
            }
        });
        // only want it to be clickable in edit mode
        image.setClickable(false);

        submitBttn = (Button) view.findViewById(R.id.bttn_submit);
        submitBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEntry();
            }
        });

        return view;
    }

    //-------- Helper Methods

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
                imageHandler.takeNewPicture(DetailFragment.this);
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(DetailFragment.this);
                dialog.dismiss();
            }
        });

        // gallery option
        ImageView galleryImg = dialog.findViewById(R.id.img_gallery);
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(DetailFragment.this);
                dialog.dismiss();
            }
        });
        TextView galleryTxt = dialog.findViewById(R.id.txt_gallery);
        galleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(DetailFragment.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Toggles the visibility of the TextViews and EditViews to either be in edit mode or view mode
     */
    private void toggleEditMode(){
        if(isInEditMode){
            // make TextViews invisible and make EditTexts visible
            titleTxt.setVisibility(View.GONE);
            captionTxt.setVisibility(View.GONE);
            descriptionTxt.setVisibility(View.GONE);

            titleEditTxt.setVisibility(View.VISIBLE);
            captionEditTxt.setVisibility(View.VISIBLE);
            descriptionEditTxt.setVisibility(View.VISIBLE);

            // just in case they're not visible
            headerCaption.setVisibility(View.VISIBLE);
            headerDescription.setVisibility(View.VISIBLE);

            image.setClickable(true);
            submitBttn.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.header_image).setVisibility(View.VISIBLE);
        }
        else{
            // make TextViews visible
            // title
            if(!title.equals("")){
                titleTxt.setVisibility(View.VISIBLE);
            }
            // caption
            if(!caption.equals("")){
                captionTxt.setVisibility(View.VISIBLE);
            }
            else{
                headerCaption.setVisibility(View.GONE);
            }
            // description
            if(!description.equals("")){
                descriptionTxt.setVisibility(View.VISIBLE);
            }
            else{
                headerDescription.setVisibility(View.GONE);
            }

            // make EditTexts invisible
            titleEditTxt.setVisibility(View.GONE);
            captionEditTxt.setVisibility(View.GONE);
            descriptionEditTxt.setVisibility(View.GONE);

            image.setClickable(false);
            submitBttn.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.header_image).setVisibility(View.GONE);
        }
    }

    /**
     * Configure the TextViews to show Entry info
     */
    private void configureTextViews(){
        // check title
        if(title.equals("")){
            // there's no title, so make it invisible
            titleTxt.setVisibility(View.GONE);
        }
        else {
            // insert title
            titleTxt.setText(title);
            titleEditTxt.setText(title);
        }

        // check caption
        if(caption.equals("")){
            // there's no caption, so make it invisible
            captionTxt.setVisibility(View.GONE);
            headerCaption.setVisibility(View.GONE);
        }
        else{
            // insert caption
            captionTxt.setText(caption);
            captionEditTxt.setText(caption);
        }

        // check description
        if(description.equals("")){
            // there's no description, so make it invisible
            descriptionTxt.setVisibility(View.GONE);
            headerDescription.setVisibility(View.GONE);
        }
        else{
            // insert description
            descriptionTxt.setText(description);
            descriptionEditTxt.setText(description);
        }
    }

    //---------- Editing Entries Methods

    /**
     *
     */
    private void submitEntry(){
        boolean hasEntryChanged = false; //represents if the entry's attributes have changed

        // check if title has changed
        if(!title.equals(titleEditTxt.getText().toString())){
            title = titleEditTxt.getText().toString();
            hasEntryChanged = true;
            entry.setTitle(title);

            titleTxt.setText(title);
            titleEditTxt.setText(title);
        }

        // check if caption has changed
        if(!caption.equals(captionEditTxt.getText().toString())){
            hasEntryChanged = true;
            caption = captionEditTxt.getText().toString();
            entry.setCaption(captionEditTxt.getText().toString());

            captionTxt.setText(caption);
            captionEditTxt.setText(caption);
        }

        // check if description has changed
        if(!description.equals(descriptionEditTxt.getText().toString())){
            hasEntryChanged = true;
            description = descriptionEditTxt.getText().toString();
            entry.setDescription(descriptionEditTxt.getText().toString());

            descriptionTxt.setText(description);
            descriptionEditTxt.setText(description);
        }

        // check if image has changed
        if(!filepath.equals(entry.getImageFilePath())){
            hasEntryChanged = true;
            entry.setImageFilePath(filepath);
        }

        // update entry in database if it has been changed
        if(hasEntryChanged){
            entryHandler.updateEntry(position, entry);
            database.updateEntryHandler(entryHandler);
        }

        toggleEditMode();
    }

    /**
     * Deletes the entry from the database and returns to DiaryActivity after deletion.
     */
    private void deleteEntry(){
        // remove entry
        entryHandler.removeEntry(position);
        // update database
        database.updateEntryHandler(entryHandler);
        // return to DiaryActivity
        Intent intent = DiaryActivity.newInstance(getActivity(), entryHandler.getStringDate());
        startActivity(intent);
    }

    /**
     * Creates an AlertDialog that confirms if the user wants to delete the entry. If they do, then
     * it passes off the deletion to the deleteEntry() method.
     */
    private void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this entry?");
        builder.setCancelable(true);

        // yes button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEntry();
            }
        });

        // no button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });AlertDialog dialog = builder.create();
        dialog.show();
    }


    //-------- Fragment Methods
    public static DetailFragment newInstance(int entryPosition, String entryHandlerDate) {
        DetailFragment fragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_HANDLER, entryHandlerDate);
        args.putInt(ARG_ENTRY_POSITION, entryPosition);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.edit:
                // toggle edit mode
                if(isInEditMode){
                    isInEditMode = false;
                }
                else{
                    isInEditMode = true;
                }
                toggleEditMode();

                return true;

            case R.id.delete:
                confirmDelete();
                return true;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageHandler.RESULT_CODE_CAMERA){
            DimensionsDiaryFragment dimensions = DimensionsDiaryFragment.getInstance();
            imageHandler.addNewImageToGallery();
            imageHandler.resizeAndInsertImage(dimensions.getWidth(), dimensions.getHeight(),
                    image);
            filepath = imageHandler.getFilepath();
        }
        if(requestCode == ImageHandler.RESULT_CODE_GALLERY){
            if(data != null){
                DimensionsDiaryFragment dimensions = DimensionsDiaryFragment.getInstance();

                Uri imgUri = data.getData();
                imageHandler.handleGalleryResults(imgUri, getContext(), canCopyImages);
                if(canCopyImages){
                    imageHandler.resizeAndInsertImage(dimensions.getWidth(),
                            dimensions.getHeight(), image);
                }
                else{
                    imageHandler.setImageInView(image);
                }
            }

            filepath = imageHandler.getFilepath();
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
}
