package com.wanghz.myutil.idworker;


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * id 获取器
 *
 * @author wanghuzhou modify from mybatisPlus
 * @date 2016-08-01
 */
public class IdWorker {

    /**
     * 主机和进程的机器码
     */
    private static IdentifierGenerator IDENTIFIER_GENERATOR = new DefaultIdentifierGenerator();

    /**
     * 毫秒格式化时间
     */
    public static final DateTimeFormatter MILLISECOND = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 获取唯一ID
     *
     * @return id
     */
    public static long getId() {
        return getId(new Object());
    }

    /**
     * 获取唯一ID
     *
     * @return id
     */
    public static long getId(Object entity) {
        return IDENTIFIER_GENERATOR.nextId(entity).longValue();
    }

    /**
     * 获取唯一ID
     *
     * @return id
     */
    public static String getIdStr() {
        return getIdStr(new Object());
    }

    /**
     * 获取唯一ID
     *
     * @return id
     */
    public static String getIdStr(Object entity) {
        return IDENTIFIER_GENERATOR.nextId(entity).toString();
    }

    /**
     * 格式化的毫秒时间
     */
    public static String getMillisecond() {
        return LocalDateTime.now().format(MILLISECOND);
    }

    /**
     * 时间 ID = Time + ID
     * <p>例如：可用于商品订单 ID</p>
     */
    public static String getTimeId() {
        return getMillisecond() + getIdStr();
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param dataCenterId 序列号
     * @see #setIdentifierGenerator(IdentifierGenerator)
     */
    public static void initSequence(long workerId, long dataCenterId) {
        IDENTIFIER_GENERATOR = new DefaultIdentifierGenerator(workerId, dataCenterId);
    }

    /**
     * 自定义id 生成方式
     *
     * @param identifierGenerator id 生成器
     */
    public static void setIdentifierGenerator(IdentifierGenerator identifierGenerator) {
        IDENTIFIER_GENERATOR = identifierGenerator;
    }

    /**
     * 使用ThreadLocalRandom获取UUID获取更优的效果 去掉"-"
     */
    public static String get32UUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace("-", StringUtils.EMPTY);
    }
}

