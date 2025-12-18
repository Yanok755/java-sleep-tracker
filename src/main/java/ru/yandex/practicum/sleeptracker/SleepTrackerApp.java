package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Основной класс приложения
public class SleepTrackerApp {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private List<SleepAnalysisFunction> analysisFunctions;

    public SleepTrackerApp() {
        // Инициализация списка аналитических функций
        this.analysisFunctions = Arrays.asList(
            new TotalSessionsFunction(),
            new MinDurationFunction(),
            new MaxDurationFunction(),
            new AverageDurationFunction(),
            new BadQualitySessionsFunction(),
            new SleeplessNightsFunction(),
            new ChronotypeFunction()
        );
    }

    // Метод для чтения файла с логом сна
    private List<SleepingSession> readSleepLog(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines
                .map(line -> line.split(";"))
                .map(parts -> {
                    try {
                        LocalDateTime sleepStart = LocalDateTime.parse(parts[0], DATE_TIME_FORMATTER);
                        LocalDateTime sleepEnd = LocalDateTime.parse(parts[1], DATE_TIME_FORMATTER);
                        SleepingSession.SleepQuality quality = SleepingSession.SleepQuality.valueOf(parts[2]);
                        return new SleepingSession(sleepStart, sleepEnd, quality);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Ошибка парсинга строки: " + Arrays.toString(parts));
                    }
                })
                .collect(Collectors.toList());
        }
    }

    // Метод для запуска всех аналитических функций
    public void analyzeSleep(String filePath) {
        try {
            List<SleepingSession> sessions = readSleepLog(filePath);
            System.out.println("Загружено " + sessions.size() + " сессий сна");
            System.out.println("=".repeat(50));

            // Выполняем все аналитические функции
            analysisFunctions.forEach(function -> {
                SleepAnalysisResult<?> result = function.apply(sessions);
                System.out.println(result);
            });

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка анализа: " + e.getMessage());
        }
    }

    // Основной метод приложения
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Использование: java SleepTrackerApp <путь_к_файлу_лога>");
            System.out.println("Пример: java SleepTrackerApp sleep_log.txt");
            return;
        }

        SleepTrackerApp app = new SleepTrackerApp();
        app.analyzeSleep(args[0]);
    }
}
