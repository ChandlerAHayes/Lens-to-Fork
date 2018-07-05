package picture.diary.lenstofork.Diary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import picture.diary.lenstofork.R;

public class DiaryFragment extends Fragment {
    // widgets
    private TextView[] titles = new TextView[6];
    private TextView[] notes = new TextView[6];
    private ImageView[] images = new ImageView[6];
    private View[] containers = new View[6];

    // other variables
    private EntryHandler entries;

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
            notes[i] = containers[i].findViewById(R.id.txt_note);
            images[i] = containers[i].findViewById(R.id.img);

            // set values for entries
            if(entries.getEntry(i) != null){
                titles[i].setText(entries.getEntry(i).getTitle());
                notes[i].setText(entries.getEntry(i).getNote());
                images[i].setImageDrawable(entries.getEntry(i).getImage());
            }
            else{
                if(entriesAreLogged){
                    entriesAreLogged = false;

                    // set default picture for adding a new pic
                    images[i].setImageResource(R.drawable.plus);
                }
                else{
                    containers[i].setVisibility(View.GONE);
                }
            }
        }
    }

    public static DiaryFragment newInstance(EntryHandler entries){
        DiaryFragment fragment = new DiaryFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY, entries);
        fragment.setArguments(args);

        return fragment;
    }
}
