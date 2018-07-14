package picture.diary.lenstofork.Diary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import picture.diary.lenstofork.Diary.Utils.FragmentController;
import picture.diary.lenstofork.R;

public class EntryActivity extends AppCompatActivity {
    private String dateString;

    // contsants
    private static final String EXTRA_FRAGMENT_TAG = "FRAGMENT TAG";
    private static final String EXTRA_ENTRY_HANDLER = "ENTRY_HANDLER";
    private static int REQUEST_CODE_READ_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        askForPermission();

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Lens to Fork");

        //------ Set Up Fragment to Displays
        Intent extras = getIntent();

        dateString = extras.getStringExtra(EXTRA_ENTRY_HANDLER);
        String fragmentTag = extras.getStringExtra(EXTRA_FRAGMENT_TAG);

        final FragmentController controller = new FragmentController(getSupportFragmentManager());
        // determine which fragment to display
        if(fragmentTag.equals(NewEntryFragment.TAG)){
            controller.openFragment(NewEntryFragment.newInstance(dateString), NewEntryFragment.TAG);
        }
    }

    private void askForPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_PERMISSION);
        }
    }

    /**
     * Returns an intent that can start this activity and adds the extras needed to start this
     * activity
     *
     * @param fragmentTag needed to know which fragment to display
     * @param dateString needed to pass on the EntryHandler's id
     * @param activity the activity that is starting this activity
     * @return
     */
    public static Intent newInstance(String fragmentTag, String dateString, Activity activity){
        // put extras in an intent
        Intent intent = new Intent(activity, EntryActivity.class);
        intent.putExtra(EXTRA_FRAGMENT_TAG, fragmentTag);
        intent.putExtra(EXTRA_ENTRY_HANDLER, dateString);

        return intent;
    }
}
