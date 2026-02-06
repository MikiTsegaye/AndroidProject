package com.example.gamermatch.search;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gamermatch.R;

public class SearchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_search_container);
        if (i_SavedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.search_fragment_container, new SearchFragment())
                    .commit();
        }
    }
}