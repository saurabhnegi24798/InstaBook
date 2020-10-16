package com.example.photoblog;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Notification_Fragment extends Fragment {

    private FloatingActionButton floatingActionButton;

    public Notification_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        floatingActionButton = getActivity().findViewById(R.id.new_post);
        floatingActionButton.setVisibility(View.INVISIBLE);
        View view = inflater.inflate(R.layout.fragment_notification_, container, false);

        return view;
    }
}