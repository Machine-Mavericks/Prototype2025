# AdvantageScope Data Logging

## Overview

This project uses WPILib's DataLogManager to log telemetry data for analysis in AdvantageScope. This allows us to monitor motor performance, diagnose issues, and analyze robot behavior during matches and testing.

## Current Implementation

### Shooter Subsystem Logging

The `Shooter` subsystem logs the following data:

- **`/Shooter/MotorVelocity`** - The actual motor speed in rotations per second, measured from the TalonFX encoder
- **`/Shooter/MotorCommand`** - The commanded motor speed value (0.0 to 1.0 or actual duty cycle)

These logs help monitor motor performance, especially during ball loading when speed dips occur.

## Setup

### Code Configuration

Data logging is automatically started in the `Robot()` constructor:

```java
public Robot() {
    // Start data logging for AdvantageScope
    DataLogManager.start();
    
    // ... rest of initialization
}
```

### Subsystem Implementation

Each subsystem that needs logging creates `DoubleLogEntry` objects:

```java
private final DoubleLogEntry motorVelocityLog;
private final DoubleLogEntry motorCommandLog;

public Shooter() {
    DataLog log = DataLogManager.getLog();
    motorVelocityLog = new DoubleLogEntry(log, "/Shooter/MotorVelocity");
    motorCommandLog = new DoubleLogEntry(log, "/Shooter/MotorCommand");
}
```

Data is logged in the `periodic()` method (runs every 20ms) and when commands are issued.

## Accessing Log Files

### On the roboRIO

1. Log files are automatically saved to `/home/lvuser/logs/` on the roboRIO
2. Each log file is timestamped: `FRC_YYYYMMDD_HHMMSS.wpilog`
3. Logs are created for each robot power cycle

### Downloading Logs

**Method 1: Using FTP**
1. Connect to the roboRIO network (WiFi or USB)
2. Use an FTP client (FileZilla, WinSCP, etc.)
3. Connect to: `ftp://10.TE.AM.2` (replace TE.AM with your team number)
   - Example: Team 1234 would use `ftp://10.12.34.2`
4. Navigate to `/home/lvuser/logs/`
5. Download `.wpilog` files

**Method 2: Using SCP/SFTP (Terminal)**
```bash
# Replace TEAM with your team number (e.g., 1234)
scp admin@10.TE.AM.2:/home/lvuser/logs/*.wpilog ~/Downloads/
```

**Method 3: Using WPILib Tools**
- In VS Code, use the WPILib command palette
- Search for "Download Logs from roboRIO"

## Viewing in AdvantageScope

### Installing AdvantageScope

1. Download from: https://github.com/Mechanical-Advantage/AdvantageScope/releases
2. Install for your platform (Windows, macOS, Linux)

### Opening Log Files

1. Launch AdvantageScope
2. Click **File** → **Open Log**
3. Select your downloaded `.wpilog` file
4. The data will load and display available fields in the left sidebar

### Viewing Shooter Data

1. In the left sidebar, expand the tree to find `/Shooter/`
2. You'll see:
   - `MotorVelocity` - Actual motor speed
   - `MotorCommand` - Commanded speed

3. **Create a Line Graph:**
   - Click the **+** button or drag fields to the main view
   - Select "Line Graph" visualization
   - Add both `MotorVelocity` and `MotorCommand` to the same graph
   - This lets you compare commanded vs actual speed

### Analyzing Speed Dips

When viewing the shooter data:

- **Normal Operation:** `MotorVelocity` should closely track `MotorCommand`
- **Ball Loading:** You'll see `MotorVelocity` dip while `MotorCommand` stays constant
- **Recovery Time:** Measure how long it takes for velocity to return to target after a ball passes

**Tips:**
- Use the zoom tools to focus on specific events
- Enable the cursor to see exact values at any timestamp
- Use playback controls to replay robot behavior
- Export graphs as images for analysis reports

## Adding Logging to Other Subsystems

To add logging to other subsystems, follow this pattern:

1. **Import required classes:**
```java
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
```

2. **Create log entries as fields:**
```java
private final DoubleLogEntry myDataLog;
```

3. **Initialize in constructor:**
```java
public MySubsystem() {
    DataLog log = DataLogManager.getLog();
    myDataLog = new DoubleLogEntry(log, "/MySubsystem/MyData");
}
```

4. **Append data in periodic() or command methods:**
```java
@Override
public void periodic() {
    double value = sensor.getValue();
    myDataLog.append(value);
}
```

## Best Practices

1. **Naming Convention:** Use hierarchical names: `/SubsystemName/DataName`
2. **Log Frequency:** Log in `periodic()` for continuous data, in command methods for events
3. **Data Types:** Use appropriate log entry types:
   - `DoubleLogEntry` for numbers
   - `BooleanLogEntry` for true/false
   - `StringLogEntry` for text/states
4. **Performance:** Logging is efficient, but avoid excessive string logging
5. **Storage:** Monitor roboRIO storage space; old logs can be deleted via FTP

## Troubleshooting

**No log files created:**
- Check that `DataLogManager.start()` is called in `Robot()` constructor
- Verify roboRIO has available storage space

**Missing data in logs:**
- Ensure `append()` is called in code that actually runs
- Check that the subsystem is instantiated and periodic() is executing

**Can't download logs:**
- Verify network connection to roboRIO
- Check firewall settings
- Try using roboRIO IP: `10.TE.AM.2` or mDNS: `roboRIO-TEAM-FRC.local`

## Additional Resources

- [WPILib DataLog Documentation](https://docs.wpilib.org/en/stable/docs/software/telemetry/datalog.html)
- [AdvantageScope Documentation](https://github.com/Mechanical-Advantage/AdvantageScope/blob/main/docs/INDEX.md)
- [AdvantageKit Best Practices](https://github.com/Mechanical-Advantage/AdvantageKit/blob/main/docs/WHAT-IS-ADVANTAGEKIT.md)
