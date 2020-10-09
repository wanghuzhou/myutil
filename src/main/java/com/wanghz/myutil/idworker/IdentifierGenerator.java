package com.wanghz.myutil.idworker;

/**
 * Id生成器接口
 *
 * @author wanghuzhou modify from mybatisPlus
 * @since 2020-06-08 14:14:03
 */
public interface IdentifierGenerator {

    /**
     * 生成Id
     *
     * @param entity 实体
     * @return id
     */
    Number nextId(Object entity);

    /**
     * 生成uuid
     *
     * @param entity 实体
     * @return uuid
     */
    default String nextUUID(Object entity) {
        return IdWorker.get32UUID();
    }
}
