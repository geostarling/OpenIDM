/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2016 ForgeRock AS.
 */
package org.forgerock.openidm.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test the DateUtil class.
 */
public class DateUtilTest {

    DateUtil dateUtil;
    
    @BeforeClass
    public void beforeClass() throws Exception {
        dateUtil = DateUtil.getDateUtil();
    }
    
    @DataProvider
    public Object[][] timestampIntervalData() {
        return new Object[][] {
            // test interval format start/period
            { "2016-01-01T09:01:00.000Z", "2016-01-01T09:01:00.000Z/P1M", true },
            { "2016-03-01T09:01:00.000Z", "2016-01-01T09:01:00.000Z/P1M", false },
            // test interval format period/stop
            { "2016-01-01T09:01:00.000Z", "P1M1DT01H01M/2016-02-01T09:01:00.000Z", true },
            { "2016-03-01T09:01:00.000Z", "P1M1DT01H01M/2016-02-01T09:01:00.000Z", false },
            // test interval format start/stop
            { "2016-02-01T09:01:00.000Z", "2016-01-01T09:01:00.000Z/2016-03-01T09:01:00.000Z", true },
            { "2016-04-01T09:01:00.000Z", "2016-01-01T09:01:00.000Z/2016-03-01T09:01:00.000Z", false }
        };
    }

    @DataProvider
    public Object[][] dateDifferenceInDaysData() {
        return new Object[][] {
            { "dd/MM/yyyy", "01/12/2016", "11/12/2016", true, 10 },
            { "dd/MM/yyyy", "01/12/2016", "11/12/2016", false, 10 },
            { "dd-MM-yyyy hh:mm:ss", "01-12-2016 12:00:00", "11-12-2016 12:00:00", true, 10 },
            { "dd-MM-yyyy hh:mm:ss", "01-12-2016 12:00:00", "11-12-2016 12:00:00", false, 10 }
        };
    }
    
    @Test(dataProvider = "timestampIntervalData")
    public void testIsTimestampWithinInterval(String timestamp, String interval, boolean result) {
        DateTime instant = dateUtil.parseIfDate(timestamp);
        assertThat(dateUtil.isTimestampWithinInterval(instant, interval)).isEqualTo(result);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidFormat() {
        String interval = "2016-01-01T09:01:00....invalid";
        dateUtil.isNowWithinInterval(interval);
    }
    
    @Test
    public void testIsNowWithinInterval() {
        String passInterval = dateUtil.formatDateTime(DateTime.now().minusDays(1)) 
                + "/" 
                + dateUtil.formatDateTime(DateTime.now().plusDays(1));
        String failInterval = dateUtil.formatDateTime(DateTime.now().plusDays(1)) 
                + "/" 
                + dateUtil.formatDateTime(DateTime.now().plusDays(2));

        assertThat(dateUtil.isNowWithinInterval(passInterval)).isEqualTo(true);
        assertThat(dateUtil.isNowWithinInterval(failInterval)).isEqualTo(false);
    }
    
    @Test(dataProvider = "dateDifferenceInDaysData")
    public void testGetDateDifferenceInDays(String format, String start, String end, Boolean includeDay, int diff) 
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        assertThat(DateUtil.getDateDifferenceInDays(sdf.parse(start), sdf.parse(end), includeDay))
                .isEqualTo(includeDay ? diff + 1 : diff);
    }
}
