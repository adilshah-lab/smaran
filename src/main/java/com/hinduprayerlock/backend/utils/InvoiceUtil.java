package com.hinduprayerlock.backend.utils;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceUtil {

    private static final AtomicLong counter = new AtomicLong(1000);

    public static String generateInvoiceNumber() {
        int year = Year.now().getValue();
        long number = counter.incrementAndGet();

        return "INV-" + year + "-" + number;
    }
}
