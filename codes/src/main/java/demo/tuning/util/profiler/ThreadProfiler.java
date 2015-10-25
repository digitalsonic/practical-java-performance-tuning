package demo.tuning.util.profiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程执行时间剖析器
 *
 * 代码参考自Michael Zhou写的Profiler，针对新版本JDK做了改善，并进行了相应简化
 * http://miracle-platform.googlecode.com/svn/trunk/MiraclePlatform/toolkit/common/lang/com/alibaba/common/lang/diagnostic/Profiler.java
 *
 * @author DigitalSonic
 */
public class ThreadProfiler {
    private static final Logger logger = LoggerFactory.getLogger(ThreadProfiler.class);
    private static final ThreadLocal<ThreadProfilerEntry> entries = new ThreadLocal<ThreadProfilerEntry>();

    /**
     * 开始计时。
     */
    public static void start() {
        start(null);
    }

    /**
     * 开始计时。
     *
     * @param message 第一个entry的信息
     */
    public static void start(String message) {
        entries.set(new ThreadProfilerEntry(message, null, null));
    }

    /**
     * 开始一个新的entry，并计时。
     *
     * @param message 新entry的信息
     */
    public static void enter(String message) {
        ThreadProfilerEntry currentEntry = getCurrentEntry();
        if (currentEntry != null) {
            currentEntry.enterSubEntry(message);
        }
    }
    /**
     * 结束最近的一个entry，记录结束时间。
     */
    public static void release() {
        ThreadProfilerEntry currentEntry = getCurrentEntry();
        if (currentEntry != null) {
            currentEntry.release();
        }
    }

    /**
     * 取得耗费的总时间。
     *
     * @return 耗费的总时间，如果未开始计时，则返回<code>-1</code>
     */
    public static long getDuration() {
        ThreadProfilerEntry entry = entries.get();
        return entry != null ? entry.getDuration() : -1;
    }

    /**
     * 列出所有的entry。
     *
     * @return 列出所有entry，并统计各自所占用的时间
     */
    public static String dump() {
        ThreadProfilerEntry entry = entries.get();
        return entry != null ? entry.toString() : "";
    }

    /**
     * 如果调用时间超过阈值，日志记录所有entry。
     *
     * @param threshold 时间阈值，单位毫秒
     * @return 是否超过阈值
     */
    public static boolean logIfSlow(long threshold) {
        boolean flag = false;
        ThreadProfilerEntry entry = entries.get();
        if (entry != null && entry.getDuration() >= threshold ) {
            flag = true;
            logger.warn("Current invoking duration is slower than {}, dumping details:\n{}", threshold, dump());
        }
        return flag;
    }

    /**
     * 取得当前的entry。
     *
     * @return 最近的一个entry，如果不存在，则返回<code>null</code>
     */
    private static ThreadProfilerEntry getCurrentEntry() {
        ThreadProfilerEntry entry = entries.get();
        ThreadProfilerEntry current = null;

        if (entry != null) {
            do {
                current = entry;
                entry = current.getUnreleasedEntry();
            } while (entry != null);
        }

        return current;
    }
}
