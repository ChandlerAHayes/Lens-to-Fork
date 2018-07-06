package picture.diary.lenstofork.Diary;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    // variables
    private static EntryHandler handler;

    // constants
    public static final String TAG = "New Entry Fragment";
    public static final String ARG_ENTRY_HANDLER = "Entry Handler";
    public static final int RESULT_CODE_CAMERA = 0;
    public static final int RESULT_CODE_GALLERY = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_entry, container, false);

        // get extras from intents
        Bundle args = getArguments();
        handler = (EntryHandler) args.getSerializable(ARG_ENTRY_HANDLER);

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
                Drawable image = entryImage.getDrawable();
                //TODO get file path
                String title = titleTxt.getText().toString();
                String caption = captionTxt.getText().toString();
                Entry entry = new Entry(image, title, caption);

                // add new entry to database
                handler.addEntry(entry);
                DatabaseHandler database = new DatabaseHandler(getContext());
                int x = handler.getNumberOfEntries();
                if(handler.getNumberOfEntries() == 1){
                    database.addEntries(handler);
                }
                else{
                    database.updateEntryHandler(handler);
                }

                // go back to main page
                startActivity(new Intent(getActivity(), DiaryActivity.class));
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
        dialog.setContentView(R.layout.photo_picker_dialog);

        //------- Initialize Widgets
        // camera option
        ImageView cameraImg = dialog.findViewById(R.id.img_camera);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                dialog.dismiss();
            }
        });
        TextView cameraTxt = dialog.findViewById(R.id.txt_camera);
        cameraTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
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
     * Lets the user to take a picture using the phone's camera
     */
    private void takePicture(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, RESULT_CODE_CAMERA);
    }

    /**
     * Lets the user choose a picture from their gallery
     */
    private void chooseImage(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.
                Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }


    //--------- Fragment Methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(requestCode == RESULT_CODE_CAMERA || requestCode == RESULT_CODE_GALLERY){
            Uri selectedImage = imageReturnedIntent.getData();
            entryImage.setImageURI(selectedImage);
        }
    }

    public static NewEntryFragment newInstance(EntryHandler handler){
        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY_HANDLER, handler);

        NewEntryFragment fragment = new NewEntryFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
