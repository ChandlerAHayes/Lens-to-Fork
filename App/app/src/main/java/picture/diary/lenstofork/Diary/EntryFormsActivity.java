package picture.diary.lenstofork.Diary;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;

import java.io.File;

import picture.diary.lenstofork.Diary.Entry.CaptionColor;
import picture.diary.lenstofork.Diary.Entry.CaptionPosition;
import picture.diary.lenstofork.Diary.Entry.Entry;
import picture.diary.lenstofork.Diary.Entry.EntryHandler;
import picture.diary.lenstofork.R;
import picture.diary.lenstofork.Utils.DatabaseHandler;
import picture.diary.lenstofork.Utils.ImageHandler;

public class EntryFormsActivity extends AppCompatActivity {
    //------- Widgets
    private EditText titleTxt, descriptionTxt, captionTxt;
    private TextView captionHeader, descriptionHeader, spinnerTitleTxt, captionPreviewTxt;
    private ImageView image, imgCaptionMenu, imgCaptionColor, imgDescriptionMenu;
    private Spinner captionSpinner;
    private Button submitBttn;

    //------- Entry-Related Variables
    private static EntryHandler entryHandler;
    private DatabaseHandler database;
    private Entry originalEntry;    // used to compare with current values in forms for editing
    private Entry entry; // current instance of entry

    //------- Caption & Description Variables
    private CaptionColor captionColor;
    private CaptionPosition captionPosition;
    private boolean showCaption = false;
    private boolean showDescription = false;

    //------- Image Variables
    private ImageHandler imageHandler;
    private String imageFilePath = "";
    private boolean canCopyImages = false;

    //------- Other Variables
    private int MODE; // will either be EDIT_MODE or NEW_MODE

