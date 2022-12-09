package com.ginalne.griffincashier;

import androidx.annotation.CheckResult;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
abstract class CheckoutAction {
    public static final int ADD  = 0;
    public static final int REMOVE = 1;
}
