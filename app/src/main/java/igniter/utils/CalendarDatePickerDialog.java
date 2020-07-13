package igniter.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category CalendarDatePickerDialog
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/*****************************************************************
 CalendarDatePickerDialog
 ****************************************************************/
public class CalendarDatePickerDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public DateSetListener dateSetListener;
    private DatePickerDialog dialog = null;
    private boolean isDOBDatePicker = false;

    public boolean setDOBDatePicker(boolean isDOBPicker) {
        return isDOBDatePicker = isDOBPicker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            dialog = new DatePickerDialog(getActivity(), this, yy, mm, dd);

            if (isDOBDatePicker) {
                Calendar maxDate = Calendar.getInstance();
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
            } else {
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dateSetListener.onClicked(year, month, day);
    }

    public interface DateSetListener {
        void onClicked(int year, int month, int day);
    }
}
