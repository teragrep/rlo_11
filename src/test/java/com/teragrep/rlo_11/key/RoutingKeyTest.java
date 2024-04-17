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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoutingKeyTest {

    @Test
    public void testRoutingKey() {
        Hostname hostname = new Hostname("host.example.com");
        AppName appName = new AppName("exampleAppName");

        Assertions.assertTrue(hostname.validate());
        Assertions.assertTrue(appName.validate());
        Assertions.assertEquals("host.example.com", hostname.hostname);

        // appName and compatible appName should be the same
        Assertions.assertEquals("exampleAppName", appName.appName);
        Assertions.assertEquals("exampleAppName", appName.asCompatible().appName);
    }

    @Test
    public void testInvalidAppName() {
        AppName appName = new AppName(":trimmed1");
        Assertions.assertFalse(appName.validate());

        AppName appName2 = new AppName("/trimmed2");
        Assertions.assertFalse(appName2.validate());

        AppName appName3 = new AppName("[trimmed3");
        Assertions.assertFalse(appName3.validate());

        AppName appName4 = new AppName(".");
        Assertions.assertFalse(appName4.validate());

        AppName appName5 = new AppName("..");
        Assertions.assertFalse(appName5.validate());

        // Empty tag is a valid case
        AppName appName6 = new AppName("");
        Assertions.assertTrue(appName6.validate());
    }

    @Test
    public void testAppNameSanitation() {
        AppName appName = new AppName("F0o@bar");
        Assertions.assertEquals("F0o", appName.asCompatible().appName);

        AppName appName2 = new AppName("F0o.bar");
        Assertions.assertEquals("F0o.bar", appName2.asCompatible().appName);

        AppName appName3 = new AppName("F0o_bar");
        Assertions.assertEquals("F0o_bar", appName3.asCompatible().appName);

        AppName appName4 = new AppName("F0o,bar");
        Assertions.assertEquals("F0o", appName4.asCompatible().appName);

        AppName appName5 = new AppName("F0o-bar");
        Assertions.assertEquals("F0o-bar", appName5.asCompatible().appName);
    }
}
