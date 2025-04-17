package com.example.smartposture.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.CustomGraph;
import com.example.smartposture.view.activity.LoginActivity;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.AuthViewModel;
import com.example.smartposture.viewmodel.ProfileViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileFragment extends BaseFragment {

    private AuthViewModel authViewModel;
    private ProfileViewModel profileViewModel;
    private SharedPreferenceManager spManager;
    private BarChart barChart;
    private PieChart pieChart;
    private Spinner spinnerMonth, spinnerYear;
    private TextView username, usertype;
    private int selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int selectedYear = Calendar.getInstance().get(Calendar.YEAR);
    private List<String> years = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeBackPressedCallback();
        initializeViewModels();
        initializeSharedPreferences();
        initializeUIElements(view);

        String token = spManager.getSessionToken();
        int userId = spManager.getUserId();

        setupMonthSpinner(view);
        setupYearSpinner(view);

        observeProfileViewModel();
        profileViewModel.fetchProfileStatistics(userId);

        setupSpinnerListeners(userId);
        setupLogoutButton(view, token);
        setupGuidanceButton(view);

        return view;
    }

    private void initializeBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Prevent going back
                    }
                });
    }

    private void initializeViewModels() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void initializeSharedPreferences() {
        spManager = SharedPreferenceManager.getInstance(requireContext());
    }

    private void initializeUIElements(View view) {
        barChart = view.findViewById(R.id.barChart);
        spinnerMonth = view.findViewById(R.id.spinnerMonth);
        spinnerYear = view.findViewById(R.id.spinnerYear);
        username = view.findViewById(R.id.userName);
        usertype = view.findViewById(R.id.userType);
        pieChart = view.findViewById(R.id.pieChartAveReps);

        username.setText(spManager.getUsername());
        usertype.setText(spManager.getUserType());
    }

    private void setupMonthSpinner(View view) {
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.months_array, R.layout.spinner_profile_item);
        monthAdapter.setDropDownViewResource(R.layout.spinner_profile_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.post(() -> spinnerMonth.setSelection(selectedMonth));
    }

    private void setupYearSpinner(View view) {
        String[] yearArray = getResources().getStringArray(R.array.years_array);
        for (String year : yearArray) {
            years.add(year);
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_profile_item, years);
        yearAdapter.setDropDownViewResource(R.layout.spinner_profile_item);
        spinnerYear.setAdapter(yearAdapter);

        for (int i = 0; i < years.size(); i++) {
            if (years.get(i).equals(String.valueOf(selectedYear))) {
                spinnerYear.setSelection(i);
                break;
            }
        }
    }

    private void observeProfileViewModel() {
        profileViewModel.getMonthlyWorkouts().observe(getViewLifecycleOwner(), monthlyData -> {
            if (monthlyData != null && selectedMonth < monthlyData.size()) {
                CustomGraph.setBarMonthlyWorkouts(requireView(), requireContext(), monthlyData.get(selectedMonth));
            }
        });

        profileViewModel.getProfileStatistics().observe(getViewLifecycleOwner(), statistics -> {
            if (statistics != null) {
                CustomGraph.setPieOverall(requireView(), requireContext(), statistics.getAvg_score(), statistics.getTotal_repetitions());
                CustomGraph.setPieScoresClassification(requireView(), requireContext(), statistics.getCount_025(), statistics.getCount_050(), statistics.getCount_100());
            }
        });
    }

    private void setupSpinnerListeners(int userId) {
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedMonth = position;

                ArrayList<ArrayList<Integer>> monthlyData = profileViewModel.getMonthlyWorkouts().getValue();
                if (monthlyData != null && selectedMonth < monthlyData.size()) {
                    CustomGraph.setBarMonthlyWorkouts(requireView(), requireContext(), monthlyData.get(selectedMonth));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedYear = Integer.parseInt(years.get(position));
                profileViewModel.fetchMonthlyWorkout(userId, selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void setupLogoutButton(View view, String token) {
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> authViewModel.logoutUser(token));

        authViewModel.getLogoutResult().observe(getViewLifecycleOwner(), logoutResponse -> {
            if (logoutResponse.isSuccess()) {
                spManager.clearSession();
                showToast(logoutResponse.getMessage());
                navigateToLogin();
            } else {
                showToast(logoutResponse.getMessage());
            }
        });
    }

    private void setupGuidanceButton(View view) {
        Button guidance = view.findViewById(R.id.offGuidance);
        updateGuidanceUI(guidance, spManager.getGuidanceStatus());

        guidance.setOnClickListener(v -> {
            boolean newStatus = !spManager.getGuidanceStatus();
            spManager.saveGuidanceStatus(newStatus);
            updateGuidanceUI(guidance, newStatus);
        });
    }

    private void updateGuidanceUI(Button button, boolean isOff) {
        button.setText(isOff ? "OFF" : "ON");
        button.setBackgroundColor(ContextCompat.getColor(
                requireContext(), isOff ? R.color.red : R.color.green_med
        ));
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.VISIBLE);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
