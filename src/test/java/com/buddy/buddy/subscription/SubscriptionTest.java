package com.buddy.buddy.subscription;


import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.auth.JwtUtils;
import com.buddy.buddy.subscription.controller.SubscriptionController;
import com.buddy.buddy.subscription.entity.Subscription;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
import com.buddy.buddy.subscription.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SubscriptionTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password1");
        User user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password2");
        Subscription subscription1 = new Subscription();
        subscription1.setSubscriber(user1);
        subscription1.setSubscribedTo(user2);

    }

    @Test
    void searchSubscription() throws Exception {

    }

}
