/*
 * Java Record Routing Library RLO-11
 * Copyright (C) 2021-2024 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.rlo_11.kin_02;

import com.google.gson.Gson;
import com.teragrep.jlt_01.pojo.JsonStringLookupTable;
import com.teragrep.rlo_11.key.AppName;
import com.teragrep.rlo_11.key.Hostname;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class KIN02Routing {
    // stream-processor_hosts.json
    // stream-processor_tags.json

    private final Gson gson = new Gson();

    private final Map<String, String> accountIdToHostnameMap;
    private final Map<String, String> logGroupToAppNameMap;

    public KIN02Routing(String lookupPath) throws FileNotFoundException {
        this.accountIdToHostnameMap = loadFile(lookupPath, "hosts");
        this.logGroupToAppNameMap = loadFile(lookupPath, "tags");
    }

    public AppName getAppName(String logGroup) {
        AppName appName;
        if (logGroupToAppNameMap.containsKey(logGroup)) {
            appName = new AppName(logGroupToAppNameMap.get(logGroup)).asCompatible();
        }
        else {
            // stub
            appName = new AppName();
        }
        return appName;
    }

    public Hostname getHostname(String accountId) {
        Hostname hostname;
        if (accountIdToHostnameMap.containsKey(accountId)) {
            hostname = new Hostname(accountIdToHostnameMap.get(accountId));
        }
        else {
            // stub
            hostname = new Hostname();
        }
        return hostname;
    }

    private Map<String, String> loadFile(String path, String type) throws FileNotFoundException {

        BufferedReader fileReader = new BufferedReader(new FileReader(path + "/stream-processor_" + type + ".json"));
        JsonStringLookupTable typeTable = gson.fromJson(fileReader, JsonStringLookupTable.class);

        final Map<String, String> typeMap = new HashMap<>();
        for (int i = 0; i < typeTable.getTable().size(); i++) {
            String key = typeTable.getTable().get(i).getIndex();
            String value = typeTable.getTable().get(i).getValue();
            typeMap.put(key, value);
        }
        return typeMap;
    }
}
