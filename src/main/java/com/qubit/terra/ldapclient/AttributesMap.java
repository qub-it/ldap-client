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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributesMap implements Serializable {

    private Map<String, List<String>> attributesMap;

    public AttributesMap() {
        super();
        this.attributesMap = new HashMap<String, List<String>>();
    }

    public void add(String attributeName, String value) {
        List<String> list = this.attributesMap.get(attributeName);
        if (list == null) {
            list = new ArrayList<String>();
            this.attributesMap.put(attributeName, list);
        }
        list.add(value);
    }

    public void add(String attributeName, String... values) {
        for (String value : values) {
            add(attributeName, value);
        }
    }

    public void clear() {
        this.attributesMap.clear();
    }

    public void remove(String attributeName) {
        this.attributesMap.remove(attributeName);
    }

    public List<String> get(String attributeName) {
        List<String> list = this.attributesMap.get(attributeName);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        return list;
    }

    Map<String, List<String>> getMap() {
        return this.attributesMap;
    }
}
