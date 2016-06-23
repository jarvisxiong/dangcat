package org.dangcat.web.filter;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class SetCharacterEncodingFilter implements Filter {
    private static Logger logger = Logger.getLogger(SetCharacterEncodingFilter.class);
    private Map<Pattern, String> charsetMap = null;
    private String defaultCharset = "utf-8";

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String charset = this.defaultCharset;
        if (this.charsetMap != null) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String uri = RequestUtils.getUri(httpServletRequest);
            for (Entry<Pattern, String> entry : this.charsetMap.entrySet()) {
                if (entry.getKey().matcher(uri).matches()) {
                    charset = entry.getValue();
                    break;
                }
            }
        }
        request.setCharacterEncoding(charset);
        filterChain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {
        String defaultCharset = config.getInitParameter("DefaultCharset");
        if (!ValueUtils.isEmpty(this.charsetMap))
            this.defaultCharset = defaultCharset;

        String charsetMap = config.getInitParameter("CharsetMap");
        if (!ValueUtils.isEmpty(charsetMap)) {
            String[] charsetItems = charsetMap.split(";");
            if (charsetItems != null) {
                for (String charsetItem : charsetItems) {
                    String[] charsets = charsetItem.split("=");
                    if (charsets != null && charsets.length == 2) {
                        String pattern = charsets[0];
                        String charset = charsets[1];
                        if (!ValueUtils.isEmpty(pattern) && !ValueUtils.isEmpty(charset)) {
                            try {
                                if (this.charsetMap == null)
                                    this.charsetMap = new HashMap<Pattern, String>();
                                this.charsetMap.put(Pattern.compile(pattern), charset);
                            } catch (Exception e) {
                                logger.error(this, e);
                            }
                        }
                    }
                }
            }
        }
    }
}
