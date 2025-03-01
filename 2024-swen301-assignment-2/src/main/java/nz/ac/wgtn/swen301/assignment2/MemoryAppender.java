package nz.ac.wgtn.swen301.assignment2;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.management.ObjectName;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryAppender extends AppenderSkeleton {
    private final List<LoggingEvent> logs = new ArrayList<>();
    private final MemoryAppenderMonitorMBean bean;

    @Override
    public void finalize() {
        unregister();
        super.finalize();
    }

    private void unregister() {
        try {
            ManagementFactory
                    .getPlatformMBeanServer()
                    .unregisterMBean(new ObjectName("nz.ac.wgtn.swen301.assignment2.MemoryAppender:type=MemoryAppender,name=" + this.getName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void register() {
        try {
            ManagementFactory
                    .getPlatformMBeanServer()
                    .registerMBean(bean, new ObjectName("nz.ac.wgtn.swen301.assignment2.MemoryAppender:type=MemoryAppender,name=" + this.getName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void setName(String name) {
        try {
            unregister();
            super.setName(name);
            register();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MemoryAppender()  {
        super.setName("MemoryAppender-" + Math.random());
        try {
            bean = new MemoryAppenderMonitor(this);
            register();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long discardedLogCount = 0;
    public long getDiscardedLogCount() { return this.discardedLogCount; }
    private long maxSize = 1000;

    public long getMaxSize() { return this.maxSize; }
    public void setMaxSize(long maxSize) { this.maxSize = maxSize; collect(); }

    public List<LoggingEvent> getCurrentLogs() {
        return Collections.unmodifiableList(this.logs);
    }

    public String[] getLogs() {
        PatternLayout pl = new PatternLayout();
        return this.logs.stream().map(pl::format).toArray(String[]::new);
    }

    public long getLogCount() { return this.logs.size(); }

    private void collect() {
        while (this.logs.size() > this.maxSize) {
            this.logs.remove(0);
            this.discardedLogCount++;
        }
    }

    public void export(String fileName) {
        JsonLayout layout = new JsonLayout();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("[");
            for (int i = 0; i < this.logs.size(); i++) {
                writer.write(layout.format(this.logs.get(i)));
                if (i != this.logs.size() - 1) writer.write(",");
            }
            writer.write("]");
        } catch (IOException e) {

        }
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        logs.add(loggingEvent);
        collect();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
