package com.xerofinancials.importer.utils;

import org.threeten.bp.OffsetDateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateUtils {

    public static LocalDateTime getCurrentDateTimeInUtc() {
        final ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        final ZonedDateTime nowUtc = now.withZoneSameInstant(ZoneOffset.UTC);
        return nowUtc.toLocalDateTime();
    }

    public static LocalDateTime convertToUtc(OffsetDateTime dateTime) {
        long epochSecond = dateTime.toEpochSecond();
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
    }
}
