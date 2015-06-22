/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 * 
 * This file is part of FenixEdu ldap-client.
 *
 * FenixEdu ldap-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu ldap-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu ldap-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qubit.terra.ldapclient;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LdapClient {

    private DirContext context;
    private final String username;
    private final String password;
    private final String url;
    private String baseDomainName;

    public LdapClient(String username, String password, String url, String baseDomainName) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.baseDomainName = baseDomainName;
    }

    public QueryReply retrieveGroupMembers(String groupCommonName) {
        return query("(& (objectcategory=group) (cn=" + groupCommonName + "))", new String[] { "cn", "destinguishedName",
                "description", "member" });
    }

    // OpenLDAP does not have by default the attribute memberOf.
    // To use this feature under openLDAP a memberOf overlay has
    // to be created, more info about overlays here:
    // http://www.openldap.org/doc/admin24/overlays.html
    //
    // 8 March 2013 - Paulo Abrantes
    public QueryReply retrieveGroupsForUser(String userCommonName) {
        return query("(& (objectClass=inetOrgPerson) (cn=" + userCommonName + "))", new String[] { "cn", "distinguishedName",
                "displayName", "mail", "memberOf" });
    }

    public QueryReply readAllUsers() {
        return query("(objectClass=inetOrgPerson)", new String[] { "cn", "distinguishedName", "displayName", "mail",
                "userpassword", "memberOf" });
    }

    public boolean isLoggedIn() {
        return this.context != null;
    }

    public void logout() {
        if (this.context != null) {
            try {
                this.context.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
        this.context = null;
    }

    public boolean login() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, this.url);
        env.put(Context.SECURITY_PRINCIPAL, this.username);
        env.put(Context.SECURITY_CREDENTIALS, this.password);
        env.put(Context.REFERRAL, "follow");

        try {
            this.context = new InitialDirContext(env);
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    protected DirContext getContext() {
        return this.context;
    }

    public QueryReply query(String searchQuery, String... replyAttributes) {
        return query(this.baseDomainName, searchQuery, SearchControls.SUBTREE_SCOPE, replyAttributes);
    }

    public QueryReply query(String searchQuery, int searchScope, String... replyAttributes) {
        return query(this.baseDomainName, searchQuery, searchScope, replyAttributes);
    }

    public QueryReply query(String baseDomainName, String searchQuery, String... replyAttributes) {
        return query(baseDomainName, searchQuery, SearchControls.SUBTREE_SCOPE, replyAttributes);
    }

    public QueryReply query(String baseDomainName, String searchQuery, int searchScope, String... replyAttributes) {
        SearchControls ctls = new SearchControls();
        ctls.setReturningAttributes(replyAttributes);
        ctls.setSearchScope(searchScope);
        QueryReply reply = new QueryReply(replyAttributes);
        try {
            NamingEnumeration<SearchResult> answer = this.context.search(this.baseDomainName, searchQuery, ctls);
            processResponse(reply, answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply;

    }

    protected void processResponse(QueryReply reply, NamingEnumeration<SearchResult> answer) throws NamingException {
        while (answer.hasMore()) {
            reply.createNewElement();
            SearchResult rslt = answer.next();
            Attributes attrs = rslt.getAttributes();
            NamingEnumeration<? extends Attribute> all = attrs.getAll();
            while (all.hasMore()) {
                Attribute next = all.next();
                NamingEnumeration<?> all2 = next.getAll();
                while (all2.hasMore()) {
                    Object next2 = all2.next();
                    reply.addAttribute(next.getID(), next2.toString());
                }

            }
        }
    }

    public void addToExistingContext(String contextId, List<String> objectClasses, AttributesMap attributesMap) {
        performWrite(contextId, objectClasses, attributesMap, addExecutor);
    }

    public void replaceInExistingContext(String contextId, List<String> objectClasses, AttributesMap attributesMap) {

        performWrite(contextId, objectClasses, attributesMap, replaceExecutor);

    }

    public void writeNewContext(String contextId, List<String> objectClasses, AttributesMap attributesMap) {
        performWrite(contextId, objectClasses, attributesMap, createExecutor);

    }

    private static interface Executor {
        public void execute(DirContext dirContext, String contextId, Attributes attributes);
    }

    private void performWrite(String contextId, List<String> objectClasses, AttributesMap attributesMap, Executor executor) {
        Attributes attributes = new BasicAttributes();
        Attribute objectClassAttribute = new BasicAttribute("objectClass");
        for (String objectClass : objectClasses) {
            objectClassAttribute.add(objectClass);
        }
        if (!objectClasses.isEmpty()) {
            attributes.put(objectClassAttribute);
        }

        Set<Entry<String, List<String>>> entrySet = attributesMap.getMap().entrySet();
        for (Entry<String, List<String>> entry : entrySet) {
            Attribute attribute = new BasicAttribute(entry.getKey());
            for (String value : entry.getValue()) {
                attribute.add(value);
            }
            attributes.put(attribute);
        }

        executor.execute(this.context, contextId, attributes);

    }

    private Executor addExecutor = new Executor() {

        public void execute(DirContext dirContext, String contextId, Attributes attributes) {
            try {
                dirContext.modifyAttributes(contextId, DirContext.ADD_ATTRIBUTE, attributes);
            } catch (NamingException e) {
                throw new RuntimeException("problems while adding info to entry: " + contextId);
            }

        }

    };

    private Executor replaceExecutor = new Executor() {

        public void execute(DirContext dirContext, String contextId, Attributes attributes) {
            try {
                dirContext.modifyAttributes(contextId, DirContext.REPLACE_ATTRIBUTE, attributes);
            } catch (NamingException e) {
                throw new RuntimeException("problems while replacing information in entry: " + contextId);
            }

        }

    };

    private Executor createExecutor = new Executor() {

        public void execute(DirContext dirContext, String contextId, Attributes attributes) {
            try {
                dirContext.createSubcontext(contextId, attributes);
            } catch (NamingException e) {
                throw new RuntimeException("problems creating entry: " + contextId);
            }
        }

    };

}
