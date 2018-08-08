package picture.diary.lenstofork.Diary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

import picture.diary.lenstofork.Diary.Entry.Entry;
import picture.diary.lenstofork.Diary.Entry.EntryHandler;
import picture.diary.lenstofork.R;
import picture.diary.lenstofork.Utils.DatabaseHandler;
import picture.diary.lenstofork.Utils.ImageHandler;

public class DetailFragment extends Fragment {
    //------ Widgets
    private ImageView image;
    private TextView titleTxt;
    private TextView headerCaption;
    private TextView captionTxt;
    private TextView headerDescription;
    private TextView descriptionTxt;

    // variables
    private DatabaseHandler database;
    private EntryHandler entryHandler;
    private Entry entry;
    private int position;
    private ImageHandler imageHandler;

    // constants
    public static final String TAG = "DetailFragment";
    private static final String ARG_ENTRY_HANDLER = "Arg picture.diary.lenstofork.Diary.Entry Handler";
    private static final String ARG_ENTRY_POSITION = "Arg picture.diary.lenstofork.Diary.Entry Position";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageHandler = new ImageHandler(getActivity(), TAG);

        // get picture.diary.lenstofork.Diary.Entry from database
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

        //-------- Toolbar Setup
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setHomeAsUpIndicator(R.drawable.up_navigation_white);

        final View view = inflater.inflate(R.layout.fragment_detail, container, false);


        //------- Set Up Widgets
        titleTxt = (TextView) view.findViewById(R.id.txt_title);
        headerCaption = (TextView) view.findViewById(R.id.header_caption);
        captionTxt = (TextView) view.findViewById(R.id.txt_caption);
        headerDescription = (TextView) view.findViewById(R.id.header_description);
        descriptionTxt = (TextView) view.findViewById(R.id.txt_description);

        configureTextViews();

        image = (ImageView) view.findViewById(R.id.img);

        if(database.doesDimensionsExists(TAG)){
            loadImage(entry.getImageFilePath());

            view.post(new Runnable() {
                @Override
                public void run() {
                    int[] dimensions = database.getDimensions(TAG);
                    // if width or height are different update values & view has positive dimensions
                    if((dimensions[0] != view.getWidth() || dimensions[1] != view.getHeight())
                            && (view.getWidth() > 0 && view.getHeight() > 0)){
                        database.updateDimensions(TAG, view.getWidth(), view.getHeight());
                    }
                }
            });
        }
        else{
            view.post(new Runnable() {
                @Override
                public void run() {
                    if(view.getWidth() > 0 && view.getHeight() > 0){
                        // values need to be added to the database
                        database.addDimensions(TAG, view.getWidth(), view.getHeight());

                        loadImage(entry.getImageFilePath());
                    }
                }
            });
        }

        return view;
    }

    //-------- Helper Methods

    /**
     * Configure the TextViews to show picture.diary.lenstofork.Diary.Entry info
     */
    private void configureTextViews(){
        // check title
        if(entry.getTitle().equals("")){
            // there's no title, so make it invisible
            titleTxt.setVisibility(View.GONE);
        }
        else {
            // insert title
            titleTxt.setText(entry.getTitle());
        }

        // check caption
        if(entry.getCaption().equals("")){
            // there's no caption, so make it invisible
            captionTxt.setVisibility(View.GONE);
            headerCaption.setVisibility(View.GONE);
        }
        else{
            // insert caption
            captionTxt.setText(entry.getCaption());
        }

        // check description
        if(entry.getDescription().equals("")){
            // there's no description, so make it invisible
            descriptionTxt.setVisibility(View.GONE);
            headerDescription.setVisibility(View.GONE);
        }
        else{
            // insert description
            descriptionTxt.setText(entry.getDescription());
        }
    }

    private void updateUI(){
        // update the entrHandler and entry
        entryHandler = database.getEntryHandler(entryHandler.getStringDate());
        entry = entryHandler.getEntry(position);

        // update view
        loadImage(entry.getImageFilePath());
        configureTextViews();
    }

    //---------- Editing Entries Methods

    /**
     * Deletes the entry from the database and returns to DiaryActivity after deletion.
     */
    private void deleteEntry(){
        // remove entry
        entryHandler.removeEntry(position);
        // if there are no entries left, delete entryHandler, otherwise update database
        if(entryHandler.getNumberOfEntries() == 0){
            database.deleteEntryHandler(entryHandler);
        }
        else{
            database.updateEntryHandler(entryHandler);
        }

        // return to DiaryActivity
        getActivity().onBackPressed();
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

    /**
     * Determines the dimensions of the image and loads it into the image view
     */
    private void loadImage(String filepath){
        int[] dimensions = database.getDimensions(TAG);
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
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.id.edit:
                Intent intent = EntryFormsActivity.newIntent(getActivity(),
                        entryHandler.getStringDate(), position);
                startActivity(intent);
                return true;
            case R.id.delete:
                confirmDelete();
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onResume() {
        // check if entry has been updated in the database
        Entry updatedEntry = database.getEntryHandler(entryHandler.getStringDate())
                .getEntry(position);
        if(!entry.equals(updatedEntry)){
            updateUI();
        }

        super.onResume();
    }
}
