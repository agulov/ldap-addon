package com.company.ldap.core.spring;

import com.company.ldap.core.spring.events.BeforeNewUserCreatedFromLdapEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LdapEventListener implements ApplicationListener<BeforeNewUserCreatedFromLdapEvent> {
    @Override
    public void onApplicationEvent(BeforeNewUserCreatedFromLdapEvent event) {
        System.out.println("Received spring custom event - " + event);
    }

}
