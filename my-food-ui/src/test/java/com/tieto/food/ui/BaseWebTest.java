package com.tieto.food.ui;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/jpa-mapping.xml",
        "classpath:spring/datasourceTest.xml", "classpath:spring/domain.xml",
        "classpath:spring/wicket.xml" })
public abstract class BaseWebTest implements ApplicationContextAware {

    protected WicketTester tester;

    private ApplicationContext context;

    @Before
    public void setUp() throws IOException, Exception {
        tester = new WicketTester(new Application() {
            @Override
            public ServletContext getServletContext() {
                ServletContext servletContext = super.getServletContext();
                XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
                applicationContext.setParent(context);
                applicationContext
                        .setConfigLocation("classpath:/spring/emptyApplicationContext.xml");
                applicationContext.setServletContext(servletContext);
                applicationContext.refresh();
                servletContext
                        .setAttribute(
                                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                                applicationContext);
                return servletContext;
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        this.context = context;
    }
}
