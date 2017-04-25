package com.get_table.biwiremob17_l6bc_1;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by albertcahyawan on 4/4/2017.
 */

public class RegisterFragment extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.register_layout, container, false);

        //Allow Action Bar
        setHasOptionsMenu(true);


        return myView;
    }

    //Select List for Action Bar
    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.register_menu, menu);
    }

    //Action when the bar is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.nav_Return:
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
