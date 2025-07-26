# ParentalControl

ParentalControl is a Java desktop application for monitoring and limiting daily computer usage. It tracks session time, enforces daily limits, and provides a floating UI for user interaction.

## Features

- Tracks daily computer usage time
- Enforces a configurable daily usage limit (default: 5 hours)
- Notifies and shuts down the computer when the limit is reached
- Floating timer UI for real-time feedback
- Resets usage counter each day

## Requirements

- Java 8 or higher
- Windows OS (uses `shutdown` command)
- [JNativeHook](https://github.com/kwhat/jnativehook) library (included in `libs/`)

## Project Structure

```
src/
  main/
    java/
      com/jvgn/parental/
        MainApp.java
        core/SessionTimer.java
        storage/UsageLog.java
        ui/FloatingTimerUI.java
libs/
  jnativehook-2.2.2.jar
  JNativeHook-2.2.2.x86_64.dll
jre/
pom.xml
```

## Building

To build the project, use Maven:

```sh
mvn clean package
```

The output JAR will be in the `target/` directory.

## Running

To run the application:

```sh
java -jar target/parental-control-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Configuration

- Daily usage limit is set in [`MainApp`](src/main/java/com/jvgn/parental/MainApp.java) (`dailyLimitSeconds` variable).
- Usage data is stored in `usage_data.properties` in the `target/` directory.

## License

MIT License

---

**Note:** This application is intended for educational purposes. Use