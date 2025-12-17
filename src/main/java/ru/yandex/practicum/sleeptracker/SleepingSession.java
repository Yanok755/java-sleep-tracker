package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.Duration;

// Класс для представления качества сна
enum SleepQuality {
    GOOD, NORMAL, BAD
}

// Класс для представления сессии сна
public class SleepingSession {
    private LocalDateTime sleepStart;
    private LocalDateTime sleepEnd;
    private SleepQuality quality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality quality) {
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.quality = quality;
    }

    public long getDurationInMinutes() {
        return Duration.between(sleepStart, sleepEnd).toMinutes();
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return String.format("SleepSession[start=%s, end=%s, duration=%d min, quality=%s]", 
            sleepStart, sleepEnd, getDurationInMinutes(), quality);
    }
}
