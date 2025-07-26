package com.jvgn.parental;

import com.jvgn.parental.core.SessionTimer;
import com.jvgn.parental.storage.UsageLog;
import com.jvgn.parental.ui.FloatingTimerUI;

import javax.swing.*;

/**
 * MainApp — главный класс, запускающий приложение родительского контроля.
 */
public class MainApp {
    public static void main(String[] args) {
        System.out.println("[START] Родительский контроль активирован.");

        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setType(JFrame.Type.UTILITY);
        frame.setOpacity(0.0f);
        frame.setVisible(false);

        UsageLog usageLog = new UsageLog();

        if (usageLog.isNewDay()) {
            System.out.println("[INFO] Новый день — сбрасываем счётчик времени.");
            usageLog.reset();
        }

        int used = usageLog.getUsedSeconds();
        int dailyLimitSeconds = 5 * 3600;

        System.out.println("[DEBUG] Использовано: " + used + " сек из " + dailyLimitSeconds);

        if (used >= dailyLimitSeconds) {
            System.out.println("[WARNING] Дневной лимит исчерпан. Выключение...");
            try {
                Runtime.getRuntime().exec(new String[]{"shutdown", "-s", "-t", "0"});
            } catch (Exception e) {
                System.err.println("[ERROR] Ошибка при выключении: " + e.getMessage());
            }
            return;
        }

        System.out.println("[INFO] Запускаем UI и таймер.");
        FloatingTimerUI timerUI = new FloatingTimerUI();

        SessionTimer sessionTimer = new SessionTimer(usageLog, timerUI);

        timerUI.setOnUnlock(() -> {
            System.out.println("[UNLOCK] Код принят, отключаем контроль.");
            sessionTimer.stopSession();
            System.exit(0);
        });

        sessionTimer.startSession();

        if (!sessionTimer.isRunning()) {
            System.out.println("[WARNING] Сессия не стартовала, повторная попытка...");
            sessionTimer.startSession();
        } else {
            System.out.println("[INFO] Сессия успешно запущена.");
        }
    }
}
