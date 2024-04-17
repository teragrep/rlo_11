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

import com.teragrep.rlo_11.key.AppName;
import com.teragrep.rlo_11.key.Hostname;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class KIN02RoutingTest {

    @Test
    public void testRouting() throws FileNotFoundException {
        KIN02Routing kin02Routing = new KIN02Routing("src/test/resources/kin_02");

        String account = "1234567890";
        String logGroup = "/example/logGroupName/ThatExists";

        Hostname hostname = kin02Routing.getHostname(account);
        AppName appName = kin02Routing.getAppName(logGroup);

        Assertions.assertEquals("1234567890.host.example.com", hostname.hostname);
        Assertions.assertEquals("exampleAppName", appName.appName);
    }

    @Test
    public void testFailRouting() throws FileNotFoundException {
        KIN02Routing kin02Routing = new KIN02Routing("src/test/resources/kin_02");

        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            String account = "0987654321";
            String logGroup = "/example/logGroupName/ThatExists";
            Hostname hostname = kin02Routing.getHostname(account);
            AppName appName = kin02Routing.getAppName(logGroup);

            appName.validate();
            hostname.validate(); // throws
        });

        Assertions.assertEquals("Unable to validate stub hostname", thrown.getMessage());

        IllegalStateException thrownToo = Assertions.assertThrows(IllegalStateException.class, () -> {
            String account = "1234567890";
            String logGroup = "/example/logGroupName/ThatDoesNotExists";

            Hostname hostname = kin02Routing.getHostname(account);
            AppName appName = kin02Routing.getAppName(logGroup);

            hostname.validate();
            appName.validate(); // throws
        });

        Assertions.assertEquals("Unable to validate stub appName", thrownToo.getMessage());
    }
}
