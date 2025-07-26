package com.jvgn.parental.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;


import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FloatingTimerUI — всплывающее окно с таймером и глобальной клавишей разблокировки (Ctrl+Shift+P).
 */
public class FloatingTimerUI extends JFrame implements NativeKeyListener {

    private final JLabel timeLabel;
    private final JLabel statusLabel;
    private Runnable onUnlock;

    public FloatingTimerUI() {
        setUndecorated(true);
        setAlwaysOnTop(true);
        setType(Type.UTILITY);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        // Таймер
        timeLabel = new JLabel("Осталось: 05:00:00");
        timeLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        timeLabel.setForeground(Color.RED);
        add(Box.createHorizontalStrut(10));
        add(timeLabel);

        // Статус
        statusLabel = new JLabel(" ● ");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        statusLabel.setForeground(Color.GREEN);
        add(Box.createHorizontalStrut(10));
        add(statusLabel);

        add(Box.createHorizontalGlue());

        // Кнопка
        JButton unlockButton = new JButton("Отключить контроль");
        unlockButton.setVisible(false);
        unlockButton.addActionListener(e -> onUnlockPressed());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(unlockButton);
        add(buttonPanel);

        // Глобальный хук
        disableJNativeLogging();
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            System.out.println("[DEBUG] JNativeHook зарегистрирован.");
        } catch (NativeHookException e) {
            System.err.println("[ERROR] Не удалось зарегистрировать JNativeHook: " + e.getMessage());
        }

        setSize(320, 40);
        setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - 340, 40);
        setVisible(true);
    }

    private void onUnlockPressed() {
        System.out.println("[DEBUG] Нажата кнопка разблокировки");
        SwingUtilities.invokeLater(() -> {
            String pass = JOptionPane.showInputDialog(FloatingTimerUI.this, "Введите код разблокировки:");
            if ("admin123".equals(pass)) {
                System.out.println("[DEBUG] Пароль принят");
                if (onUnlock != null) onUnlock.run();
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(FloatingTimerUI.this, "Неверный код!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_P &&
                (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0 &&
                (e.getModifiers() & NativeKeyEvent.SHIFT_MASK) != 0) {

            System.out.println("[DEBUG] Обнаружена глобальная комбинация Ctrl+Shift+P");
            SwingUtilities.invokeLater(this::onUnlockPressed);
        }
    }


    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}

    private void disableJNativeLogging() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    public void updateTime(Duration remaining) {
        long h = remaining.toHours();
        long m = remaining.toMinutesPart();
        long s = remaining.toSecondsPart();
        timeLabel.setText(String.format("Осталось: %02d:%02d:%02d", h, m, s));
    }

    public void updateStatus(boolean isRunning) {
        statusLabel.setForeground(isRunning ? Color.GREEN : Color.GRAY);
    }

    public void setOnUnlock(Runnable action) {
        this.onUnlock = action;
    }
}
