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

import java.util.ArrayList;
import java.util.List;

public class QueryReply {

    private final List<QueryReplyElement> elements;
    private final String[] replyAttributes;
    private QueryReplyElement currentElement = null;

    public QueryReply(String[] replyAttributes) {
        this.elements = new ArrayList<QueryReplyElement>();
        this.replyAttributes = replyAttributes;
    }

    void addAttribute(String id, String value) {
        currentElement.addAttribute(new String(id), new String(value));
    }

    public void createNewElement() {
        this.currentElement = new QueryReplyElement(replyAttributes);
        this.elements.add(this.currentElement);
    }

    public int getNumberOfResults() {
        return this.elements.size();
    }

    public List<QueryReplyElement> getResults() {
        return this.elements;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Number of results: ");
        builder.append(this.elements.size());
        builder.append("\n");
        int i = 0;
        for (QueryReplyElement element : this.elements) {
            builder.append(i);
            builder.append(":\n");
            builder.append(element.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
