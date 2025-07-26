package com.jvgn.parental.storage;

import java.io.*;
import java.time.LocalDate;
import java.util.Properties;

/**
 * UsageLog отвечает за хранение и загрузку данных о времени использования за текущий день.
 * Использует .properties файл для хранения даты и количества использованных секунд.
 */
public class UsageLog {
    private static final String FILE_PATH = "usage_data.properties";
    private static final String DATE_KEY = "date";
    private static final String USED_SECONDS_KEY = "usedSeconds";

    private LocalDate currentDate;
    private int usedSeconds;

    public void setUsedHours(double hours) {
        this.usedSeconds = (int) (hours * 3600);
    }


    public UsageLog() {
        load();
    }

    public void load() {
        Properties props = new Properties();
        File file = new File(FILE_PATH);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                String dateStr = props.getProperty(DATE_KEY);
                String secondsStr = props.getProperty(USED_SECONDS_KEY);

                LocalDate fileDate = LocalDate.parse(dateStr);
                if (fileDate.equals(LocalDate.now())) {
                    currentDate = fileDate;
                    usedSeconds = Integer.parseInt(secondsStr);
                } else {
                    reset();
                }
            } catch (Exception e) {
                System.err.println("Ошибка при загрузке usage_log: " + e.getMessage());
                reset();
            }
        } else {
            reset();
        }
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty(DATE_KEY, LocalDate.now().toString());
        props.setProperty(USED_SECONDS_KEY, String.valueOf(usedSeconds));

        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            props.store(fos, "Usage log file");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении usage_log: " + e.getMessage());
        }
    }

    public void addUsedSeconds(int seconds) {
        usedSeconds += seconds;
    }

    public int getUsedSeconds() {
        return usedSeconds;
    }

    public double getUsedHours() {
        return usedSeconds / 3600.0;
    }

    public void reset() {
        currentDate = LocalDate.now();
        usedSeconds = 0;
        save();
    }

    public boolean isNewDay() {
        return !currentDate.equals(LocalDate.now());
    }

    public void setUsedSeconds(int seconds) {
        this.usedSeconds = seconds;

    }
}

