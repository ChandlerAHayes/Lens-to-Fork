package picture.diary.lenstofork.Diary;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import Entry.CaptionColor;
import Entry.Entry;
import Entry.EntryHandler;
import picture.diary.lenstofork.R;
import picture.diary.lenstofork.Utils.DatabaseHandler;
import picture.diary.lenstofork.Utils.ImageHandler;

public class EditFragment extends Fragment{
    // widgets
    private EditText titleTxt;
    private TextView captionHeader;
    private EditText captionTxt;
    private TextView descriptionHeader;
    private EditText descriptionTxt;
    private ImageView image;
    private ImageView imgCaptionMenu;
    private ImageView imgCaptionColor;
    private ImageView imgDescriptionMenu;
    private Button submitBttn;

    // Entry attributes
    private String filepath = "";
    private String title = "";
    private String caption = "";
    private String description = "";
    private CaptionColor captionColor;

    // variables
    private DatabaseHandler database;
    private EntryHandler entryHandler;
    private Entry entry;
    private ImageHandler imageHandler;
    private boolean canCopyImages = false;
    private boolean showCaption = false; // if true, the caption options are visible
    private boolean showDescription = false; // if true, the description options are visible

    // constants
    public static final String TAG = "EditFragment";
    private static final String ARG_ENTRY_HANDLER = "Arg Entry Handler";
    private static final String ARG_ENTRY_INDEX = "Arg Entry Index";
    private static final int REQUEST_CODE_READ_PERMISSION = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageHandler = new ImageHandler(getActivity(), DetailFragment.TAG);

        // get Entry & EntryHandler from database
        database = new DatabaseHandler(getContext());
        Bundle arguments = getArguments();
        entryHandler = database.getEntryHandler(arguments.getString(ARG_ENTRY_HANDLER));
        entry = entryHandler.getEntry(arguments.getInt(ARG_ENTRY_INDEX));

        getActivity().setTitle("Edit Entry");

        getOriginalValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        //-------- Toolbar
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setHomeAsUpIndicator(R.drawable.up_navigation_white);

        //-------- Initialize Widgets
        // set title
        titleTxt = (EditText) view.findViewById(R.id.txt_title);

        // caption
        captionHeader = (TextView) view.findViewById(R.id.header_caption);
        captionTxt = (EditText) view.findViewById(R.id.txt_caption);
        imgCaptionColor = (ImageView) view.findViewById(R.id.img_caption_color);
        imgCaptionMenu = (ImageView) view.findViewById(R.id.img_menu_caption);

        // description
        descriptionHeader = (TextView) view.findViewById(R.id.header_description);
        descriptionTxt = (EditText) view.findViewById(R.id.txt_description);
        imgDescriptionMenu = (ImageView) view.findViewById(R.id.img_menu_description);

        // image
        image = (ImageView) view.findViewById(R.id.image);

        // submit button
        submitBttn = view.findViewById(R.id.bttn_submit);

        // fill in values for widgets
        configureViews();

