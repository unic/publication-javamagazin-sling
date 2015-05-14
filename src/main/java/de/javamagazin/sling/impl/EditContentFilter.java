package de.javamagazin.sling.impl;

import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.apache.felix.scr.annotations.sling.SlingFilterScope.INCLUDE;

/**
 * Wraps included components with HTML exposing the included resource's path, resource type and the
 * selectors used for the inclusion to the DOM. This is also an example of the Service Component Runtime (SCR)
 * convenience annotations supported by the maven-scr plugin.
 *
 * @author Olaf Otto
 */
@SlingFilter(scope = INCLUDE, order = 0)
public class EditContentFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        String selectors = slingRequest.getRequestPathInfo().getSelectorString();

        response.getWriter().write("<div class='editable' data-resource='" + slingRequest.getResource().getPath() + "' " +
                                                         "data-selectors='" + (selectors != null ? selectors : "") + "' " +
                                                         "data-type='" + slingRequest.getResource().getResourceType() + "'>");
        chain.doFilter(request, response);
        response.getWriter().write("</div>");
    }

    public void destroy() {
    }
}
