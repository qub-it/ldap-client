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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class QueryReplyElement {

    private static String EMPTY = "";
    private final HashMap<String, List<String>> responseMap;
    private final String[] replyAttributes;

    QueryReplyElement(String[] replyAttributes) {
	this.responseMap = new HashMap<String, List<String>>();
	this.replyAttributes = replyAttributes;
    }

    void addAttribute(String id, String value) {
	List<String> list = this.responseMap.get(id);
	if (list == null) {
	    list = new ArrayList<String>();
	    this.responseMap.put(id, list);
	}
	list.add(value);
    }

    public boolean isWithAllAttributesFilled() {
	Set<String> keySet = this.responseMap.keySet();
	if (keySet.size() == this.replyAttributes.length) {
	    keySet.removeAll(Arrays.asList(this.replyAttributes));
	    return keySet.size() == 0;
	}
	return false;
    }

    public boolean isAttributePresent(String id) {
	return this.responseMap.get(id) != null;
    }

    public List<String> getListAttribute(String id) {
	List<String> list = this.responseMap.get(id);
	return list == null ? Collections.EMPTY_LIST : list;
    }

    public String getSimpleAttribute(String id) {
	List<String> list = this.responseMap.get(id);
	if (list == null) {
	    return EMPTY;
	}
	if (list.size() > 1) {
	    throw new RuntimeException("Attribute with id: " + id + " is not a simple attribute since there are " + list.size()
		    + " values for it. Use getListAttribute instead!");
	}
	return list.isEmpty() ? EMPTY : list.get(0);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder("Requested attributes: ");
	builder.append(replyAttributes);
	builder.append("\n");
	builder.append("WithAllAttributesFilled: ");
	builder.append(isWithAllAttributesFilled());
	builder.append("\n");
	builder.append("Present attributes: ");
	Set<Entry<String, List<String>>> entrySet = this.responseMap.entrySet();
	for (Entry<String, List<String>> entry : entrySet) {
	    builder.append(entry.getKey());
	    builder.append(": ");
	    builder.append(entry.getValue());
	    builder.append("\n");
	}
	return builder.toString();
    }
}
