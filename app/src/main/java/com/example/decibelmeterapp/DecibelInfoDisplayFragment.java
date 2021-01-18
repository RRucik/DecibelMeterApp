package com.example.decibelmeterapp;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class DecibelInfoDisplayFragment extends Fragment
{
    TextView chosenInfoTitleTextView;
    TextView chosenInfoDangerLevelTextView;
    TextView chosenInfoDescriptionTextView;
    String[] decibelListTitles;
    String[] decibelListDangerLevels;
    String[] decibelListDescriptions;
    ImageButton returnButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_decibel_info_display, container, false);

        chosenInfoTitleTextView = view.findViewById(R.id.chosen_info_title);
        chosenInfoDangerLevelTextView = view.findViewById(R.id.chosen_info_danger_level);
        chosenInfoDescriptionTextView = view.findViewById(R.id.chosen_info_description);

        Resources res = getResources();

        decibelListTitles = res.getStringArray(R.array.decibel_titles);
        decibelListDangerLevels = res.getStringArray(R.array.decibel_danger_levels);
        decibelListDescriptions = res.getStringArray(R.array.decibel_descriptions);


        int position = this.getArguments().getInt("position");
        chosenInfoTitleTextView.setText(decibelListTitles[position]);
        chosenInfoDangerLevelTextView.setText(decibelListDangerLevels[chooseCorrectDangerLevelIndex(position)]);
        chosenInfoDescriptionTextView.setText(decibelListDescriptions[position]);

        returnButton = view.findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new DecibelInfoFragment()).commit();
            }
        });

        return view;
    }

    public int chooseCorrectDangerLevelIndex(int position)
    {
        if(position < 2)
            return 0;
        else if (position < 5)
            return 1;
        else if (position < 7)
            return 2;
        else if (position < 9)
            return 3;
        else if (position < 11)
            return 4;
        else
            return 5;
    }
}