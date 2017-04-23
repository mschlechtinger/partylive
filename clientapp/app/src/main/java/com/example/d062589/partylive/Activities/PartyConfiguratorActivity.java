package com.example.d062589.partylive.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.Models.User;
import com.example.d062589.partylive.PartyTabLayout.BringalongAddFragment;
import com.example.d062589.partylive.PartyTabLayout.BringalongListFragment;
import com.example.d062589.partylive.PartyTabLayout.GuestAddFragment;
import com.example.d062589.partylive.PartyTabLayout.GuestListFragment;
import com.example.d062589.partylive.PartyTabLayout.InfoAddFragment;
import com.example.d062589.partylive.PartyTabLayout.InfoListFragment;
import com.example.d062589.partylive.PartyTabLayout.MyFragmentPagerAdapter;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.MyListener;
import com.example.d062589.partylive.Utils.PrefUtils;
import com.example.d062589.partylive.Utils.RecyclerClickListener;
import com.example.d062589.partylive.Utils.RecyclerTouchListener;
import com.example.d062589.partylive.Utils.RestClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by D062589 on 15.03.2017.
 */

public class PartyConfiguratorActivity extends AppCompatActivity implements
        BringalongAddFragment.BringalongDialogListener,
        GuestAddFragment.GuestDialogListener,
        InfoAddFragment.InfoDialogListener {

    private PrefUtils prefUtils;
    private User user;
    private String sessionCookie;
    private String userId;
    private Location mLastKnownLocation;
    private FloatingActionButton fab;
    int currentPage = 0;
    MyFragmentPagerAdapter pagerAdapter;
    ViewPager viewPager;
    Party newParty;

    boolean bringalongClick = false;
    boolean guestClick = false;


    /**
     * Hide Navbar and Statusbar for Fullscreen Map
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_configurator);

        // Get Intent information from Map Screen
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLastKnownLocation = extras.getParcelable("LOCATION");
            Gson gson = new Gson();
            newParty = gson.fromJson(extras.getString("PARTY_JSON"), Party.class);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        // TODO: CHECK IF PARTY OBJECT IS EMPTY
        createViewPager();
        // Create new Party object to be build
        if (newParty == null) {
            newParty = new Party();
        }

        prefUtils = PrefUtils.getInstance(getApplicationContext());
        user = prefUtils.getCurrentUser();
        sessionCookie = user.session;
        userId = user.userID;
    }

    /**
     * Creates the viewPager in edit mode and passes the binding
     */
    private void createViewPager(Party party) {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                PartyConfiguratorActivity.this, party);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Creates the viewPager in new mode
     */
    private void createViewPager() {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                PartyConfiguratorActivity.this);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: fab.setImageResource(R.drawable.user_plus);break;
                    case 1: fab.setImageResource(R.drawable.cart_plus);break;
                    case 2: fab.setImageResource(R.drawable.pencil);break;
                    default: fab.setImageResource(R.drawable.user_plus);break;
                }
                // Store active page in variable
                currentPage = position;
            }
        });


    }

    /**
     * FAB context specific actions
     */
    public void fabAction(View view) {
        switch (currentPage) {
            case 0:
                addGuest();
                break;
            case 1:
                addBringalong();
                break;
            case 2:
                editDescription();
                break;
            default:
                break;
        }
    }


    private void addGuest() {
        FragmentManager fm = getSupportFragmentManager();
        GuestAddFragment.newInstance(newParty, userId, sessionCookie).show(fm,"guest");
    }

    private void editGuest(int position) {
        FragmentManager fm = getSupportFragmentManager();
        GuestAddFragment.newInstance(newParty, position, userId, sessionCookie).show(fm,"guest");
    }


    private void addBringalong() {
        FragmentManager fm = getSupportFragmentManager();
        BringalongAddFragment.newInstance(newParty).show(fm,"bringalong");
    }

    private void editBringalong(int position) {
        FragmentManager fm = getSupportFragmentManager();
        BringalongAddFragment.newInstance(newParty, position).show(fm,"bringalong");
    }


    private void editDescription() {
        // TODO: Get current description to edit
        FragmentManager fm = getSupportFragmentManager();
        Party.PartyLocation location =
                new Party.PartyLocation(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude());
        InfoAddFragment.newInstance(location, newParty).show(fm,"info");
    }




    /**
     * Add Onclicklistener to recyclerRow to edit stuff
     * @param activePage current Fragment in ViewPager
     */
    private void bringalongRowOnclick(Fragment activePage) {
        // Set OnclickListener to bringalong item to edit item
        ((BringalongListFragment)activePage)
                .mRecyclerView
                .addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                        ((BringalongListFragment)activePage)
                                .mRecyclerView, new RecyclerClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        editBringalong(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {}
                }));
        bringalongClick = true;
    }


    @Override
    public void onBringalongDialogPositiveClick(Party currentParty) {
        if (currentParty != null) {
            newParty.addBringItems(currentParty.getBringitems());
            Fragment activePage = getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.viewpager + ":"
                            + viewPager.getCurrentItem());
            // based on the current position you can then cast the page to the correct
            // class and call the method:
            if (viewPager.getCurrentItem() == 1 && activePage != null) {
                ((BringalongListFragment)activePage).refreshBringItemRows(newParty);
                if (!bringalongClick) {
                    bringalongRowOnclick(activePage);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.fill_out_error),
                    Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Add Onclicklistener to recyclerRow to edit stuff
     * @param activePage current Fragment in ViewPager
     */
    private void guestRowOnclick(Fragment activePage) {
        // Set OnclickListener to bringalong item to edit item
        ((GuestListFragment)activePage)
                .mRecyclerView
                .addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                        ((GuestListFragment)activePage)
                                .mRecyclerView, new RecyclerClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        editGuest(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {}
                }));
        guestClick = true;
    }

    @Override
    public void onGuestDialogPositiveClick(Party currentParty) {
        if (currentParty != null) {
            newParty.addGuests(currentParty.getGuests());

            Fragment activePage = getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.viewpager + ":"
                            + viewPager.getCurrentItem());
            // based on the current position you can then cast the page to the correct
            // class and call the method:
            if (viewPager.getCurrentItem() == 0 && activePage != null) {
                ((GuestListFragment) activePage).refreshGuestRows(newParty);
                if (!guestClick) {
                    guestRowOnclick(activePage);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.fill_out_error),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }


    @Override
    public void onInfoDialogPositiveClick(String description, Party.PartyLocation location, String locationText) {
        if (!locationText.equals("") && location != null) {
            Fragment activePage = getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.viewpager + ":"
                            + viewPager.getCurrentItem());
            // based on the current position you can then cast the page to the correct
            // class and call the method:
            if (viewPager.getCurrentItem() == 2 && activePage != null) {
                ((InfoListFragment) activePage).addInfo(location, description);
                showSnackbar();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.fill_out_error),
                    Toast.LENGTH_SHORT)
                    .show();
        }
        // Store Description & location in Party Object
        newParty.setDescription(description);
        newParty.setLocation(location);
    }


    private void showSnackbar() {
        CoordinatorLayout myCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_root);
        Snackbar.make(myCoordinatorLayout, "Party ready!", Snackbar.LENGTH_INDEFINITE)
                .setAction("CREATE PARTY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createParty(view);
                                startActivity(new Intent(PartyConfiguratorActivity.this, MainActivity.class));
                            }
                }).show();
    }



    /**
     * Send Post Request containing the Party to the server
     */
    public void createParty(View view) {
        String partyJson = new Gson().toJson(newParty);
        try {
            JSONObject payload = new JSONObject(partyJson);

            // StartDate
            SimpleDateFormat startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ms'Z'"); //2017-03-31T10:50:42.389Z
            Date date = new Date();
            System.out.println(startDate.format(date));
            payload.put("startDate", startDate.format(date));


            RestClient.getInstance().post(userId, sessionCookie, payload, "/events", new MyListener<JSONObject>()
            {
                @Override
                public void getResult(JSONObject response) {
                    if (response != null)
                    {
                        Toast.makeText(getApplicationContext(), "Party created successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error while creating the party.", Toast.LENGTH_LONG).show();
                    }
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
