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
package com.teragrep.rlo_11.cfe_07;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.teragrep.jlt_01.pojo.JsonStringLookupTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LogGroupProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogGroupProcessor.class);

    private final Gson gson;
    private final Set<String> targetSet;

    public LogGroupProcessor(Set<String> targetSet) {
        this.gson = new Gson();
        this.targetSet = targetSet;
    }

    public LinkedList<LogGroup> load(String path) throws IOException {
        final LinkedList<LogGroup> logGroupList = new LinkedList<>();
        Set<String> groups;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
            LogGroupSearch groupSearch = new LogGroupSearch(stream);
            groups = groupSearch.getGroupList();
        }

        LOGGER.debug("Found log group names <[{}]>", groups);

        if (groups != null && !groups.isEmpty()) {
            for (String group : groups) {
                // read hosts for the Group
                final Set<String> hosts = loadHostsFile(path, group);

                HashMap<String, Set<String>> targetTags = new HashMap<>();
                for (String target : targetSet) {
                    final Set<String> tags = loadFile(path, group, target);
                    targetTags.put(target, tags);
                }

                logGroupList.add(new LogGroup(group, hosts, targetTags));
            }
        }

        LOGGER.debug("Loaded log group list <[{}]>", logGroupList);

        return logGroupList;
    }

    private Set<String> loadFile(String path, String group, String type) throws IOException {
        final Set<String> groupEnabled = new HashSet<>();
        File file = new File(path + "/" + group + "_target_" + type + ".json");
        try (FileReader fileReader = new FileReader(file)) {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                JsonStringLookupTable groupTable;
                try {
                    groupTable = gson.fromJson(bufferedReader, JsonStringLookupTable.class);
                }
                catch (JsonSyntaxException e) {
                    LOGGER.error("Can't parse file <[{}]>: ", file.getAbsolutePath(), e);
                    throw new JsonSyntaxException(e);
                }
                for (int i = 0; i < groupTable.getTable().size(); i++) {
                    if ("true".equals(groupTable.getTable().get(i).getValue()))
                        groupEnabled.add(groupTable.getTable().get(i).getIndex());
                }
            }

        }
        catch (FileNotFoundException fileNotFoundException) {
            LOGGER
                    .warn(
                            "Skipped group <[{}]> target <{}> due to exception: {}", group, type,
                            fileNotFoundException.getMessage()
                    );
        }

        return groupEnabled;
    }

    private Set<String> loadHostsFile(String path, String group) throws IOException {
        try (FileReader fileReader = new FileReader(path + "/" + group + "_hosts.json")) {
            try (BufferedReader hostReader = new BufferedReader(fileReader)) {
                JsonStringLookupTable hostsTable = gson.fromJson(hostReader, JsonStringLookupTable.class);

                final Set<String> groupHosts = new HashSet<>();
                for (int i = 0; i < hostsTable.getTable().size(); i++) {
                    if ("true".equals(hostsTable.getTable().get(i).getValue()))
                        groupHosts.add(hostsTable.getTable().get(i).getIndex());
                }
                return groupHosts;
            }
        }
    }
}
