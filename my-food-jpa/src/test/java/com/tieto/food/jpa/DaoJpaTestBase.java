package com.tieto.food.jpa;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(locations = { "classpath:spring/jpa-mapping.xml",
        "classpath:spring/datasourceTest.xml", "classpath:spring/domain.xml" })
public abstract class DaoJpaTestBase {
}