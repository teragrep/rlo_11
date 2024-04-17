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
package com.teragrep.rlo_11.key;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hostname implements Validatable {

    private static final Pattern hostnamePattern = Pattern.compile("^[a-zA-Z0-9\\.\\-]+$");

    public final String hostname;

    public final boolean isStub;

    public Hostname(String hostname) {
        this.hostname = hostname;
        this.isStub = false;
    }

    public Hostname() {
        this.hostname = "";
        this.isStub = true;
    }

    @Override
    public boolean validate() {
        if (isStub) {
            throw new IllegalStateException("Unable to validate stub hostname");
        }

        boolean rv = false;
        Matcher hostnameMatcher = hostnamePattern.matcher(hostname);
        if (hostnameMatcher.matches()) {
            rv = true;
        }

        return rv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Hostname hostname1 = (Hostname) o;
        return isStub == hostname1.isStub && Objects.equals(hostname, hostname1.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, isStub);
    }
}
