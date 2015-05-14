package de.javamagazin.sling.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.servlets.post.NodeNameGenerator;

import static de.javamagazin.sling.impl.Constants.CONTACT_INBOX;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

/**
 * Generates a hard-to-guess names for contact requests.
 * This is also an example for the extension-by-service-publication pattern of Sling: System behavior can be altered simply
 * by means of publishing an OSGi service implementing an extension interface - here, {@link NodeNameGenerator}.
 *
 * @author Olaf Otto
 */
@Service
@Component
public class ContactNodeNameGenerator implements NodeNameGenerator {
    @Override
    public String getNodeName(SlingHttpServletRequest request, String parentPath, boolean requirePrefix, NodeNameGenerator defaultNodeNameGenerator) {
        return parentPath.equals(CONTACT_INBOX) ? randomAlphanumeric(12) : null;
    }
}
