package picture.diary.lenstofork.Diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import Entry.EntryHandler;
import picture.diary.lenstofork.Diary.Utils.FragmentController;
import picture.diary.lenstofork.R;

public class EntryActivity extends AppCompatActivity {
    // variables
    private EntryHandler handler;

    // contsants
    private static final String EXTRA_FRAGMENT_TAG = "FRAGMENT TAG";
    private static final String EXTRA_ENTRY_HANDLER = "ENTRY_HANDLER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Lens to Fork");

        //------ Set Up Fragment to Dispalys
        Intent extras = getIntent();
        handler = (EntryHandler) extras.getSerializableExtra(EXTRA_ENTRY_HANDLER);
        String fragmentTag = extras.getStringExtra(EXTRA_FRAGMENT_TAG);

        final FragmentController controller = new FragmentController(getSupportFragmentManager());
        // determine which fragment to dispaly
        if(fragmentTag.equals(NewEntryFragment.TAG)){
            controller.openFragment(NewEntryFragment.newInstance(handler), NewEntryFragment.TAG);
        }
    }

    /**
     * Returns an intent that can start this activity and adds the extras needed to start this
     * activity
     *
     * @param fragmentTag needed to know which fragment to display
     * @param handler needed to pass on the EntryHandler
     * @param activity the activity that is starting this activity
     * @return
     */
    public static Intent newInstance(String fragmentTag, EntryHandler handler, Activity activity){
        // put extras in an intent
        Intent intent = new Intent(activity, EntryActivity.class);
        intent.putExtra(EXTRA_FRAGMENT_TAG, fragmentTag);
        intent.putExtra(EXTRA_ENTRY_HANDLER, handler);

        return intent;
    }
}
