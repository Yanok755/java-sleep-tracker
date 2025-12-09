import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Класс для представления качества сна
enum SleepQuality {
    GOOD, NORMAL, BAD
}

// Класс для представления сессии сна
class SleepingSession {
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
    
    public LocalDateTime getSleepStart() { return sleepStart; }
    public LocalDateTime getSleepEnd() { return sleepEnd; }
    public SleepQuality getQuality() { return quality; }
    
    @Override
    public String toString() {
        return String.format("SleepSession[start=%s, end=%s, duration=%d min, quality=%s]", 
            sleepStart, sleepEnd, getDurationInMinutes(), quality);
    }
}

// Класс-обёртка для результата анализа
class SleepAnalysisResult<T> {
    private String description;
    private T value;
    
    public SleepAnalysisResult(String description, T value) {
        this.description = description;
        this.value = value;
    }
    
    public String getDescription() { return description; }
    public T getValue() { return value; }
    
    @Override
    public String toString() {
        return String.format("%s: %s", description, value.toString());
    }
}

// Функциональный интерфейс для анализа сна
interface SleepAnalysisFunction extends Function<List<SleepingSession>, SleepAnalysisResult<?>> {
    // Базовый метод по умолчанию для удобства
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
