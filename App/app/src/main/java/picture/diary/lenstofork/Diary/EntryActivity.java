package picture.diary.lenstofork.Diary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import picture.diary.lenstofork.Diary.Entry.EntryHandler;
import picture.diary.lenstofork.R;
import picture.diary.lenstofork.Utils.DatabaseHandler;

public class EntryActivity extends AppCompatActivity {
    private String dateString;
    private ViewPager viewPager;

    // constants
    private static final String EXTRA_FRAGMENT_TAG = "Extra Fragment Tag";
    private static final String EXTRA_ENTRY_HANDLER = "Extra picture.diary.lenstofork.Diary.Entry Handler";
    private static final String EXTRA_ENTRY_POSITION = "Extra picture.diary.lenstofork.Diary.Entry Position";
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

        Intent extras = getIntent();
        dateString = extras.getStringExtra(EXTRA_ENTRY_HANDLER);
        DatabaseHandler database = new DatabaseHandler(this);
        final EntryHandler entryHandler = database.getEntryHandler(dateString);


        viewPager = (ViewPager) findViewById(R.id.main_content);
        FragmentManager manager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentPagerAdapter(manager) {
            @Override
            public Fragment getItem(int position) {
                Fragment detailFragment = DetailFragment.newInstance(position, dateString);
                return detailFragment;
            }

            @Override
            public int getCount() {
                return entryHandler.getNumberOfEntries();
            }
        });
        viewPager.setCurrentItem(extras.getIntExtra(EXTRA_ENTRY_POSITION, 0));
    }

    /**
     * Asks the user for permission to read external storage so that the app can copy the image the
     * user picks from gallery
     */
    private void askForPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_PERMISSION);
        }
    }

    public static Intent newInstance(String dateString, int entryPosition, Activity activity)
    {
        // put extras in an intent
        Intent intent = new Intent(activity, EntryActivity.class);
        intent.putExtra(EXTRA_ENTRY_HANDLER, dateString);
        intent.putExtra(EXTRA_ENTRY_POSITION, entryPosition);

        return intent;
    }

    @Override
    public void onBackPressed() {
        // finish activity if on the DetailFragment (backStack will have a count of 1)
        FragmentManager manager = getSupportFragmentManager();
        if(manager.getBackStackEntryCount() == 1){
            finish();
        }
        else{
            super.onBackPressed();
        }
    }
}
