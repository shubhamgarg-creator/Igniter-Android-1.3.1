package igniter.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category CalendarTimePickerDialog
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/*****************************************************************
 CalendarTimePickerDialog
 ****************************************************************/
public class CalendarTimePickerDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public onTimeSetListener onTimeSetListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        onTimeSetListener.onClicked(hourOfDay, minute);
    }

    public interface onTimeSetListener {
        void onClicked(int hourOfDay, int minute);
    }
}
