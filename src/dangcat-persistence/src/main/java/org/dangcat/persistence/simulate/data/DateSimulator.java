package org.dangcat.persistence.simulate.data;

import org.dangcat.commons.utils.DateUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * ÈÕÆÚÄ£ÄâÆ÷¡£
 *
 * @author dangcat
 */
public class DateSimulator extends ValueSimulator {
    private Date beginTime = null;
    private int dateField = DateUtils.DAY;
    private int step = 1;

    public DateSimulator() {
        this(Timestamp.class, DateUtils.now(), DateUtils.DAY);
    }

    public DateSimulator(Class<?> classType, Date beginTime) {
        this(classType, beginTime, DateUtils.DAY);
    }

    public DateSimulator(Class<?> classType, Date beginTime, int dateField) {
        this(classType, beginTime, dateField, 1);
    }

    public DateSimulator(Class<?> classType, Date beginTime, int dateField, int step) {
        super(classType);
        this.beginTime = DateUtils.clear(dateField, beginTime);
        this.dateField = dateField;
        this.step = step;
    }

    protected Object createValue(int index) {
        Object value = DateUtils.now();
        if (this.beginTime != null && this.step != 0)
            value = DateUtils.add(this.dateField, this.beginTime, index + this.step);
        return value;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public int getDateField() {
        return dateField;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
