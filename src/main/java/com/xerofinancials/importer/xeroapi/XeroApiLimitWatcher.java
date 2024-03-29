package com.xerofinancials.importer.xeroapi;

import com.xerofinancials.importer.exceptions.XeroAPIRateLimitException;
import com.xerofinancials.importer.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class XeroApiLimitWatcher {
    private static final Logger logger = LoggerFactory.getLogger(XeroApiLimitWatcher.class);
    private static final int DELTA = 10;
    private static final int MINUTE_LIMIT = 60 - DELTA;
    private static final int DAY_LIMIT = 5_000 - DELTA;
    private Map<Integer, Counter> CALLS_PER_MINUTE = new HashMap<>();
    private Map<Integer, Counter> CALLS_PER_DAY = new HashMap<>();
    private int lastCurrentMinute = 0;
    private int lastCurrentDay = 0;

    //todo: save to database limits/rates
    public void watch() {
        final Counter callsDuringCurrentMinute = trackMinuteWindow();
        final Counter callsDuringCurrentDay = trackDayWindow();
        waitIfNeeded(callsDuringCurrentMinute, callsDuringCurrentDay);
        refreshIfNeeded();
    }

    private Counter trackMinuteWindow() {
        final int currentMinute = DateUtils.getCurrentDateTimeInUtc().getMinute();
        this.lastCurrentMinute = currentMinute;
        final Counter callsDuringCurrentMinute = CALLS_PER_MINUTE.getOrDefault(currentMinute, new Counter());
        CALLS_PER_MINUTE.put(currentMinute, callsDuringCurrentMinute);
        callsDuringCurrentMinute.increment();
        return callsDuringCurrentMinute;
    }

    private Counter trackDayWindow() {
        final int currentDay = DateUtils.getCurrentDateTimeInUtc().getDayOfYear();
        this.lastCurrentDay = currentDay;
        final Counter callsDuringCurrentDay = CALLS_PER_DAY.getOrDefault(currentDay, new Counter());
        CALLS_PER_DAY.put(currentDay, callsDuringCurrentDay);
        callsDuringCurrentDay.increment();
        return callsDuringCurrentDay;
    }

    private void waitIfNeeded(Counter callsDuringCurrentMinute, Counter callsDuringCurrentDay) {
        final Counter callsDuringPreviousMinute = CALLS_PER_MINUTE.get(this.lastCurrentMinute - 1);
        final int total = callsDuringCurrentMinute.get() + (callsDuringPreviousMinute != null ? callsDuringPreviousMinute.get() : 0);
        if (total >= MINUTE_LIMIT) {
            logger.info("Xero API minute limit is exceeded. Waiting for 60 seconds before making next request...");
            wait(60 + DELTA);
        }
        if (callsDuringCurrentDay.get() >= DAY_LIMIT) {
            logger.info("Xero API day limit is exceeded. No more request this day.");
            throw new XeroAPIRateLimitException("Xero API rate day limit is exceeded. Executed "
                    + callsDuringCurrentDay.get() + " calls during this day. Limit is : " + DAY_LIMIT);
        }
    }

    private void refreshIfNeeded() {
        final int currentDay = DateUtils.getCurrentDateTimeInUtc().getDayOfYear();
        if (lastCurrentDay != currentDay) {
            CALLS_PER_DAY.clear();
        }

        final List<Integer> minutesToClear = new ArrayList<>();
        for (final Integer minute : CALLS_PER_MINUTE.keySet()) {
            if (minute < lastCurrentMinute - DELTA) {
                minutesToClear.add(minute);
            }
        }
        minutesToClear.forEach(minute -> CALLS_PER_MINUTE.remove(minute));
    }

    private void wait(int intervalInSeconds) {
        try {
            Thread.sleep(intervalInSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Counter {
        int count;

        void increment() {
            this.count++;
        }

        int get() {
            return this.count;
        }
    }
}
