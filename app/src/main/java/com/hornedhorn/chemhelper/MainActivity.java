package com.hornedhorn.chemhelper;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.fragments.CompoundFragment;
import com.hornedhorn.chemhelper.fragments.CompoundReciverFragment;
import com.hornedhorn.chemhelper.fragments.InfoFragment;
import com.hornedhorn.chemhelper.fragments.MainFragment;
import com.hornedhorn.chemhelper.fragments.NewCompoundFragment;
import com.hornedhorn.chemhelper.fragments.ReactionFragment;
import com.hornedhorn.chemhelper.fragments.TableFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private Fragment currentFragment;

    private MainFragment mainFragment;
    private CompoundFragment compoundFragment;
    private ReactionFragment reactionFragment;
    private TableFragment tableFragment;
    private InfoFragment infoFragment;
    private NewCompoundFragment newCompoundFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainFragment = new MainFragment();
        compoundFragment = new CompoundFragment();
        reactionFragment = new ReactionFragment();
        tableFragment = new TableFragment();
        infoFragment = new InfoFragment();
        newCompoundFragment = new NewCompoundFragment();

        setContentFragment( mainFragment, false );

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count != 0) {
                back();
            }else if (currentFragment != mainFragment) {
                toolbar.setTitle("ChemHelper");
                setContentFragment(mainFragment, false);
            }else {
                super.onBackPressed();
            }
        }
    }

    public void back(){
        getSupportFragmentManager().popBackStack();
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_solutions)
            setReactionFragment();
        else if (id == R.id.nav_compounds) {
            setCompoundSearchFragment();
        } else if (id == R.id.nav_periodicTable) {
            setTableFragment();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setContentFragment(Fragment fragment, boolean addToBackStack){
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment);
        if (addToBackStack)
            transaction.addToBackStack(null);
        else
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.commit();
        currentFragment = fragment;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("intent", intent.getAction());
        if (intent.getAction().equals( Intent.ACTION_SEARCH )) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            compoundFragment.searchCompound(query);
        }
        if (intent.getAction().equals( Intent.ACTION_VIEW)) {
            Log.e("intent", " " + intent.getData().toString());
            compoundFragment.selectSuggestion(intent.getDataString());
        }
    }

    public void setTableFragment(){
        setContentFragment(tableFragment, false);
        toolbar.setTitle("Periodic table");
    }

    public void setReactionFragment(){
        toolbar.setTitle("Reaction");
        setContentFragment(reactionFragment, false);
    }

    public void setCompoundSearchFragment(){
        setCompoundSearchFragment(infoFragment);
    }

    public void setCompoundSearchFragment(CompoundReciverFragment compoundReceiver){
        boolean info = compoundReceiver == infoFragment;
        if (info)
            toolbar.setTitle("Compounds");
        compoundFragment.setReceiverFragment(compoundReceiver, info);
        setContentFragment(compoundFragment, !info);

    }

    public void setInfoFragment(Compound compound){
        setContentFragment(infoFragment, true);
        infoFragment.setCompound(compound);
    }


    public void setNewCompoundFragment() {
        setEditCompoundFragment(null);
    }

    public void setEditCompoundFragment(Compound compound) {
        setContentFragment(newCompoundFragment, true);
        newCompoundFragment.setCompound(compound);
    }
}
