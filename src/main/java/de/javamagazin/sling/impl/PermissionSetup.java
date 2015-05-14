package de.javamagazin.sling.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static de.javamagazin.sling.impl.Constants.CONTACT_INBOX;
import static javax.jcr.security.Privilege.JCR_ADD_CHILD_NODES;
import static javax.jcr.security.Privilege.JCR_MODIFY_PROPERTIES;
import static javax.jcr.security.Privilege.JCR_READ;
import static javax.jcr.security.Privilege.JCR_WRITE;
import static org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils.deny;
import static org.apache.sling.jcr.base.util.AccessControlUtil.getAccessControlManager;
import static org.apache.sling.jcr.base.util.AccessControlUtil.getPrincipalManager;

/**
 * Sets up permissions for the anonymous user in order to allow anonymous users to create contact request
 * content in the JCR. This uses the pre- Jackrabbit-Oak API. For Oak, see
 * <a href="http://jackrabbit.apache.org/oak/docs/security/permission/differences.html">http://jackrabbit.apache.org/oak/docs/security/permission/differences.html</a>.
 *
 * @author Olaf Otto
 */
@Component(immediate = true)
public class PermissionSetup {
    @Reference
    private SlingRepository repository;

    @Activate
    protected void activate() throws RepositoryException {
        // NOTE: One *must not* use the administrative session this way in a real-world scenario,
        // but use service authentication instead - see https://sling.apache.org/documentation/the-sling-engine/service-authentication.html
        Session session = this.repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        try {
            JackrabbitAccessControlManager acMgr = (JackrabbitAccessControlManager) getAccessControlManager(session);

            removePreviousInboxACLs(acMgr);
            grantWritePermissionsBelowInboxNodeToAnonymous(session, acMgr);

            session.save();
        } finally {
            session.logout();
        }
    }

    private void grantWritePermissionsBelowInboxNodeToAnonymous(Session session, JackrabbitAccessControlManager acMgr) throws RepositoryException {
        PrincipalManager principalManager = getPrincipalManager(session);
        Principal principal = principalManager.getPrincipal("anonymous");

        // Deny read access to the parent node of the requests inbox resource to prevent anonymous from recursively reading
        // the content in the inbox, e.g. by using a json view of the parent resources
        deny(session.getNode(CONTACT_INBOX).getParent(), principal.getName(), JCR_READ);

        // Allow adding children below the inbox folder and allow read access to the inbox itself (otherwise, no content could be added to that node,
        // as it would not exist for anonymous)
        JackrabbitAccessControlList list = (JackrabbitAccessControlList) acMgr.getApplicablePolicies(CONTACT_INBOX).nextAccessControlPolicy();
        list.addEntry(principal, new Privilege[]{acMgr.privilegeFromName(JCR_ADD_CHILD_NODES),
                acMgr.privilegeFromName(JCR_READ)}, true);

        // Allow writing content below the contacts folder and allow modifying the written resource's properties (this is required for the
        // initial property creation). To apply the ACL to the children of an existing node, a "globbing" (pattern match) is added (rep:glob) to the ACL.
        ValueFactory vf = session.getValueFactory();
        Map<String, Value> restrictToChildren = new HashMap<>();
        restrictToChildren.put("rep:glob", vf.createValue("/*"));

        list.addEntry(principal, new Privilege[]{
                acMgr.privilegeFromName(JCR_WRITE),
                acMgr.privilegeFromName(JCR_MODIFY_PROPERTIES),
                acMgr.privilegeFromName(JCR_READ)
        }, true, restrictToChildren);

        acMgr.setPolicy(CONTACT_INBOX, list);
    }

    private void removePreviousInboxACLs(JackrabbitAccessControlManager acMgr) throws RepositoryException {
        AccessControlPolicyIterator applicablePolicies = acMgr.getApplicablePolicies(CONTACT_INBOX);

        if (!applicablePolicies.hasNext()) {
            for (AccessControlPolicy policy : acMgr.getPolicies(CONTACT_INBOX)) {
                acMgr.removePolicy(CONTACT_INBOX, policy);
            }
        }
    }
}
