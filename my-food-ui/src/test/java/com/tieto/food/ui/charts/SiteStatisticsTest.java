package com.tieto.food.ui.charts;

import org.junit.Test;

import com.tieto.food.ui.BaseWebTest;

public class SiteStatisticsTest extends BaseWebTest {
    
    @Test
    public void testIfPageRenders() {
        tester.startPage(SiteStatistics.class);
    }
}