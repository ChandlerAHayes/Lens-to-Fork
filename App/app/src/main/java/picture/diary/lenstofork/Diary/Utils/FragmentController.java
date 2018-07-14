package picture.diary.lenstofork.Diary.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import picture.diary.lenstofork.R;

public class FragmentController {
    FragmentManager manager;

    public FragmentController(FragmentManager manager){
        this.manager = manager;
    }

    /**
     * Adds the given fragment on top of the fragment stack. It ensures that duplicate entries are
     * not added.
     *
     * @param fragment the fragment to add on top of the stack
     * @param tag the tag corresponding to the fragment
     */
    public void openFragment(Fragment fragment, String tag){
        FragmentTransaction transaction = manager.beginTransaction();

        // if there's nothing in the back stack then add fragment
        if(manager.getBackStackEntryCount() == 0 ){
            transaction.add(R.id.main_content, fragment, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        }
        else{
            //get last entry on back stack to ensure we're not creating duplicate entries
            int index = manager.getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry lastEntry = manager.getBackStackEntryAt(index);

            //see if last fragment on back stack is equal to new fragment
            if(!lastEntry.getName().equals(tag)){
                transaction.replace(R.id.main_content, fragment, tag);
                transaction.addToBackStack(tag);
                transaction.commit();

                // *may need to remove/clear back stack depending on what fragment is being added*
            }

        }
    }

    public void popFragment(String tag){
        int index = manager.getBackStackEntryCount() - 1;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(manager.findFragmentByTag(tag));
    }

}
