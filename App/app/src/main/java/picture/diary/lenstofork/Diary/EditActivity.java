package picture.diary.lenstofork.Diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class EditActivity extends AppCompatActivity{
    // widgets
    private EditText titleTxt, descriptionTxt, captionTxt;
    private TextView captionHeader, descriptionHeader, spinnerTitleTxt;
    private ImageView image, imgCaptionMenu, imgCaptionColor, imgDescriptionMenu;
    private Spinner captionSpinner;
    private Button submitBttn;

    // Entry attributes
    private String filepath = "";
    private String title = "";
    private String caption = "";
    private String description = "";
    private CaptionColor captionColor;
    private CaptionPosition captionPosition;

    // variables
    private DatabaseHandler database;
    private EntryHandler entryHandler;
    private Entry entry = null;
    private ImageHandler imageHandler;
    private boolean canCopyImages = false;
    private boolean showCaption = false; // if true, the caption options are visible
    private boolean showDescription = false; // if true, the description options are visible

    // constants
    public static final String TAG = "Edit Activity";
    private static final String EXTRA_ENTRY_HANDLER = "Extra Entry Handler";
    private static final String EXTRA_ENTRY_INDEX = "Extra Entry Index";
    private static final int REQUEST_CODE_READ_PERMISSION = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_forms);

        //-------- Initialize imageHandler & EntryHandler and get original entry values
        imageHandler = new ImageHandler(this, DetailFragment.TAG);
        // get Entry & EntryHandler from database
        database = new DatabaseHandler(this);
        Intent arguments = getIntent();
        entryHandler = database.getEntryHandler(arguments.getStringExtra(EXTRA_ENTRY_HANDLER));
        entry = entryHandler.getEntry(arguments.getIntExtra(EXTRA_ENTRY_INDEX, 0));
        getOriginalValues();

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit Entry");

        //-------- Initialize Widgets
        // set title
        titleTxt = (EditText) findViewById(R.id.txt_title);

        // caption
        captionHeader = (TextView) findViewById(R.id.header_caption);
        captionTxt = (EditText) findViewById(R.id.txt_caption);
        imgCaptionColor = (ImageView) findViewById(R.id.img_caption_color);
        imgCaptionMenu = (ImageView) findViewById(R.id.img_menu_caption);
        spinnerTitleTxt = (TextView) findViewById(R.id.txt_capt_spinner);
        captionSpinner = (Spinner) findViewById(R.id.spinner_capt_pos);

        // description
        descriptionHeader = (TextView) findViewById(R.id.header_description);
        descriptionTxt = (EditText) findViewById(R.id.txt_description);
        imgDescriptionMenu = (ImageView) findViewById(R.id.img_menu_description);

        // image
        image = (ImageView) findViewById(R.id.image);

        // submit button
        submitBttn = findViewById(R.id.bttn_submit);

        // fill in values for widgets
        configureViews();
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
        captionPosition = entry.getCaptionPosition();
        description = entry.getDescription();
        filepath = entry.getImageFilePath();
    }

    /**
     * Fills in the relevant data into the EditTexts and ImageViews and sets their onClickListeners
     */
    private void configureViews(){
        // image
        loadImage();

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
        Drawable background = ContextCompat.getDrawable(EditActivity.this,
                R.drawable.colored_caption_white);
        background.setColorFilter(captionColor.getColor(), PorterDuff.Mode.SRC_IN);
        imgCaptionColor.setImageDrawable(background);
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
                        Drawable background = ContextCompat.getDrawable(EditActivity.this,
                                R.drawable.colored_caption_white);
                        background.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        imgCaptionColor.setImageDrawable(background);
                    }
                });
                colorPickerDialog.show(getFragmentManager(), TAG);
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

        // caption position
        configureCaptionSpinner();

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
            entry.setCaptionColor(captionColor);
        }

        if(!captionPosition.equals(entry.getCaptionPosition())){
            hasEntryChanged = true;
            entry.setCaptionPosition(captionPosition);
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
            entryHandler.updateEntry(getIntent().getIntExtra(EXTRA_ENTRY_INDEX, 0),
                    entry);
            database.updateEntryHandler(entryHandler);
        }

        onBackPressed();
    }

    /**
     * Gives the user to add a photo by either taking a picture or selecting one from their gallery
     */
    private void imageOptionsDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_photo_picker);

        //------- Initialize Widgets
        // camera option
        ImageView cameraImg = dialog.findViewById(R.id.img_camera);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(EditActivity.this);
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.takeNewPicture(EditActivity.this);
                dialog.dismiss();
            }
        });

        // gallery option
        ImageView galleryImg = dialog.findViewById(R.id.img_gallery);
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(EditActivity.this);
                dialog.dismiss();
            }
        });
        TextView galleryTxt = dialog.findViewById(R.id.txt_gallery);
        galleryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.selectImage(EditActivity.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Determines the dimensions of the image and loads it into the image view
     */
    private void loadImage(){
        int[] dimensions = database.getDimensions(DetailFragment.TAG);
        Double widthDouble = dimensions[0] * 0.98;
        Double heightDouble = dimensions[1] * 0.48;
        int minDimensions = Math.min(widthDouble.intValue(), heightDouble.intValue());
        if(new File(filepath).exists()){
            imageHandler.loadIntoImageView(minDimensions, minDimensions, filepath, image);
        }
        else{
            imageHandler.loadIntoImageView(minDimensions, minDimensions, R.drawable.camera_teal,
                    image);
        }
    }

    //-------- Caption & Description Menu Methods

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

    private void configureCaptionSpinner(){

        // create ArrayAdapter to populate spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_caption_position, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        captionSpinner.setAdapter(adapter);
        captionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String captionPosString = (String) parent.getItemAtPosition(position);
                captionPosition = CaptionPosition.getCaptionPosition(captionPosString);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // set entry's current caption position in the spinner
        captionSpinner.setSelection(adapter.getPosition(captionPosition.getValue()));
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

    public static Intent newIntent(Activity activity, String entryHandler, int entryIndex){
        Intent intent = new Intent(activity, EditActivity.class);
        intent.putExtra(EXTRA_ENTRY_HANDLER, entryHandler);
        intent.putExtra(EXTRA_ENTRY_INDEX, entryIndex);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageHandler.RESULT_CODE_CAMERA){
            imageHandler.addNewImageToGallery();
            filepath = imageHandler.getFilepath();
            loadImage();
        }
        if(requestCode == ImageHandler.RESULT_CODE_GALLERY){
            if(data != null){
                Uri imgUri = data.getData();
                imageHandler.handleGalleryResults(imgUri, this, canCopyImages);
                filepath = imageHandler.getFilepath();
                loadImage();
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
                onBackPressed();
                return true;

            default:
                return true;
        }
    }

}
