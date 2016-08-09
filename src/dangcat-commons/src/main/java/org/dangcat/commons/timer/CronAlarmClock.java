package org.dangcat.commons.timer;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Cron定时器。
 *
 * @author dangcat
 */
public class CronAlarmClock extends AlarmClock {
    private static Logger logger = Logger.getLogger(CronAlarmClock.class);
    private String cronExpression = null;
    private CronTrigger cronTrigger = null;

    public CronAlarmClock(Object target) {
        super(target);
    }

    public CronAlarmClock(String cronExpression, Object target) {
        super(target);
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    @Override
    public boolean isTimeout(Calendar calendar) {
        if (this.cronTrigger != null) {
            if (this.cronTrigger.getStartTime().after(calendar.getTime()))
                this.cronTrigger.setStartTime(new Date(calendar.getTimeInMillis() - 1000));
            this.setNextAlramTime(this.cronTrigger.getFireTimeAfter(calendar.getTime()));
            if (this.isInholdTime(calendar.getTime()) && this.cronTrigger.willFireOn(calendar)) {
                this.setLastAlramTime(calendar.getTime());
                return true;
            }
        }
        return false;
    }

    public boolean isValidExpression() {
        if (this.cronTrigger == null && CronExpression.isValidExpression(this.getCronExpression())) {
            try {
                this.cronTrigger = new CronTrigger("quartz", "DANGCAT", this.getCronExpression());
            } catch (ParseException e) {
                logger.error(this.getTarget(), e);
            }
        }
        return this.cronTrigger != null;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder(super.toString());
        if (ValueUtils.isEmpty(this.cronExpression))
            info.append("\tCronExpression =" + this.cronExpression);
        return info.toString();
    }
}
