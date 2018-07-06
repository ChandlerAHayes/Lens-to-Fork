package picture.diary.lenstofork.Diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import Entry.EntryHandler;
import picture.diary.lenstofork.R;

public class DiaryFragment extends Fragment {
    // widgets
    private TextView[] titles = new TextView[6];
    private TextView[] captions = new TextView[6];
    private ImageView[] images = new ImageView[6];
    private View[] containers = new View[6];

    // other variables
    private static EntryHandler entries;
    private int lastValidEntry = -1;

    // flags
    private static final String ARG_ENTRY = "ARG ENTRY";
    public static final String TAG = "DIARY_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        entries = (EntryHandler) getArguments().getSerializable(ARG_ENTRY);
        setUpView(view);

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

        boolean entriesAreLogged = true;    // indicates if the current iteration has data for entries
        for(int i=0; i<EntryHandler.ENTRY_LIMIT; i++){
            // initialize widgets
            titles[i] = containers[i].findViewById(R.id.txt_title);
            captions[i] = containers[i].findViewById(R.id.txt_note);
            images[i] = containers[i].findViewById(R.id.img);

            // set up OnClickListeners for each view & disable clicking
            containers[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to add new entry page
                    Intent intent = EntryActivity.newInstance(NewEntryFragment.TAG, entries,
                            getActivity());
                    startActivity(intent);
                }
            });
            containers[i].setClickable(false);


            // set values for entries
            if(entries.getEntry(i) != null){
                titles[i].setText(entries.getEntry(i).getTitle());
                captions[i].setText(entries.getEntry(i).getCaption());
                images[i].setImageDrawable(entries.getEntry(i).getImage());
            }
            else{
                if(entriesAreLogged){
                    // save the position of the last valid entry
                    lastValidEntry = i;
                    entriesAreLogged = false;
                    containers[i].setClickable(true);


                    // set default picture for adding a new pic
                    images[i].setImageResource(R.drawable.add_entry_123);
                    titles[i].setText("Add New Entry");
                }
                else{
                    containers[i].setVisibility(View.GONE);
                }
            }
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
    public static DiaryFragment newInstance(EntryHandler entries){
        DiaryFragment fragment = new DiaryFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY, entries);
        fragment.setArguments(args);

        return fragment;
    }
}
