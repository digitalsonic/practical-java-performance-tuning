package demo.tuning.util.profiler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 线程执行时间剖析器中的具体条目信息
 *
 * 代码参考自Michael Zhou写的Profiler，针对新版本JDK做了改善，并进行了相应简化
 * http://miracle-platform.googlecode.com/svn/trunk/MiraclePlatform/toolkit/common/lang/com/alibaba/common/lang/diagnostic/Profiler.java
 *
 * @author DigitalSonic
 */
public class ThreadProfilerEntry {
    private final List<ThreadProfilerEntry> subEntries = new ArrayList<ThreadProfilerEntry>(4);
    private final String message;
    private final ThreadProfilerEntry parentEntry;
    private final ThreadProfilerEntry firstEntry;
    private final long baseTime;
    private final long startTime;
    private long endTime;

    /**
     * 创建一个新的entry。
     *
     * @param message     entry的信息，可以是<code>null</code>
     * @param parentEntry 父entry，可以是<code>null</code>
     * @param firstEntry  第一个entry，可以是<code>null</code>
     */
    public ThreadProfilerEntry(String message, ThreadProfilerEntry parentEntry, ThreadProfilerEntry firstEntry) {
        this.message = message;
        this.startTime = System.currentTimeMillis();
        this.parentEntry = parentEntry;
        this.firstEntry = firstEntry == null ? this : firstEntry;
        this.baseTime = (firstEntry == null) ? 0 : firstEntry.startTime;
    }

    /**
     * 结束当前entry，并记录结束时间。
     */
    public void release() {
        endTime = System.currentTimeMillis();
    }

    /**
     * 判断当前entry是否结束。
     *
     * @return 如果entry已经结束，则返回<code>true</code>
     */
    public boolean isReleased() {
        return endTime > 0;
    }

    /**
     * 创建一个新的子entry。
     *
     * @param message 子entry的信息
     */
    public void enterSubEntry(String message) {
        ThreadProfilerEntry subEntry = new ThreadProfilerEntry(message, this, firstEntry);
        subEntries.add(subEntry);
    }

    /**
     * 取得未结束的子entry。
     *
     * @return 未结束的子entry，如果没有子entry，或所有entry均已结束，则返回<code>null</code>
     */
    public ThreadProfilerEntry getUnreleasedEntry() {
        ThreadProfilerEntry subEntry = null;

        if (!subEntries.isEmpty()) {
            subEntry = subEntries.get(subEntries.size() - 1);

            if (subEntry.isReleased()) {
                subEntry = null;
            }
        }

        return subEntry;
    }

    /**
     * 取得entry相对于第一个entry的起始时间。
     *
     * @return 相对起始时间
     */
    public long getStartTime() {
        return (baseTime > 0) ? (startTime - baseTime) : 0;
    }

    /**
     * 取得entry持续的时间。
     *
     * @return entry持续的时间，如果entry还未结束，则返回<code>-1</code>
     */
    public long getDuration() {
        if (endTime < startTime) {
            return -1;
        } else {
            return endTime - startTime;
        }
    }

    /**
     * 取得entry自身所用的时间，即总时间减去所有子entry所用的时间。
     *
     * @return entry自身所用的时间，如果entry还未结束，则返回<code>-1</code>
     */
    public long getSelfDuration() {
        long duration = getDuration();

        if (duration < 0) {
            return -1;
        } else if (subEntries.isEmpty()) {
            return duration;
        } else {
            for (ThreadProfilerEntry subEntry : subEntries) {
                duration -= subEntry.getDuration();
            }
            return duration < 0 ? -1 : duration;
        }
    }

    /**
     * 取得当前entry在父entry中所占的时间百分比。
     *
     * @return 百分比
     */
    public double getPercentage() {
        double parentDuration = 0;
        double duration = getDuration();

        if ((parentEntry != null) && parentEntry.isReleased()) {
            parentDuration = parentEntry.getDuration();
        }

        if ((duration > 0) && (parentDuration > 0)) {
            return duration / parentDuration;
        } else {
            return 0;
        }
    }

    /**
     * 取得当前entry在第一个entry中所占的时间百分比。
     *
     * @return 百分比
     */
    public double getPercentageOfAll() {
        double firstDuration = 0;
        double duration = getDuration();

        if (firstEntry.isReleased()) {
            firstDuration = firstEntry.getDuration();
        }

        if ((duration > 0) && (firstDuration > 0)) {
            return duration / firstDuration;
        } else {
            return 0;
        }
    }

    /**
     * 将entry转换成字符串的表示。
     *
     * @return 字符串表示的entry
     */
    public String toString() {
        return toString("", "");
    }

    /**
     * 将entry转换成字符串的表示。
     *
     * @param prefix1 首行前缀
     * @param prefix2 后续行前缀
     *
     * @return 字符串表示的entry
     */
    public String toString(String prefix1, String prefix2) {
        StringBuffer buffer = new StringBuffer();
        toString(buffer, prefix1, prefix2);
        return buffer.toString();
    }

    /**
     * 将entry转换成字符串的表示。
     *
     * @param buffer  字符串buffer
     * @param prefix1 首行前缀
     * @param prefix2 后续行前缀
     */
    private void toString(StringBuffer buffer, String prefix1, String prefix2) {
        buffer.append(prefix1);

        long startTime = getStartTime();
        long duration = getDuration();
        long durationOfSelf = getSelfDuration();
        double percent = getPercentage();
        double percentOfAll = getPercentageOfAll();

        Object[] params = new Object[] {message, // {0} - entry信息
                startTime, // {1} - 起始时间
                duration, // {2} - 持续总时间
                durationOfSelf, // {3} - 自身消耗的时间
                percent, // {4} - 在父entry中所占的时间比例
                percentOfAll // {5} - 在总时间中所旧的时间比例
        };

        StringBuffer pattern = new StringBuffer("{1,number} ");

        if (isReleased()) {
            pattern.append("[{2,number}ms");

            if ((durationOfSelf > 0) && (durationOfSelf != duration)) {
                pattern.append(" ({3,number}ms)");
            }

            if (percent > 0) {
                pattern.append(", {4,number,##%}");
            }

            if (percentOfAll > 0) {
                pattern.append(", {5,number,##%}");
            }

            pattern.append("]");
        } else {
            pattern.append("[UNRELEASED]");
        }

        if (message != null) {
            pattern.append(" - {0}");
        }

        buffer.append(MessageFormat.format(pattern.toString(), params));

        for (int i = 0; i < subEntries.size(); i++) {
            ThreadProfilerEntry subEntry = subEntries.get(i);

            buffer.append('\n');

            if (i == (subEntries.size() - 1)) {
                subEntry.toString(buffer, prefix2 + "+---", prefix2 + "    "); // 最后一项
            } else {
                subEntry.toString(buffer, prefix2 + "+---", prefix2 + "|   "); // 第一项和中间项
            }
        }
    }

}
