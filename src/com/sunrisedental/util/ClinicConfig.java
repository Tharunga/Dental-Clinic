package com.sunrisedental.util;

import java.math.BigDecimal;

/* Clinic settings - consultation fee is fixed for all appointments */
public final class ClinicConfig {

    public static final BigDecimal CONSULTATION_FEE = new BigDecimal("1500.00");
    public static final String CLINIC_NAME = "Sunrise Dental Clinic";
    public static final String CLINIC_ADDRESS = "42 Galle Road, Colombo 03, Sri Lanka";
    public static final String CLINIC_PHONE = "+94 11 234 5678";

    private ClinicConfig() {
    }
}
