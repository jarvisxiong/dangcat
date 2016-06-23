package org.dangcat.business.server.chart;

import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.annotation.JndiName;

/**
 * 服务器管理。
 *
 * @author
 */
@JndiName(module = "Device", name = "ServerChart")
public interface ServerChartService {
    /**
     * 产生资源监视热点信息。
     *
     * @param id     服务器号。
     * @param width  图片宽度。
     * @param height 图片高度。
     * @return 图片热点信息。
     */
    String createActiveArea(@Parameter(name = "id") Integer id, @Parameter(name = "width") Integer width, @Parameter(name = "height") Integer height) throws ServiceException;

    /**
     * 产生资源监视图表。
     *
     * @param id     服务器号。
     * @param width  图片宽度。
     * @param height 图片高度。
     * @return 图片。
     */
    void renderChartImg(@Parameter(name = "id") Integer id, @Parameter(name = "width") Integer width, @Parameter(name = "height") Integer height) throws ServiceException;
}
