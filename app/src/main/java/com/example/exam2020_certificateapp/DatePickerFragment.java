package com.example.exam2020_certificateapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    /**
     * Creates a DatePickerDialog for a user to choose an expiration date.
     * The savedInstanceState represents state of the DatePicker.
     * The state is null on creation, and then saves data to the Bundle.
     * Can never return null.
     * @param savedInstanceState the state of the DatePicker, when last modified.
     * @return Selected date in the DatePicker.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener) getActivity(),
                year, month, day);
    }
}
