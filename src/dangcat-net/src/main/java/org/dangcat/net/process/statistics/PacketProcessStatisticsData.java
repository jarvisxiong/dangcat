package org.dangcat.net.process.statistics;

import org.dangcat.boot.statistics.StatisticsData;

/**
 * 数据处理统计
 *
 * @author dangcat
 */
public class PacketProcessStatisticsData extends StatisticsData {
    /**
     * 性能不足忽略的数据。
     */
    public static final String Ignore = "Ignore";
    /**
     * 解析错误的数据。
     */
    public static final String ParseError = "ParseError";
    /**
     * 接收的数据。
     */
    public static final String Receive = "Receive";
    /**
     * 接收的速率。
     */
    public static final String ReceiveVelocity = "ReceiveVelocity";
    /**
     * 检验错误的数据。
     */
    public static final String ValidError = "ValidError";

    public PacketProcessStatisticsData(String name) {
        super(name);
        this.addCount(Receive);
        this.addVelocity(ReceiveVelocity, Receive);
        this.addCount(ParseError);
        this.addCount(ValidError);
        this.addCount(Ignore);
    }
}
