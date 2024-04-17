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
package com.teragrep.rlo_11.cfe_16;

import com.google.gson.Gson;
import com.teragrep.jlt_01.pojo.JsonStringLookupTable;
import com.teragrep.rlo_11.key.AppName;
import com.teragrep.rlo_11.key.Hostname;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CFE16Routing {
    // hec_hosts.json
    /*
    NOTE hec_tags.json is not used and must not be used see cfe_07/issue/56
     */

    private final Map<String, String> tokenToHostMap;

    public CFE16Routing(String lookupPath) throws IOException {
        this.tokenToHostMap = loadHostsFile(lookupPath);
    }

    public AppName getAppName(String authenticationToken) {
        return new AppName("capsulated");
    }

    public Hostname getHostname(String authenticationToken) {
        Hostname hostname;
        if (tokenToHostMap.containsKey(authenticationToken)) {
            hostname = new Hostname(tokenToHostMap.get(authenticationToken));
        }
        else {
            // return stub
            hostname = new Hostname();
        }
        return hostname;
    }

    private Map<String, String> loadHostsFile(String path) throws IOException {
        final Gson gson = new Gson();

        try (FileReader fileReader = new FileReader(path + "/hec_hosts.json")) {
            try (BufferedReader hostReader = new BufferedReader(fileReader)) {
                JsonStringLookupTable hostsTable = gson.fromJson(hostReader, JsonStringLookupTable.class);

                final Map<String, String> tokenToHostMap = new HashMap<>();
                for (int i = 0; i < hostsTable.getTable().size(); i++) {
                    String token = hostsTable.getTable().get(i).getIndex();
                    String hostname = hostsTable.getTable().get(i).getValue();
                    tokenToHostMap.put(token, hostname);
                }
                return tokenToHostMap;
            }
        }
    }
}
