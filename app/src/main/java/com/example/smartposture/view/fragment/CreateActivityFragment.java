package com.example.smartposture.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.ActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateActivityFragment extends BaseFragment {
    private LinearLayout workoutLayout;
    private FloatingActionButton fabAddLayout;
    private ActivityViewModel viewModel;
    private boolean isInitial = true;
    private int[] workouts = new int[0];
    private int[] repetitions = new int[0];
    private String[] spinnerItems = {"Squat"};
    private int[] spinnerValues = {5000};
    private String title, description, date, time;
    private boolean validInputs = true;
    private EditText titleEditText, descriptionEditText, datePickerEditText, timePickerEditText;
    private Button createActivityButton;
    private ImageButton backButton;
    private LinearLayout layout;
    private ScrollView scrollView;
    private SimpleDateFormat dateTimeFormat, dateFormat,  time12Format, time24Format;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_activity, container, false);

        int roomId = requireArguments().getInt("room_id", -1);

        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        time12Format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        time24Format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        datePickerEditText = view.findViewById(R.id.datePickerEditText);
        timePickerEditText = view.findViewById(R.id.timePickerEditText);

        createActivityButton = view.findViewById(R.id.createActivity);
        backButton = view.findViewById(R.id.backButton);
        layout = view.findViewById(R.id.layoutCreate);
        scrollView = view.findViewById(R.id.scrollview);
        workoutLayout = view.findViewById(R.id.addLayoutWorkout);
        fabAddLayout = view.findViewById(R.id.fabAddLayout);

        viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        View.OnClickListener hideKeyboardListener = v -> {
            hideKeyboard(view);
            v.clearFocus();
        };

        fabAddLayout.setOnClickListener(v -> addNewLayout());

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        layout.setOnClickListener(hideKeyboardListener);
        scrollView.setOnClickListener(hideKeyboardListener);
        view.setOnTouchListener((v, event) -> {
            hideKeyboard(v);
            v.clearFocus();
            v.performClick();
            return false;
        });

        setUpDatePicker(datePickerEditText);
        setUpTimePicker(timePickerEditText, datePickerEditText);

        LinearLayout initialLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_layout, workoutLayout, false);
        setupSpinner(initialLayout, spinnerItems);
        workoutLayout.addView(initialLayout);

        createActivityButton.setOnClickListener(v -> {
            title = titleEditText.getText().toString().trim();
            description = descriptionEditText.getText().toString().trim();
            date = datePickerEditText.getText().toString().trim();
            time = timePickerEditText.getText().toString().trim();
            boolean successCreate = appendLast();

            if (title.isEmpty()) {
                titleEditText.setError("Title is required");
                titleEditText.requestFocus();
                validInputs = false;
            }

            if (description.isEmpty()) {
                descriptionEditText.setError("Description is required");
                descriptionEditText.requestFocus();
                validInputs = false;
            }

            if (date.isEmpty()) {
                datePickerEditText.setError("Date is required");
                datePickerEditText.requestFocus();
                validInputs = false;
            }

            if (time.isEmpty()) {
                timePickerEditText.setError("Time is required");
                timePickerEditText.requestFocus();
                validInputs = false;
            }

            for (int i = 0; i < workouts.length; i++) {
                if (workouts[i] <= 0) {
                    Toast.makeText(requireContext(), "Invalid workout ID at position " + (i + 1), Toast.LENGTH_SHORT).show();
                    validInputs = false;
                }

                if (repetitions[i] <= 0) {
                    Toast.makeText(requireContext(), "Invalid repetition count at position " + (i + 1), Toast.LENGTH_SHORT).show();
                    validInputs = false;
                }
            }

            recheckTime();

            if (successCreate && validInputs) {
                viewModel.createActivity(roomId, title, description, date, time, workouts, repetitions);
            }
        });

        viewModel.getCreateActivityStatus().observe(getViewLifecycleOwner(), status -> {
            if ("Success".equals(status)) {
                Toast.makeText(requireContext(), "Activity Created Successfully", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else if (status != null) {
                Toast.makeText(requireContext(), "Failed to create activity: " + status, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void setUpDatePicker(EditText datePickerEditText) {

        datePickerEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (dialogView, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String currentDateString = dateFormat.format(calendar.getTime());

                        try {
                            Date selectedDate = dateFormat.parse(date);
                            Date currentDate = dateFormat.parse(currentDateString);

                            if (selectedDate != null && currentDate != null) {
                                if (!selectedDate.before(currentDate)) {
                                    datePickerEditText.setText(date);
                                    datePickerEditText.setError(null);
                                } else {
                                    Toast.makeText(requireContext(), "Selected date cannot be in the past", Toast.LENGTH_SHORT).show();
                                    datePickerEditText.setError("Change date");
                                    datePickerEditText.setText(null);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setUpTimePicker(EditText timePickerEditText, EditText datePickerEditText) {
        timePickerEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (dialogView, selectedHour, selectedMinute) -> {
                        String amPm = (selectedHour >= 12) ? "PM" : "AM";
                        int hourIn12Format = (selectedHour > 12) ? (selectedHour - 12) : (selectedHour == 0 ? 12 : selectedHour);
                        String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
                        timePickerEditText.setError(null);

                        try {
                            if (datePickerEditText.getText().toString().isEmpty()) {
                                Calendar now = Calendar.getInstance();
                                String defaultDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                        now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
                                datePickerEditText.setText(defaultDate);
                                datePickerEditText.setError(null);
                            }
                            String date = datePickerEditText.getText().toString().trim();

                            String dateTimeString = date + " " + time;

                            Date selectedDateTime = dateTimeFormat.parse(dateTimeString);
                            Date currentDate = new Date();

                            if (selectedDateTime == null) {
                                Toast.makeText(requireContext(), "Invalid date or time format", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String currentDateString = dateFormat.format(currentDate);
                            String currentTimeString = time12Format.format(currentDate);

                            Date selectedDateOnly = dateFormat.parse(date);
                            Date currentDateOnly = dateFormat.parse(currentDateString);

                            if (selectedDateOnly != null && !selectedDateOnly.before(currentDateOnly)) {
                                Date selectedTime = time12Format.parse(time);
                                Date currentTime = time12Format.parse(currentTimeString);

                                if (selectedTime != null) {
                                    if (selectedDateOnly.after(currentDateOnly)){
                                        timePickerEditText.setText(time);
                                        timePickerEditText.setError(null);
                                    } else {
                                        if (selectedDateOnly.equals(currentDateOnly) && !selectedTime.before(currentTime)){
                                            timePickerEditText.setText(time);
                                            timePickerEditText.setError(null);
                                        } else {
                                            Toast.makeText(requireContext(), "Selected time cannot be in the past", Toast.LENGTH_SHORT).show();
                                            timePickerEditText.setText(null);
                                            timePickerEditText.setError("Change Time");
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "Different Date", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT).show();
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean addNewLayout() {
        int lastChildIndex = workoutLayout.getChildCount() - 1;
        LinearLayout lastLayout = (LinearLayout) workoutLayout.getChildAt(lastChildIndex);

        Spinner spinner = lastLayout.findViewById(R.id.mySpinner);
        EditText repetitionInput = lastLayout.findViewById(R.id.repetition);

        repetitionInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        boolean isValid = checkLastLayout(spinner, repetitionInput);
        if (isValid && isInitial)
            isInitial = false;

        if (isValid) {
            repetitionInput.setEnabled(false);
            LinearLayout newLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_layout, workoutLayout, false);
            setupSpinner(newLayout, spinnerItems);
            workoutLayout.addView(newLayout);
        }
        return isValid;
    }

    // Helper method to append an element to an int array
    private int[] appendToArray(int[] array, int value) {
        if (array == null) {
            return new int[]{value};
        }
        int[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = value;
        return newArray;
    }

    // Helper method to set up the spinner with items
    private void setupSpinner(LinearLayout layout, String[] spinnerItems) {
        Spinner spinner = layout.findViewById(R.id.mySpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_workout, spinnerItems) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.item_dropdown_workout, parent, false);
                }
                TextView text = convertView.findViewById(R.id.text1);
                text.setText(getItem(position));
                return convertView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.dropdown_workout, parent, false);
                }
                TextView text = convertView.findViewById(R.id.text1);
                text.setText(getItem(position));
                return convertView;
            }
        };
        spinner.setAdapter(spinnerAdapter);

        // Disable the spinner if only one item is present
        if (spinnerItems.length == 1) {
            spinner.setEnabled(false);
            spinner.setClickable(false);
        } else {
            spinner.setEnabled(true);
            spinner.setClickable(true);
        }
    }

    private boolean appendLast() {
        int lastChildIndex = workoutLayout.getChildCount() - 1;
        LinearLayout lastLayout = (LinearLayout) workoutLayout.getChildAt(lastChildIndex);

        Spinner spinner = lastLayout.findViewById(R.id.mySpinner);
        EditText repetitionInput = lastLayout.findViewById(R.id.repetition);

        boolean isValid = checkLastLayout(spinner, repetitionInput);

        return isValid;
    }

    private boolean checkLastLayout(Spinner spinner, EditText repetitionInput) {
        boolean isValid = true;

        if (spinner.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
            Toast.makeText(getContext(), "Please select a workout", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (repetitionInput.getText().toString().trim().isEmpty()) {
            repetitionInput.setError("Repetition count is required");
            isValid = false;
        }

        if (isInitial)
            isInitial = false;

        if (isValid) {
            int selectedIndex = spinner.getSelectedItemPosition();
            int workoutValue = spinnerValues[selectedIndex];
            int repetitionCount = Integer.parseInt(repetitionInput.getText().toString().trim());

            // Save inputs to the global arrays
            workouts = appendToArray(workouts, workoutValue);
            repetitions = appendToArray(repetitions, repetitionCount);
        }
        return isValid;
    }

    private void recheckTime(){
        try {
            String dateTimeString = date + " " + time;

            Date selectedDateTime = dateTimeFormat.parse(dateTimeString);
            Date currentDate = new Date();

            if (selectedDateTime == null) {
                Toast.makeText(requireContext(), "Invalid date or time format", Toast.LENGTH_SHORT).show();
                validInputs = false;
                return;
            }

            String currentDateString = dateFormat.format(currentDate);
            String currentTimeString = time12Format.format(currentDate);

            Date selectedDateOnly = dateFormat.parse(date);
            Date currentDateOnly = dateFormat.parse(currentDateString);

            if (selectedDateOnly != null && !selectedDateOnly.before(currentDateOnly)) {
                Date selectedTime = time12Format.parse(time);
                Date currentTime = time12Format.parse(currentTimeString);
                String formattedTime = time.replaceAll("\\s+", " ").trim();

                Date parsedTime = time12Format.parse(formattedTime);
                String time24 = time24Format.format(parsedTime);

                if (selectedTime != null) {
                    if (selectedDateOnly.after(currentDateOnly)){
                        timePickerEditText.setText(time);
                        timePickerEditText.setError(null);
                        time = time24;
                    } else {
                        if (selectedDateOnly.equals(currentDateOnly) && !selectedTime.before(currentTime)){
                            timePickerEditText.setText(time);
                            timePickerEditText.setError(null);
                            time = time24;
                        } else {
                            Toast.makeText(requireContext(), "Selected time cannot be in the past", Toast.LENGTH_SHORT).show();
                            timePickerEditText.setText(null);
                            timePickerEditText.setError("Change Time");
                            validInputs = false;
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Different Date", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT).show();
            validInputs = false;
        }
    }
}
