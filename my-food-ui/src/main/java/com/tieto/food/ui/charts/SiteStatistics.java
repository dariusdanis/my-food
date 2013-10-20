package com.tieto.food.ui.charts;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.Cursor;
import com.googlecode.wickedcharts.highcharts.options.DataLabels;
import com.googlecode.wickedcharts.highcharts.options.Events;
import com.googlecode.wickedcharts.highcharts.options.ExportingOptions;
import com.googlecode.wickedcharts.highcharts.options.Function;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.PlotLine;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.PointOptions;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.HighchartsColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.color.RadialGradient;
import com.googlecode.wickedcharts.highcharts.options.functions.AddPointFunction;
import com.googlecode.wickedcharts.highcharts.options.functions.RemovePointFunction;
import com.googlecode.wickedcharts.highcharts.options.series.CoordinatesSeries;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;
import com.tieto.food.domain.entity.Event;
import com.tieto.food.domain.service.EventService;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BasePage;

@SuppressWarnings("all") 
public class SiteStatistics extends BasePage {

    private static final String ON_PLACE_EVENT = "On place";
    private static final String TAKE_OUT_EVENT = "Take out";
    private static final String RESTAURANT_EVENT = "Restaurant";

    @SpringBean
    private EventService eventService;

    @SpringBean
    private UserService userService;

    private static final long serialVersionUID = 1L;

    protected void onInitialize() {
        super.onInitialize();
        initializeUserEventCountChart();
        initializeEventTypePieChart();
        iniitalizeFunChart();
        initializeTopEventsChart();
    }

