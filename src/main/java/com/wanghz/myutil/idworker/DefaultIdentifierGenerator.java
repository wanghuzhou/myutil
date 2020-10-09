package com.wanghz.myutil.idworker;

/**
 * 默认生成器
 *
 * @author wanghuzhou modify from mybatisPlus
 * @since 2019-10-15
 */
public class DefaultIdentifierGenerator implements IdentifierGenerator {

    private final Sequence sequence;

    public DefaultIdentifierGenerator() {
        this.sequence = new Sequence();
    }

    public DefaultIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public DefaultIdentifierGenerator(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public Long nextId(Object entity) {
        return sequence.nextId();
    }
}

