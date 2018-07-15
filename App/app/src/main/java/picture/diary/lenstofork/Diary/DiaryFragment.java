package picture.diary.lenstofork.Diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import Entry.Entry;
import Entry.EntryHandler;
import picture.diary.lenstofork.Diary.Utils.DatabaseHandler;
import picture.diary.lenstofork.Diary.Utils.SquareImageView;
import picture.diary.lenstofork.R;

public class DiaryFragment extends Fragment {
    // widgets
    private TextView[] titles = new TextView[6];
    private TextView[] captions = new TextView[6];
    private SquareImageView[] images = new SquareImageView[6];
    private View[] containers = new View[6];

    // other variables
    private static EntryHandler entryHandler;
    private int lastValidEntry = -1;

    // flags
    private static final String ARG_ENTRY_HANDLER = "ARG_ENTRY_HANDLER";
    public static final String TAG = "DIARY_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        //------ Get EntryHandler for date given
        handleExtras();

        setUpView(view);

        // resize images
        view.post(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
                    images[i].resizeImage(getView());
                }
            }
        });

        return view;
    }

    //--------- Helper Methods

    /**
     * Sets up the widgets within each of the entry's custom_entry layout. It initializes all of the
     * title TextViews, note TextViews, and image ImageViews.
     *
     * @param parent the parent View of the layout for this fragment
     */
    private void setUpView(View parent){
        // set up views holding custom_entry layout
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
            images[i] = (SquareImageView) containers[i].findViewById(R.id.img);

            // set up OnClickListeners for each view & disable clicking
            containers[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to add new entry page
                    Intent intent = EntryActivity.newInstance(NewEntryFragment.TAG,
                            entryHandler.getStringDate(), getActivity());
                    startActivity(intent);
                }
            });
            containers[i].setClickable(false);

            //------ Set Values for Entries
            Entry currentEntry = entryHandler.getEntry(i);
            if(currentEntry != null){
                // set title
                titles[i].setText(currentEntry.getTitle());
                captions[i].setText(currentEntry.getCaption());

                // check if the image stored in currentEntry exists
                String filepath = currentEntry.getImageFilePath();
                if(!new File(filepath).exists()){
                    //TODO: make a default image
                    // image does not exist, so use default
                    images[i].setImageResource(R.drawable.ic_launcher_foreground);
                }
                else{
                    images[i].setImageBitmap(currentEntry.getImage());
                }
            }
            else{
                if(entriesAreLogged){
                    // save the position of the last valid entry
                    lastValidEntry = i;
                    entriesAreLogged = false;
                    containers[i].setClickable(true);


                    // set default picture for adding a new pic
                    images[i].setImageResource(R.drawable.add_entry_teal);
                    titles[i].setText("Add New Entry");
                }
                else{
                    containers[i].setVisibility(View.GONE);
                }
            }
        }
    }

    private void handleExtras(){
        String dateString = getArguments().getString(ARG_ENTRY_HANDLER);
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        if(databaseHandler.doesEntryHandlerExist(dateString)){
            // get EntryHandler from database
            entryHandler = databaseHandler.getEntryHandler(dateString);
            entryHandler.getNumberOfEntries();
        }
        else{
            // create EntryHandler for given date
            entryHandler = new EntryHandler(dateString);
        }
    }

    /**
     * Handles which box displays "Add New Entry" and sets the old one to be unable to click on,
     * while making the new box clickable
     *
     * @param position the position of the new "Add New Entry" box
     */
    private void setAddNewEntryBox(int position){
        // set the old "Add New Entry" box to be un-clickable
        containers[position-1].setClickable(false);

        // set new "Add New Entry" box to clickable
        containers[position].setClickable(true);
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
