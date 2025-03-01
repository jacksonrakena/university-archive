package nz.ac.wgtn.swen301.assignment2;

public class MemoryAppenderMonitor implements MemoryAppenderMonitorMBean  {
    private final MemoryAppender appender;
    public MemoryAppenderMonitor(MemoryAppender appender) {
        this.appender = appender;
    }

    public String[] getLogs() { return appender.getLogs(); }

    public long getLogCount() { return appender.getLogCount(); }

    public long getDiscardedLogCount() { return appender.getDiscardedLogCount(); }

    public void export(String fileName) {
        appender.export(fileName);
    }
}
