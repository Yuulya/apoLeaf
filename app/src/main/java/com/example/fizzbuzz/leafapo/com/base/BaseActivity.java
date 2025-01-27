package com.example.fizzbuzz.leafapo.com.base;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fizzbuzz.leafapo.ApoData;
import com.example.fizzbuzz.leafapo.MainActivity;
import com.example.fizzbuzz.leafapo.NoteActivity;
import com.example.fizzbuzz.leafapo.R;
import com.example.fizzbuzz.leafapo.ScrollViewActivity;
import com.example.fizzbuzz.leafapo.com.content.ApoPage;
import com.example.fizzbuzz.leafapo.com.helper.Check;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {


    // type face
    public Typeface typeface;

    public ArrayList<ApoPage> apoPages;

    public MediaPlayer mediaPlayer1;
    public boolean mediaFadeOut = false;

    public Handler musicHandler;

    public Menu menuOpt;

    protected Handler lyricHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.go, menu);
        this.menuOpt = menu;

        menu.findItem(R.id.action_favorite).getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItem itemChoose = BaseActivity.this.menuOpt.findItem(R.id.action_favorite);
                BaseActivity.this.onOptionsItemSelected(itemChoose);
            }
        });

        menu.findItem(R.id.action_note).getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItem itemChoose = BaseActivity.this.menuOpt.findItem(R.id.action_note);
                BaseActivity.this.onOptionsItemSelected(itemChoose);
            }
        });

        SearchView searchView = (SearchView) findViewById(R.id.action_search);
        searchView.clearFocus();

        /*LinearLayout.LayoutParams mg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mg.setMargins(0, 0, -20, 0);*/

        return super.onCreateOptionsMenu(menu);

    }

    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void changeStatusBarColor(String colorCode){
        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.manipulateColor(Color.parseColor(colorCode), (float) 0.8));
        }
    }

    public int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

    public void init(){
        // set Data
        ApoData apoData = new ApoData(this);
        apoPages = apoData.getApoPages();

        typeface = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");

        this.changeStatusBarColor("#A4A4A4");
        this.setToolBar();
    }

    public void setToolBar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.search_edit_frame);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.setSearchView();
    }

    public void setSearchView(){
        SearchView searchView = (SearchView) findViewById(R.id.action_search);
        searchView.setMaxWidth(500);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                if (Check.checkQuery(query, BaseActivity.this.apoPages.size()) == true) {
                    BaseActivity.this.stopAction();
                    intent.putExtra("jump", Integer.parseInt(query));
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText editText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setHintTextColor(Color.parseColor("#1B1B1B"));
        editText.setHint("Jump...");
        editText.setTypeface(this.typeface);
        editText.setTextSize(14);

        View searchplate = (View) searchView.findViewById(android.support.v7.appcompat.R.id.search_edit_frame);
        searchplate.setBackgroundColor(Color.parseColor("#ffffff"));

        // search view config for greater android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            searchplate.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 8);
                    //outline.
                }
            });
            searchplate.setClipToOutline(true);
        }

        LinearLayout.LayoutParams mg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mg.setMargins(0, 0, 10, 10);
        searchplate.setLayoutParams(mg);

        ImageView searchIView = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        searchIView.setScaleX((float) 0.7);
        searchIView.setScaleY((float) 0.7);

        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                this.showScrollView();
                return true;
            case R.id.action_note:
                this.showNote();
                return true;
            case R.id.action_settings_overr:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showScrollView(){
        Toast.makeText(this, "Test", 200).show();
        Intent intent = new Intent(this, ScrollViewActivity.class);
        stopAction();
        startActivity(intent);
    }

    public void showNote(){
        Toast.makeText(this, "Test", 200).show();
        Intent intent = new Intent(this, NoteActivity.class);
        stopAction();
        startActivity(intent);
    }

    private void stopAction(){
        if (this.musicHandler != null){
            this.mediaPlayer1.stop();
            this.musicHandler.removeCallbacksAndMessages(null);
            this.mediaPlayer1 = null;
        }

        if (this.lyricHandler != null){
            this.lyricHandler.removeCallbacksAndMessages(null);
        }
    }

}