    private void initializeTopEventsChart() {
        Options options = new Options();
        Event[] events = pickTopEvents();

        if (events.length < 5) {
            add(new Label("popularEventsChart",
                    "Top events chart will be visible when there are at least 5 created events"));
            return;
        }

        options.setChartOptions(new ChartOptions().setType(SeriesType.COLUMN));

        options.setTitle(new Title("Top Events"));
        options.setxAxis(new Axis().setCategories(events[0].getTitle(),
                events[1].getTitle(), events[2].getTitle(),
                events[3].getTitle(), events[4].getTitle()));

        options.setyAxis(new Axis().setMin(0).setTitle(
                new Title("Participants")));

        options.setLegend(new Legend().setLayout(LegendLayout.VERTICAL)
                .setBackgroundColor(new HexColor("#FFFFFF"))
                .setAlign(HorizontalAlignment.LEFT)
                .setVerticalAlign(VerticalAlignment.TOP).setX(100).setY(70)
                .setFloating(Boolean.TRUE).setShadow(Boolean.TRUE));

        options.setPlotOptions(new PlotOptionsChoice()
                .setColumn(new PlotOptions().setPointPadding(0.2f)
                        .setBorderWidth(0)));

        options.addSeries(new SimpleSeries().setName("Top 5 events").setData(
                events[0].getUsers().size(), events[1].getUsers().size(),
                events[2].getUsers().size(), events[3].getUsers().size(),
                events[4].getUsers().size()));

        add(new Chart("popularEventsChart", options));

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Event[] pickTopEvents() {
        List<Event> eventList = eventService.listAll();
        Event[] eventArray = new Event[eventList.size()];
        for (int i = 0; i < eventList.size(); i++) {
            eventArray[i] = eventList.get(i);
        }
        Arrays.sort(eventArray, new Comparator() {
            @Override
            public int compare(Object e1, Object e2) {
                Event event1 = (Event) e1;
                Event event2 = (Event) e2;
                int event1Users = event1.getUsers().size();
                int event2Users = event2.getUsers().size();
                if (event1Users > event2Users) {
                    return -1;
                } else if (event1Users == event2Users) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        return eventArray;
    }

    private void iniitalizeFunChart() {

        Options options = new Options();

        options.setChartOptions(new ChartOptions().setType(SeriesType.SCATTER)
                .setMargin(Arrays.asList(70, 50, 60, 80))
                .setEvents(new Events().setClick(new AddPointFunction())));

        options.setTitle(new Title("Draw the data you need !"));

        options.setSubtitle(new Title(
                "Click the plot area to add a point. Click a point to remove it. Have fun !"));

        options.setxAxis(new Axis().setMinPadding(0.2f).setMaxPadding(0.2f)
                .setMaxZoom(60));

        options.setyAxis(new Axis()
                .setTitle(new Title("Value"))
                .setMinPadding(0.2f)
                .setMaxPadding(0.2f)
                .setMaxZoom(60)
                .addPlotLine(
                        new PlotLine().setValue(0f).setWidth(1)
                                .setColor(HexColor.fromString("#808080"))));

        options.setLegend(new Legend().setEnabled(Boolean.FALSE));

        options.setExporting(new ExportingOptions().setEnabled(Boolean.FALSE));

        options.setPlotOptions(new PlotOptionsChoice()
                .setSeries(new PlotOptions().setLineWidth(1).setPoint(
                        new PointOptions().setEvents(new Events()
                                .setClick(new RemovePointFunction())))));

        options.addSeries(new CoordinatesSeries().addPoint(20, 20).addPoint(80,
                80));
        add(new Chart("funChart", options));
    }

    private void initializeEventTypePieChart() {

        Options options = new Options();

        options.setChartOptions(new ChartOptions()
                .setPlotBackgroundColor(new NullColor())
                .setPlotBorderWidth(null).setPlotShadow(Boolean.FALSE));

        options.setTitle(new Title("Event count by type"));

        options.setTooltip(new Tooltip().setFormatter(new Function()
                .setFunction("return '<b>'+ this.point.name +':</b> ' +' ('+ parseFloat(this.percentage).toFixed(2) +'% )';")));

        options.setPlotOptions(new PlotOptionsChoice()
                .setPie(new PlotOptions()
                        .setAllowPointSelect(Boolean.TRUE)
                        .setCursor(Cursor.POINTER)
                        .setDataLabels(
                                new DataLabels()
                                        .setEnabled(Boolean.TRUE)
                                        .setColor(new HexColor("#000000"))
                                        .setConnectorColor(
                                                new HexColor("#000000"))
                                        .setFormatter(
                                                new Function()
                                                        .setFunction("return '<b>'+ this.point.name +':</b> ' +' ('+ parseFloat(this.percentage).toFixed(2) +'% )';")))));

        Map<String, Double> map = countEventTypePercentages();

        options.addSeries(new PointSeries()
                .setType(SeriesType.PIE)
                .setName("Event type popularity")
                .addPoint(
                        new Point(ON_PLACE_EVENT, map.get(ON_PLACE_EVENT))
                                .setColor(new RadialGradient()
                                        .setCx(0.5)
                                        .setCy(0.3)
                                        .setR(0.7)
                                        .addStop(0, new HighchartsColor(0))
                                        .addStop(
                                                1,
                                                new HighchartsColor(0)
                                                        .brighten(-0.3f))))
                .addPoint(
                        new Point(TAKE_OUT_EVENT, map.get(TAKE_OUT_EVENT))
                                .setColor(new RadialGradient()
                                        .setCx(0.5)
                                        .setCy(0.3)
                                        .setR(0.7)
                                        .addStop(0, new HighchartsColor(3))
                                        .addStop(
                                                1,
                                                new HighchartsColor(3)
                                                        .brighten(-0.3f))))
                .addPoint(
                        new Point(RESTAURANT_EVENT, map.get(RESTAURANT_EVENT))
                                .setSliced(Boolean.TRUE)
                                .setSelected(Boolean.TRUE)
                                .setColor(
                                        new RadialGradient()
                                                .setCx(0.5)
                                                .setCy(0.3)
                                                .setR(0.7)
                                                .addStop(0,
                                                        new HighchartsColor(2))
                                                .addStop(
                                                        1,
                                                        new HighchartsColor(2)
                                                                .brighten(-0.3f)))));
        add(new Chart("eventTypesChart", options));
    }

    private HashMap<String, Double> countEventTypePercentages() {

        HashMap<String, Double> map = new HashMap<String, Double>();
        int onPlace = 0;
        int takeOut = 0;
        int restaurant = 0;
        List<Event> eventList = eventService.listAll();
        for (Event event : eventList) {
            String type = event.getEventType().getType();
            if (type.equals(ON_PLACE_EVENT)) {
                onPlace++;
            } else if (type.equals(TAKE_OUT_EVENT)) {
                takeOut++;
            } else if (type.equals(RESTAURANT_EVENT)) {
                restaurant++;
            }
        }
        map.put(ON_PLACE_EVENT, (double) ((100d / eventList.size()) * onPlace));
        map.put(TAKE_OUT_EVENT, (double) ((100d / eventList.size()) * takeOut));
        map.put(RESTAURANT_EVENT,
                (double) ((100d / eventList.size()) * restaurant));
        return map;
    }

    private void initializeUserEventCountChart() {
        Options options = new Options();
        String[] monthArray = initializeMonthArray();
        Number[] userCountArray = initializeUserCountArray();
        Number[] eventCountArray = initializeEventCountArray();
        options.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
        options.setTitle(new Title("Users and events"));
        options.setSubtitle(new Title(
                "Registered users and created events in the past year"));
        options.setxAxis(new Axis().setCategories(Arrays.asList(monthArray)));
        options.setyAxis(new Axis().setTitle(new Title("Users/Events")));
        options.setLegend(new Legend().setLayout(LegendLayout.VERTICAL)
                .setAlign(HorizontalAlignment.RIGHT)
                .setVerticalAlign(VerticalAlignment.TOP).setX(-10).setY(100)
                .setBorderWidth(0));

        options.addSeries(new SimpleSeries().setName("Users").setData(
                Arrays.asList(userCountArray)));
        options.addSeries(new SimpleSeries().setName("Events").setData(
                Arrays.asList(eventCountArray)));

        add(new Chart("userCountChart", options));
    }

    private Number[] initializeEventCountArray() {
        Number[] eventCount = new Number[12];
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int monthCounter = 0;
        for (int i = month + 1; i < 12; i++) {
            cal.set(year - 1, i, 1);
            eventCount[monthCounter] = eventService
                    .listEventsThatHappenedBefore(cal.getTime()).size();
            monthCounter++;
        }
        for (int i = 0; i <= month; i++) {
            if (i != 11) {
                cal.set(year, i + 1, 1);
            } else {
                cal.set(year + 1, 1, 1);
            }
            eventCount[monthCounter] = eventService
                    .listEventsThatHappenedBefore(cal.getTime()).size();
            monthCounter++;
        }
        return eventCount;
    }

    private Number[] initializeUserCountArray() {
        Number[] userCount = new Number[12];
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int monthCounter = 0;
        for (int i = month + 1; i < 12; i++) {
            cal.set(year - 1, i, 1);
            userCount[monthCounter] = userService.listUsersWhoJoinedBefore(
                    cal.getTime()).size();
            monthCounter++;
        }
        for (int i = 0; i <= month; i++) {
            if (i != 11) {
                cal.set(year, i + 1, 1);
            } else {
                cal.set(year + 1, 1, 1);
            }
            userCount[monthCounter] = userService.listUsersWhoJoinedBefore(
                    cal.getTime()).size();
            monthCounter++;
        }
        return userCount;
    }

    private String[] initializeMonthArray() {
        String[] tempArray = new String[] { "Jan", "Feb", "Mar", "Apr", "May",
                "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        String[] monthArray = new String[12];
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int month = cal.get(Calendar.MONTH);
        int monthCounter = 0;
        for (int i = month + 1; i < tempArray.length; i++) {
            monthArray[monthCounter] = tempArray[i];
            monthCounter++;
        }
        for (int i = 0; i <= month; i++) {
            monthArray[monthCounter] = tempArray[i];
            monthCounter++;
        }

        return monthArray;
    }

}