        return view;
    }

    //-------- Helper Methods

    /**
     * Initializes the title, caption, captionColor, description, and filepath objects with the
     * values attached to the Entry object so that it can be compared to see if any of the values
     * have been changed when the user taps the submitBttn
     */
    private void getOriginalValues(){
        title = entry.getTitle();
        caption = entry.getCaption();
        captionColor = entry.getCaptionColor();
        description = entry.getDescription();
        filepath = entry.getImageFilePath();
    }

    /**
     * Fills in the relevant data into the EditTexts and ImageViews and sets their onClickListeners
     */
    private void configureViews(){
        // image
        int[] dimensions = database.getDimensions(DetailFragment.TAG);
        Double widthDouble = dimensions[0] * 0.98;
        Double heightDouble = dimensions[1] * 0.48;
        int minDimensions = Math.min(widthDouble.intValue(), heightDouble.intValue());
        imageHandler.loadIntoImageView(minDimensions, minDimensions, filepath, image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOptionsDialog();
            }
        });

        // title
        if(!entry.getTitle().equals("")){
            titleTxt.setText(entry.getTitle());
        }

        // caption
        if(!entry.getCaption().equals("")){
            captionTxt.setText(entry.getCaption());
        }

        // caption color
        if(entry.getCaptionColor().equals(CaptionColor.BLACK)){
            imgCaptionColor.setImageResource(R.drawable.colored_caption_black);
        }
        imgCaptionColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle current color for caption
                if(entry.getCaptionColor().equals(CaptionColor.WHITE)){
                    // toggle to black
                    imgCaptionColor.setImageResource(R.drawable.colored_caption_black);
                    entry.setCaptionColor(CaptionColor.BLACK);
                }
                else{
                    // toggle to white
                    imgCaptionColor.setImageResource(R.drawable.colored_caption_white);
                    entry.setCaptionColor(CaptionColor.WHITE);
                }
            }
        });

        // caption menu
        imgCaptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCaptionMenu();
            }
        });
        captionHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCaptionMenu();
            }
        });

        // description
        if(!entry.getDescription().equals("")){
            descriptionTxt.setText(entry.getDescription());
        }

        imgDescriptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggles between showing and hiding description options
                toggleDescriptionMenu();
            }
        });
        descriptionHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDescriptionMenu();
            }
        });

        // submit button
        submitBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEntry();
            }
        });
    }

    /**
     * Gets the values entered and updates the Entry with the database
     */
    private void submitEntry(){
        boolean hasEntryChanged = false;

        // check if any entry attributes have changed
        if(!title.equals(titleTxt.getText().toString()) ){
            hasEntryChanged = true;
            entry.setTitle(titleTxt.getText().toString());
        }

        if(!caption.equals(captionTxt.getText().toString())){
            hasEntryChanged = true;
            entry.setCaption(captionTxt.getText().toString());
        }

        if(!captionColor.equals(entry.getCaptionColor())){
            hasEntryChanged = true;
        }

        if(!description.equals(entry.getDescription())){
            hasEntryChanged = true;
            entry.setDescription(descriptionTxt.getText().toString());
        }

        if(!filepath.equals(entry.getImageFilePath())){
            hasEntryChanged = true;
            entry.setImageFilePath(filepath);
        }

        if(hasEntryChanged){
            // update entry with entryHandler and database
            entryHandler.updateEntry(getArguments().getInt(ARG_ENTRY_INDEX), entry);
            database.updateEntryHandler(entryHandler);
        }

        returnToDetailFragment();
    }

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
                imageHandler.takeNewPicture(EditFragment.this);
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(EditFragment.this);
                dialog.dismiss();
            }
        });

        // gallery option
        ImageView galleryImg = dialog.findViewById(R.id.img_gallery);
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(EditFragment.this);
                dialog.dismiss();
            }
        });
        TextView galleryTxt = dialog.findViewById(R.id.txt_gallery);
        galleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(EditFragment.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //-------- Caption & Description Menu Methods

    /**
     * Toggles if the caption options are displaying or not
     */
    private void toggleCaptionMenu(){
        if(showCaption){
            imgCaptionMenu.setImageResource(R.drawable.arrow_down_black);
            captionTxt.setVisibility(View.GONE);
            imgCaptionColor.setVisibility(View.GONE);
            showCaption = false;
        }
        else{
            imgCaptionMenu.setImageResource(R.drawable.arrow_up_black);
            captionTxt.setVisibility(View.VISIBLE);
            imgCaptionColor.setVisibility(View.VISIBLE);
            showCaption = true;
        }
    }

    /**
     * Toggles if the description options are displaying or not
     */
    private void toggleDescriptionMenu(){
        if(showDescription){
            imgDescriptionMenu.setImageResource(R.drawable.arrow_down_black);
            descriptionTxt.setVisibility(View.GONE);
            showDescription = false;
        }
        else{
            imgDescriptionMenu.setImageResource(R.drawable.arrow_up_black);
            descriptionTxt.setVisibility(View.VISIBLE);
            showDescription = true;
        }
    }

    //-------- Fragment Methods

    public static EditFragment newInstance(String entryHandler, int entryIndex){
        EditFragment fragment = new EditFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_HANDLER, entryHandler);
        args.putInt(ARG_ENTRY_INDEX, entryIndex);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageHandler.RESULT_CODE_CAMERA){
            imageHandler.addNewImageToGallery();
            filepath = imageHandler.getFilepath();

            // resize and insert image into ImageView
            int[] dimensions = database.getDimensions(DetailFragment.TAG);
            Double widthDouble = dimensions[0] * 0.98;
            Double heightDouble = dimensions[1] * 0.48;
            int minDimension = Math.min(widthDouble.intValue(), heightDouble.intValue());
            Picasso.get()
                .load(new File(entry.getImageFilePath()))
                .resize(minDimension, minDimension)
                .centerCrop()
                .into(image);
        }
        if(requestCode == ImageHandler.RESULT_CODE_GALLERY){
            if(data != null){
                Uri imgUri = data.getData();
                imageHandler.handleGalleryResults(imgUri, getContext(), canCopyImages);
                filepath = imageHandler.getFilepath();

                // resize and insert image into ImageView
                int[] dimensions = database.getDimensions(DetailFragment.TAG);
                Double widthDouble = dimensions[0] * 0.98;
                Double heightDouble = dimensions[1] * 0.48;
                int minDimension = Math.min(widthDouble.intValue(), heightDouble.intValue());
                Picasso.get()
                    .load(new File(filepath))
                    .resize(minDimension, minDimension)
                    .centerCrop()
                    .into(image);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                returnToDetailFragment();
                return true;

            default:
                return true;
        }
    }

    private void returnToDetailFragment(){
        // add new instance of DetailFragment
        FragmentManager manager = getActivity().getSupportFragmentManager();
        // remove EditFragment from backStack
        manager.popBackStackImmediate();
        FragmentTransaction transaction = manager.beginTransaction();
        DetailFragment fragment = DetailFragment.newInstance(getArguments()
                .getInt(ARG_ENTRY_INDEX), entryHandler.getStringDate());
        transaction.replace(R.id.main_content, fragment, DetailFragment.TAG);
        transaction.commit();
    }
}
