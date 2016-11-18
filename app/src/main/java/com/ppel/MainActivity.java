package com.ppel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppel.login.LoginWebActivity;
import com.ppel.login.LogoutTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    NavigationView navigationView;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        relativeLayout = (RelativeLayout) findViewById(R.id.content_main);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set email dynamically
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email = extras.getString("email");
            View headerView = navigationView.getHeaderView(0);

            Drawable drawableEmblem = getDrawable (R.drawable.wsu_emblem_drawable);
            drawableEmblem = resize(drawableEmblem );
            ((ImageView) headerView.findViewById(R.id.navigation_icon)).setImageDrawable(drawableEmblem );

            ((TextView) headerView.findViewById(R.id.email)).setText(email);
            ((TextView) headerView.findViewById(R.id.userName)).setText(email.split("@")[0].replace('.', ' '));
        }
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 200, 200, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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



        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, ExpandableLayoutMaterialDesign.class);
            intent.putExtra("email", email);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            // this is PPEL's "Info" page or home page
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        } else if (id == R.id.nav_manage){
            /*CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);*/
            LogoutTask logoutTask = new LogoutTask();
            try {
                logoutTask.execute(" https://debianvm.eecs.wsu.edu/Shibboleth.sso/Logout").get(10000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } finally{
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookies(null);
            }
            startActivity(new Intent(this, LoginWebActivity.class));
        }

        /*else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
