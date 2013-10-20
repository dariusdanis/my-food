package com.tieto.food.ui.event;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.entity.Notification;
import com.tieto.food.domain.entity.User;
import com.tieto.food.domain.service.UserService;
import com.tieto.food.ui.BaseWebTest;
import com.tieto.food.ui.utils.MyFoodSession;

@Transactional
public class AddEventPageTest extends BaseWebTest {
	
	@Autowired
	private UserService userService;
	
    @Test
    public void rendersSuccessfullyWithLogin() {
        User tempUser = new User("tomas@rupsys.com", "tomas", "rupsys");
        tempUser.setNotification(new ArrayList<Notification>());       
        MyFoodSession.get().setUser(userService
                .merge(tempUser));
        tester.startPage(AddEventPage.class);
    }

    @Test
    public void sessionDoesNotCreateByDefault(){
    	MyFoodSession mfs = (MyFoodSession) tester.getSession();
    	assertTrue(mfs.getUser() == null);
    }
}
