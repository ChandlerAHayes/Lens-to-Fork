package picture.diary.lenstofork.Diary;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;

import picture.diary.lenstofork.Diary.Utils.DatabaseHandler;
import picture.diary.lenstofork.R;

public class DiaryActivity extends AppCompatActivity {
    // widgets
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        //-------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //------- Widgets
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        });

        // Make data that can be added to the database
        EntryHandler handler0 = new EntryHandler("06-25-2018");
        Entry entry0 = new Entry("filepath0", "title0", "title0");
        Entry entry1 = new Entry("filepath1", "title1", "note1");
        Entry entry2 = new Entry("filepath2", "title2", "note2");
        Entry entry3 = new Entry("filepath3", "title3", "note3");

        handler0.addEntry(entry0);
        handler0.addEntry(entry1);
        handler0.addEntry(entry2);
        handler0.addEntry(entry3);

        // Add to database
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        List<EntryHandler> list = dbHandler.getAllEntryHandlers();
        List<Entry> list1 = dbHandler.getAllEntries();

        dbHandler.addEntries(handler0);
        dbHandler.deleteEntryHandler(handler0);

        list = dbHandler.getAllEntryHandlers();
        list1 = dbHandler.getAllEntries();
        int x=0;
    }
}

/*
*
* https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot
*
public Bitmap screenShot(View view) {
    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
            view.getHeight(), Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    view.draw(canvas);
    return bitmap;
}
*/