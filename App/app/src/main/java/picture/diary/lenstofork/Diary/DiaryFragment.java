package picture.diary.lenstofork.Diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import Entry.CaptionColor;
import Entry.Entry;
import Entry.EntryHandler;
import picture.diary.lenstofork.R;
import picture.diary.lenstofork.Utils.DatabaseHandler;
import picture.diary.lenstofork.Utils.ImageHandler;

public class DiaryFragment extends Fragment {
    // widgets
    private TextView[] titles = new TextView[6];
    private TextView[] captions = new TextView[6];
    private ImageView[] images = new ImageView[6];
    private View[] containers = new View[6];

    // variables
    private static EntryHandler entryHandler;
    DatabaseHandler database;


    // constants
    public static final String TAG = "DIARY_FRAGMENT";
    private static final String ARG_ENTRY_HANDLER = "ARG_ENTRY_HANDLER";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_diary, container, false);
        database = new DatabaseHandler(getContext());

        //------ Get EntryHandler for date given
        handleArguments();

        setUpView(view);

        // check if dimensions need to be stored or updated
        if(database.doesDimensionsExists(TAG)){
           // check if values have changed compared to database
           view.post(new Runnable() {
               @Override
               public void run() {
                    int[] dimensions = database.getDimensions(TAG);
                    // if width or height are different update values
                    if(dimensions[0] != view.getWidth() || dimensions[1] != view.getHeight()){
                        database.updateDimensions(TAG, view.getWidth(), view.getHeight());
                    }
               }
           });
        }
        else{
            view.post(new Runnable() {
                @Override
                public void run() {
                    // values need to be added to the database
                    database.addDimensions(TAG, view.getWidth(), view.getHeight());
                }
            });
        }

        return view;
    }

    //--------- Methods for Setting Up the Views

    /**
     * Sets up the widgets within each of the entry's custom_entry layout. It initializes all of the
     * title TextViews, note TextViews, and image ImageViews.
     *
     * @param parent the parent View of the layout for this fragment
     */
    private void setUpView(View parent){
        // set up the containers holding custom_entry layout
        containers[0] = parent.findViewById(R.id.entry_0);
        containers[1] = parent.findViewById(R.id.entry_1);
        containers[2] = parent.findViewById(R.id.entry_2);
        containers[3] = parent.findViewById(R.id.entry_3);
        containers[4] = parent.findViewById(R.id.entry_4);
        containers[5] = parent.findViewById(R.id.entry_5);

        boolean entriesAreLogged = true;    // indicates if the current iteration has data for entryHandler
        for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
            // initialize widgets
            titles[i] = (TextView) containers[i].findViewById(R.id.txt_title);
            captions[i] = (TextView) containers[i].findViewById(R.id.txt_note);
            images[i] = (ImageView) containers[i].findViewById(R.id.img);

            //------ Set Values for Entries
            Entry currentEntry = entryHandler.getEntry(i);
            if(currentEntry != null){
                // get currentEntry's data and fill it into the current container[i]
                fillInEntryData(currentEntry, i);
            }
            else{
                if(entriesAreLogged){
                    /**
                     * Current index is the first entry that is null. Therefore, make this index
                     * display the add new entry image. Set this container
                     */
                    entriesAreLogged = false;

                    setUpAddEntryContainer(i);
                }
                else{
                    // hide additional null entries
                    containers[i].setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * With the given entry, it extracts its data and inserts it into the TextViews & ImageView
     * that correspond to the given index of the container it belongs in.
     *
     * @param entry the entry to extract its data and insert it into the TextViews & ImageView
     * @param index the index that the entry belongs in
     */
    private void fillInEntryData(Entry entry, final int index){
        // insert title and caption
        titles[index].setText(entry.getTitle());
        captions[index].setText(entry.getCaption());
        if(entry.getCaptionColor().equals(CaptionColor.WHITE)){
            captions[index].setTextColor(ContextCompat.getColor(getContext(),
                    android.R.color.white));
        }
        else{
            captions[index].setTextColor(ContextCompat.getColor(getContext(),
                    android.R.color.black));
        }

        // check if the image stored in currentEntry exists
        String filepath = entry.getImageFilePath();
        if(!new File(filepath).exists()){
            //TODO: make a default image
            // image does not exist, so use default
            images[index].setImageResource(R.drawable.ic_launcher_foreground);
        }
        else{
            // resize and insert image into ImageView
            int[] results = database.getDimensions(TAG);
            Double widthDouble = results[0] * 0.48;
            Double heightDouble = results[1] * 0.31;
            int minDimension = Math.min(widthDouble.intValue(), heightDouble.intValue());
            new ImageHandler(getActivity(), TAG).loadIntoImageView(minDimension, minDimension,
                    filepath, images[index]);
        }

        // add onClickListener to view Entry Details or Edit
        containers[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EntryActivity.newInstance(DetailFragment.TAG,
                        entryHandler.getStringDate(),index, getActivity());
                startActivity(intent);
            }
        });
    }

    /**
     * Set up the container that will hold the add a new entry box
     *
     * @param index the index of the container that will hold the new entry box
     */
    private void setUpAddEntryContainer(final int index){
        // set default picture for adding a new pic
        images[index].setImageResource(R.drawable.add_entry_teal);

        // go to NewEntryFragment, to create a new Entry
        containers[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to add new entry page
                Intent intent = EntryActivity.newInstance(NewEntryFragment.TAG,
                        entryHandler.getStringDate(), getActivity());
                startActivity(intent);
            }
        });
    }

    //--------- Helper Methods

    /**
     * Extracts the arguments that this fragment was initialized with
     */
    private void handleArguments(){
        String dateString = getArguments().getString(ARG_ENTRY_HANDLER);
        if(database.doesEntryHandlerExist(dateString)){
            // get EntryHandler from database
            entryHandler = database.getEntryHandler(dateString);
            entryHandler.getNumberOfEntries();
        }
        else{
            // create EntryHandler for given date
            entryHandler = new EntryHandler(dateString);
        }
    }

    //------- Fragment Methods
    public static DiaryFragment newInstance(String entryHandlerDate){
        DiaryFragment fragment = new DiaryFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_HANDLER, entryHandlerDate);
        fragment.setArguments(args);

        return fragment;
    }
}
