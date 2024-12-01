package com.example.smartposture.view.fragment;

import androidx.fragment.app.Fragment;

import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.view.activity.MainActivity;

public abstract class BaseFragment extends Fragment {

    protected SharedPreferenceManager getSharedPreferenceManager() {
        if (getActivity() instanceof MainActivity) {
            return ((MainActivity) getActivity()).getSharedPreferenceManager();
        } else {
            throw new IllegalStateException("Activity is not MainActivity");
        }
    }
}
