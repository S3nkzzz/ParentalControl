# 🛡️ ParentalControl

> **Java desktop application for managing computer usage sessions.**
> The app is written in Java, but the **interface and messages are in Russian** 🇷🇺.

---

## 📌 About

**ParentalControl** is a simple desktop application designed to **limit daily computer usage** by managing session timers. It was originally developed as a **personal project** to help the developer's **younger brother take breaks from gadgets**.

This is a **test version**, used for practicing Java development skills, JavaFX GUI, and system-level interaction.

---

## 🧠 Features

* ⏱️ Session timer: limits computer use to a specified duration.
* 🔔 Sound & popup notifications when session ends.
* 🧑‍💼 Admin interrupt: the session can be stopped manually **via a secret key combination + password input**.
* 🇷🇺 **All UI and prompts are in Russian**.
* 🔒 Protects session from being closed by the user easily.
* 💡 Configurable timer duration in future versions.

---

## 🎯 Purpose

This program was created to:

* Learn and practice **Java SE** programming.
* Understand **JavaFX UI** development.
* Build something **practical and personal**.
* Help a family member build healthy digital habits.

---

## 🛠️ Tech Stack

* Java 17
* JavaFX (GUI)
* Maven (project build)
* Simple file-based config (`usage_data.properties`)

---

## 📁 Project Structure

```
src/main/java/com/jvgn/parental/
├── ui/            # JavaFX UI components
├── core/          # Logic for session timing
├── storage/       # Data handling (future use)
└── MainApp.java   # Application entry point
```

---

## 🚀 How to Run

### 1. Clone the repo

```bash
git clone https://github.com/S3nkzzz/ParentalControl.git
cd ParentalControl
```

### 2. Build with Maven

```bash
mvn clean package
```

### 3. Run the app

```bash
java -jar target/ParentalControl.jar
```

> Make sure Java 17+ is installed and configured (`java -version`).

---

## 🔐 Admin Override

During an active session, an **administrator** can interrupt the timer by:

1. Pressing a **secret key combination** (e.g. `Ctrl + Alt + P`)
2. Entering a **valid password** in a prompt window.

This allows the responsible person to override the timer in specific situations.

---

## ⚠️ Disclaimer

This is a test project created for educational purposes only.
The app is not intended for production use.

---

## 🧑‍💻 Author

Created by **Jevgeni Tsernokozov** — an aspiring Java developer, self-taught, and passionate about solving real-world problems through code.

---

## ⭐️ Want to contribute?

Feel free to fork the project, test it, and suggest improvements!
