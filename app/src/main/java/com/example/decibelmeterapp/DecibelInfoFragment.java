package com.example.decibelmeterapp;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


public class DecibelInfoFragment extends Fragment implements AdapterView.OnItemClickListener
{
    Bundle bundle;
    String[] decibelListTitles;
    Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_decibel_info, container, false);
        bundle = new Bundle();
        Resources res = getResources();
        decibelListTitles = res.getStringArray(R.array.decibel_titles);
        android.widget.ListView list = view.findViewById(R.id.decibelInfoListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.text_view_in_decibel_list_view, decibelListTitles);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        bundle.putInt("position", position);
        fragment = new DecibelInfoDisplayFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}