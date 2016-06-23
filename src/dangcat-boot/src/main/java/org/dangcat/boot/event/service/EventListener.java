package org.dangcat.boot.event.service;

/**
 * ÏûÏ¢ÕìÌıÆ÷¡£
 *
 * @author dangcat
 */
public interface EventListener {
    /**
     * Æô¶¯ÕìÌıÆ÷¡£
     */
    void start();

    /**
     * Í£Ö¹ÕìÌı¡£
     */
    void stop();
}
