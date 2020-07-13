package igniter.interfaces;
/**
 * @package com.trioangle.igniter
 * @subpackage interfaces
 * @category SignUpActivityListener
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.res.Resources;

import igniter.views.signup.SignUpActivity;

/*****************************************************************
 SignUpActivityListener
 ****************************************************************/


public interface SignUpActivityListener {

    Resources getRes();

    SignUpActivity getInstance();

}
