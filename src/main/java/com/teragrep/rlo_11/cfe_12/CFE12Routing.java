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
package com.teragrep.rlo_11.cfe_12;

import com.teragrep.rlo_11.key.AppName;
import com.teragrep.rlo_11.key.Hostname;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

public class CFE12Routing {
    /*
     */

    private final LinkedList<LogGroup> logGroupList;

    public CFE12Routing(String lookupPath) throws IOException {
        final LogGroupProcessor logGroupProcessor = new LogGroupProcessor();
        this.logGroupList = logGroupProcessor.load(lookupPath);

    }

    public HashSet<String> getIndexes(Hostname hostname, AppName appName) {
        if (!hostname.validate()) {
            throw new IllegalArgumentException("Unable to getIndexes for invalid hostname");
        }

        if (!appName.validate()) {
            throw new IllegalArgumentException("Unable to getIndexes for invalid appName");
        }

        final HashSet<String> indexes = new HashSet<>();
        // CFE-12 lookups have no way to determine all groups for host, iterating all
        for (LogGroup group : logGroupList) {
            final String index = group.getIndex(hostname.hostname, appName.appName);
            if (index != null) {
                indexes.add(index);
            }
        }
        return indexes;
    }

    public HashSet<String> getSourcetypes(Hostname hostname, AppName appName) {
        if (!hostname.validate()) {
            throw new IllegalArgumentException("Unable to getSourcetypes for invalid hostname");
        }

        if (!appName.validate()) {
            throw new IllegalArgumentException("Unable to getSourcetypes for invalid appName");
        }

        final HashSet<String> sourcetypes = new HashSet<>();
        for (LogGroup group : logGroupList) {
            final String sourcetype = group.getSourcetype(hostname.hostname, appName.appName);
            if (sourcetype != null) {
                sourcetypes.add(sourcetype);
            }
        }
        return sourcetypes;
    }
}