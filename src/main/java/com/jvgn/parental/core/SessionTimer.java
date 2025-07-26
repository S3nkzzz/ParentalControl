package com.jvgn.parental.core;

import com.jvgn.parental.storage.UsageLog;
import com.jvgn.parental.ui.FloatingTimerUI;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;

/**
 * SessionTimer отвечает за отслеживание времени текущей сессии пользователя.
 * Проверяет время суток и дневной лимит.
 */
public class SessionTimer {

    private final LocalTime allowedStart = LocalTime.of(8, 0);
    private final LocalTime allowedEnd = LocalTime.of(23, 0);

    private final UsageLog usageLog;
    private final FloatingTimerUI timerUI;
    private Instant sessionStart;
    private boolean running = false;
    private boolean notified15Min = false;
    private TrayIcon trayIcon;

    public SessionTimer(UsageLog usageLog, FloatingTimerUI timerUI) {
        this.usageLog = usageLog;
        this.timerUI = timerUI;
        initializeTrayIcon();
    }

    private boolean isWithinAllowedHours() {
        LocalTime now = LocalTime.now();
        return now.isBefore(allowedStart) || now.isAfter(allowedEnd);
    }

    public void startSession() {
        if (isWithinAllowedHours()) {
            System.out.println("[BLOCKED] Сейчас запрещённое время. Сессия не будет запущена.");
            return;
        }

        if (isRunning()) {
            System.out.println("[INFO] Сессия уже запущена.");
            return;
        }

        sessionStart = Instant.now();
        running = true;
        notified15Min = false;

        System.out.println("🟢 Сессия началась.");

        addToAutostart();
        startTickLoop();
    }

    private void startTickLoop() {
        new Thread(() -> {
            while (running) {
                try {
                    updateSession();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("⛔ Поток таймера прерван: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    public void updateSession() {
        Instant now = Instant.now();
        long elapsed = Duration.between(sessionStart, now).getSeconds();

        int sessionLimitSeconds = 5 * 3600;

        int totalUsed = usageLog.getUsedSeconds() + (int) elapsed;
        int remaining = Math.max(0, sessionLimitSeconds - totalUsed);

        LocalTime currentTime = LocalTime.now();
        if (isWithinAllowedHours()) {
            System.out.println("⛔ Вышли за пределы разрешенного времени. Завершаем работу...");
            shutdownComputer();
            running = false;
            return;
        }

        timerUI.updateTime(Duration.ofSeconds(remaining));
        timerUI.updateStatus(isRunning());

        if (remaining == 15 * 60 && !notified15Min) {
            showNotification("Сессия завершится через 15 минут.");
            notified15Min = true;
        }

        if (remaining <= 0) {
            System.out.println("⛔ Лимит времени исчерпан. Выключаем ПК...");
            shutdownComputer();
            running = false;
        }
    }

    private void shutdownComputer() {
        try {
            new ProcessBuilder("shutdown", "-s", "-t", "0").start();
        } catch (IOException e) {
            System.err.println("⚠️ Ошибка при выключении: " + e.getMessage());
        }
    }

    private void initializeTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(
                    Toolkit.getDefaultToolkit().createImage(""), "ParentalControl"
            );
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Ошибка добавления TrayIcon: " + e.getMessage());
            }
        }
    }

    private void showNotification(String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage("⏰ Осталось 15 минут!", message, MessageType.INFO);
        }
    }

    private void addToAutostart() {
        try {
            String startupFolder = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
            String shortcutPath = startupFolder + "\\ParentalControl.lnk";
            String exePath = new java.io.File(SessionTimer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            if (!exePath.endsWith(".exe")) return;

            String vbs = """
                Set shell = CreateObject("WScript.Shell")
                Set shortcut = shell.CreateShortcut("%s")
                shortcut.TargetPath = "%s"
                shortcut.Save
            """.formatted(shortcutPath, exePath);

            Path scriptPath = Path.of("createShortcut.vbs");
            Files.writeString(scriptPath, vbs);
            Process p = Runtime.getRuntime().exec("wscript " + scriptPath.toAbsolutePath());
            p.waitFor();
            Files.delete(scriptPath);
        } catch (Exception e) {
            System.err.println("Не удалось добавить автозапуск: " + e.getMessage());
        }
    }

    public void stopSession() {
        running = false;
        System.out.println("⛔ Сессия остановлена вручную.");
    }

    public boolean isRunning() {
        return running;
    }
}