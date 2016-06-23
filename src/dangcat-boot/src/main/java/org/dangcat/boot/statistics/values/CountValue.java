package org.dangcat.boot.statistics.values;

import java.util.concurrent.atomic.AtomicLong;

public class CountValue extends AtomicValue {
    private AtomicLong value = new AtomicLong(0);

    public CountValue(String name) {
        super(name);
    }

    public long getValue() {
        return this.value.get();
    }

    public long increase() {
        return this.value.incrementAndGet();
    }

    public long increase(long value) {
        return this.value.addAndGet(value);
    }
}
