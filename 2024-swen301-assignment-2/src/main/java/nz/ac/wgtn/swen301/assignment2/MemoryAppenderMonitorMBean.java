package nz.ac.wgtn.swen301.assignment2;

public interface MemoryAppenderMonitorMBean {

    String[] getLogs();

    long getLogCount();

    long getDiscardedLogCount();

    void export(String fileName);
}