    //-------- Constants
    public static final String TAG = "Entry Forms Activity";
    public static final String EXTRA_ENTRY_HANDLER = "Entry Handler";
    private static final String EXTRA_ENTRY_INDEX = "Extra Entry Index";
    private static final String EXTRA_MODE = "Extra Mode";
    private static final int REQUEST_CODE_READ_PERMISSION = 3;
    // determines if the activity is adding a new entry or editing an existing entry
    private static final int EDIT_MODE = 111;
    private static final int NEW_MODE = 222;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_forms);
        MODE = getIntent().getIntExtra(EXTRA_MODE, 222);

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.up_navigation_white);
        if(MODE == EDIT_MODE){
            setTitle(R.string.toolbar_edit);
        }
        else{
            setTitle(R.string.toolbar_new_entry);
        }

        configureHandlers();
        // ask for reading permission if it has not been granted
        if(MODE == EDIT_MODE){
            checksReadingPermission();
        }

        //--------- Widgets
        initializeWidgets();
        if(MODE == EDIT_MODE){
            setUpEntryData();
        }
        configureCaptionWidgets();
        configureOnClickListeners();

    }

    //------- Initializations & Configurations Methods

    /**
     * Initializes the database, entryHandler, and imageHandler objects. If in EditMode, then it
     * will also initialize the entry object and a copy of it in originalEntry object.
     */
    private void configureHandlers(){
        Intent arguments = getIntent();

        // get EntryHandler & entry (if in EditMode)
        String dateStr = arguments.getStringExtra(EXTRA_ENTRY_HANDLER);
        database = new DatabaseHandler(this);
        if(database.doesEntryHandlerExist(dateStr)){
            entryHandler = database.getEntryHandler(dateStr);
        }
        else{
            entryHandler = new EntryHandler(dateStr);
        }
        if(MODE == EDIT_MODE){
            // get entry
            entry = entryHandler.getEntry(arguments.getIntExtra(EXTRA_ENTRY_INDEX, 0));

            // get a copy of the current values in entry
            originalEntry = new Entry(entry.getId(), entry.getImageFilePath(), entry.getTitle(),
                    entry.getCaption(), entry.getDescription());
            originalEntry.setCaptionColor(entry.getCaptionColor());
            originalEntry.setCaptionPosition(entry.getCaptionPosition());
        }

        // get ImageHandler
        if(MODE == EDIT_MODE){
            // want the size to be the same as the DetailFragment images
            imageHandler = new ImageHandler(this, DetailFragment.TAG);
            // initialize imageFilePath
            imageFilePath = entry.getImageFilePath();
        }
        else{
            // want the size to be the same as the DiaryFragment images
            imageHandler = new ImageHandler(this, DiaryFragment.TAG);
        }
    }

    /**
     * Initializes all widgets with the
     */
    private void initializeWidgets(){
        // set title
        titleTxt = (EditText) findViewById(R.id.txt_title);

        // caption
        captionHeader = (TextView) findViewById(R.id.header_caption);
        captionTxt = (EditText) findViewById(R.id.txt_caption);
        imgCaptionColor = (ImageView) findViewById(R.id.img_caption_color);
        imgCaptionMenu = (ImageView) findViewById(R.id.img_menu_caption);
        spinnerTitleTxt = (TextView) findViewById(R.id.txt_capt_spinner);
        captionSpinner = (Spinner) findViewById(R.id.spinner_capt_pos);
        captionPreviewTxt = (TextView) findViewById(R.id.txt_capt_preview);

        // description
        descriptionHeader = (TextView) findViewById(R.id.header_description);
        descriptionTxt = (EditText) findViewById(R.id.txt_description);
        imgDescriptionMenu = (ImageView) findViewById(R.id.img_menu_description);

        // image
        image = (ImageView) findViewById(R.id.image);

        // submit button
        submitBttn = findViewById(R.id.bttn_submit);
        if(MODE == NEW_MODE){
            submitBttn.setText("Submit");
        }
        else{
            submitBttn.setText("Save Changes");
        }
    }

    /**
     * Adds the entry object's attributes in widgets if in EditMode
     */
    private void setUpEntryData(){
        // image
        loadImage(entry.getImageFilePath());

        // title
        if(!entry.getTitle().equals("")){
            titleTxt.setText(entry.getTitle());
        }

        // caption
        if(!entry.getCaption().equals("")){
            captionTxt.setText(entry.getCaption());
            captionPreviewTxt.setText(entry.getCaption());
        }

        // description
        if(!entry.getDescription().equals("")){
            descriptionTxt.setText(entry.getDescription());
        }
    }

    /**
     * Configure caption related widgets, setting up their onClickListeners, and customizing
     * views.
     */
    private void configureCaptionWidgets(){
        // get caption preview as user writes it
        captionTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                captionPreviewTxt.setText(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //-------- Caption Menu
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

        //--------- Caption Color
        // initialize captionColor object
        if(MODE == NEW_MODE){
            // initialize with a default color of white
            captionColor = CaptionColor.WHITE;
        }
        else{
            // change caption color icon to appropriate color
            captionColor = entry.getCaptionColor();
            Drawable background = ContextCompat.getDrawable(EntryFormsActivity.this,
                    R.drawable.colored_caption_white);
            background.setColorFilter(captionColor.getColor(), PorterDuff.Mode.SRC_IN);
            imgCaptionColor.setImageDrawable(background);
        }

        // set onClickListener
        imgCaptionColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] colors = getResources().getIntArray(R.array.colorPicker);

                final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.initialize(R.string.color_picker, colors,
                        captionColor.getColor(), 4, colors.length);
                colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.
                        OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        colorPickerDialog.setSelectedColor(color);
                        captionColor = CaptionColor.getCaptionColor(color);

                        // change color of caption icon
                        Drawable background = ContextCompat.getDrawable(
                                EntryFormsActivity.this, R.drawable.colored_caption_white);
                        background.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        imgCaptionColor.setImageDrawable(background);

                        // change color of caption preview
                        captionPreviewTxt.setTextColor(color);
                    }
                });
                colorPickerDialog.show(getFragmentManager(), TAG);
            }
        });

        //--------- Caption Position
        // create ArrayAdapter to populate spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_caption_position, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        captionSpinner.setAdapter(adapter);

        // add OnClickListener
        captionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // save selected position
                String captionPositionStr = (String) parent.getItemAtPosition(position);
                captionPosition = CaptionPosition.getCaptionPosition(captionPositionStr);
                moveCaptionPreview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // set entry's current caption position in the spinner
        if(MODE == EDIT_MODE){
            captionPosition = entry.getCaptionPosition();
        }
        else{
            captionPosition = CaptionPosition.CENTER;
        }
        captionSpinner.setSelection(adapter.getPosition(captionPosition.getValue()));
    }

    /**
     * Adds the onClickListeners to the widgets, excluding caption-related widgets.
     */
    private void configureOnClickListeners(){
        // description
        imgDescriptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDescriptionMenu();
            }
        });
        descriptionHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDescriptionMenu();
            }
        });

        // image
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOptionsDialog();
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

    //------- Caption & Description Methods

    /**
     * Moves the position of the captionPreviewTxt to location the user selected
     */
    private void moveCaptionPreview(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);

        switch(captionPosition){
            case TOP_LEFT:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                captionPreviewTxt.setLayoutParams(params);
                break;

            case TOP_CENTER:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                captionPreviewTxt.setLayoutParams(params);
                break;

            case TOP_RIGHT:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_RIGHT, image.getId());
                captionPreviewTxt.setLayoutParams(params);
                break;

            case LEFT_CENTER:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                captionPreviewTxt.setLayoutParams(params);
                break;

            case CENTER:
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                captionPreviewTxt.setLayoutParams(params);
                break;

            case RIGHT_CENTER:
                params.addRule(RelativeLayout.ALIGN_RIGHT, image.getId());
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                captionPreviewTxt.setLayoutParams(params);
                break;

            case BOTTOM_LEFT:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                captionPreviewTxt.setLayoutParams(params);
                break;

            case BOTTOM_CENTER:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                captionPreviewTxt.setLayoutParams(params);
                break;

            case BOTTOM_RIGHT:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.ALIGN_RIGHT, image.getId());
                captionPreviewTxt.setLayoutParams(params);
                break;
        }
    }

    /**
     * Toggles if the caption options are displaying or not
     */
    private void toggleCaptionMenu(){
        if(showCaption){
            showCaption = false;

            imgCaptionMenu.setImageResource(R.drawable.arrow_down_black);
            captionTxt.setVisibility(View.GONE);
            imgCaptionColor.setVisibility(View.GONE);
            spinnerTitleTxt.setVisibility(View.GONE);
            captionSpinner.setVisibility(View.GONE);

        }
        else{
            showCaption = true;

            imgCaptionMenu.setImageResource(R.drawable.arrow_up_black);
            captionTxt.setVisibility(View.VISIBLE);
            imgCaptionColor.setVisibility(View.VISIBLE);
            spinnerTitleTxt.setVisibility(View.VISIBLE);
            captionSpinner.setVisibility(View.VISIBLE);
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

    //------- Image Methods

    /**
     * Gives the user to add a photo by either taking a picture or selecting one from their gallery
     */
    private void imageOptionsDialog(){
        final Dialog dialog = new Dialog(EntryFormsActivity.this);
        dialog.setContentView(R.layout.dialog_photo_picker);

        //------- Initialize Widgets
        // camera option
        ImageView cameraImg = dialog.findViewById(R.id.img_camera);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(EntryFormsActivity.this);
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(EntryFormsActivity.this);
                dialog.dismiss();
            }
        });

        // gallery option
        ImageView galleryImg = dialog.findViewById(R.id.img_gallery);
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(EntryFormsActivity.this);
                dialog.dismiss();
            }
        });
        TextView galleryTxt = dialog.findViewById(R.id.txt_gallery);
        galleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(EntryFormsActivity.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Determines the dimensions of the image and loads it into the image view
     */
    private void loadImage(String filepath){
        int[] dimensions;
        Double widthDouble;
        Double heightDouble;

        if(MODE == NEW_MODE){
            dimensions = database.getDimensions(DiaryFragment.TAG);
            widthDouble = dimensions[0] * .48;
            heightDouble = dimensions[1] * .31;}
        else{
            dimensions = database.getDimensions(DetailFragment.TAG);
            widthDouble = dimensions[0] * 0.98;
            heightDouble = dimensions[1] * 0.48;
        }

        int minDimension = Math.min(widthDouble.intValue(), heightDouble.intValue());
        if(new File(filepath).exists()){
            imageHandler.loadIntoImageView(minDimension, minDimension, filepath, image);
        }
        else{
            imageHandler.loadIntoImageView(minDimension, minDimension, R.drawable.camera_teal,
                    image);
        }
    }

    //-------- Helper Methods

    /**
     * If in NewMode, an entry object is created from the data in the widgets and adds it to the
     * database.
     *
     * If in EditMode, it updates the entry object with data that has been changed and updates the
     * database.
     */
    private void submitEntry(){
        if(MODE == NEW_MODE){
            // create new entry
            String title = titleTxt.getText().toString();
            String caption = captionTxt.getText().toString();
            String description = descriptionTxt.getText().toString();
            imageFilePath = imageHandler.getFilepath();
            Entry entry = new Entry(imageFilePath, title, caption, description);
            entry.setCaptionColor(captionColor);
            entry.setCaptionPosition(captionPosition);

            // add new entry to database
            entryHandler.addEntry(entry);
            if(!database.doesEntryHandlerExist(entryHandler.getStringDate())){
                database.addEntries(entryHandler);
            }
            else{
                database.updateEntryHandler(entryHandler);
            }

            // go back to main page
            Intent intent = DiaryActivity.newInstance(this, entryHandler.getStringDate());
            startActivity(intent);
        }
        else{
            boolean hasEntryChanged = false;

            // check if any entry attributes have changed
            if(!originalEntry.getTitle().equals(titleTxt.getText().toString()) ){
                hasEntryChanged = true;
                entry.setTitle(titleTxt.getText().toString());
            }

            if(!originalEntry.getCaption().equals(captionTxt.getText().toString())){
                hasEntryChanged = true;
                entry.setCaption(captionTxt.getText().toString());
            }

            if(!originalEntry.getCaptionColor().equals(captionColor)){
                hasEntryChanged = true;
                entry.setCaptionColor(captionColor);
            }

            if(!originalEntry.getCaptionPosition().equals(captionPosition)){
                hasEntryChanged = true;
                entry.setCaptionPosition(captionPosition);
            }

            if(!originalEntry.getDescription().equals(entry.getDescription())){
                hasEntryChanged = true;
                entry.setDescription(descriptionTxt.getText().toString());
            }

            if(!originalEntry.getImageFilePath().equals(imageFilePath)){
                hasEntryChanged = true;
                entry.setImageFilePath(imageFilePath);
            }

            if(hasEntryChanged){
                // update entry with entryHandler and database
                entryHandler.updateEntry(getIntent().getIntExtra(EXTRA_ENTRY_INDEX, 0),
                        entry);
                database.updateEntryHandler(entryHandler);
            }

            onBackPressed();
        }
    }

    //--------- Activity Methods

    /**
     * Returns an intent to start EntryFormsActivity in Edit Mode
     *
     * @param activity the activity calling the method
     * @param entryHandler the EntryHandler the activity needs
     * @param entryIndex the index of the Entry to edit
     * @return an Intent that will start this Activity in Edit Mode
     */
    public static Intent newIntent(Activity activity, String entryHandler, int entryIndex){
        Intent intent = new Intent(activity, EntryFormsActivity.class);
        intent.putExtra(EXTRA_ENTRY_HANDLER, entryHandler);
        intent.putExtra(EXTRA_ENTRY_INDEX, entryIndex);
        intent.putExtra(EXTRA_MODE, EDIT_MODE);
        return intent;
    }

    /**
     * Returns an intent to start EntryFormsActivity in New Mode
     * @param activity the activity calling the method
     * @param entryHandlerDate the EntryHandler the activity needs
     * @return an Intent that will start this Activity in New Mode
     */
    public static Intent newIntent(Activity activity, String entryHandlerDate){
        Intent intent = new Intent(activity, EntryFormsActivity.class);
        intent.putExtra(EXTRA_ENTRY_HANDLER, entryHandlerDate);
        intent.putExtra(EXTRA_MODE, NEW_MODE);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageHandler.RESULT_CODE_CAMERA){
            imageHandler.addNewImageToGallery();
            imageFilePath = imageHandler.getFilepath();
            loadImage(imageFilePath);
        }
        if(requestCode == ImageHandler.RESULT_CODE_GALLERY){
            if(data != null){
                Uri imgUri = data.getData();
                imageHandler.handleGalleryResults(imgUri, getApplicationContext(), canCopyImages);
                imageFilePath = imageHandler.getFilepath();
                loadImage(imageFilePath);

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
