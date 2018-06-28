package picture.diary.lenstofork.Diary;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class DiaryFragment extends Fragment {
    private EntryHandler entries;

    // flags
    private static final String ARG_ENTRY = "ARG ENTRY";

    public static DiaryFragment newInstance(EntryHandler entries){
        DiaryFragment fragment = new DiaryFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY, entries);
        fragment.setArguments(args);

        return fragment;
    }
}
