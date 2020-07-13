package igniter.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category Validator
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*****************************************************************
 Validator
 ****************************************************************/
public class Validator {
    // at least 8 characters. => {8,}
    // at lease 1 numeric => (?=.*\\d)
    // at lease 1 capital => (?=.*[A-Z])
    private static final String PASS_PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])(?=\\S+$).{8,}$";

    public boolean isValidatePwd(String password) {
        password = password.trim();
        Pattern pattern = Pattern.compile(PASS_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return (password.length() > 0 && matcher.matches());
    }

    public boolean isValidateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
