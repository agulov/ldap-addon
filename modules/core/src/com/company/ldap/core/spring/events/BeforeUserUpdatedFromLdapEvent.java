package com.company.ldap.core.spring.events;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.context.ApplicationEvent;

public class BeforeUserUpdatedFromLdapEvent extends ApplicationEvent {

    private final ApplyMatchingRuleContext applyMatchingRuleContext;
    private final User cubaUser;

    public BeforeUserUpdatedFromLdapEvent(Object source, ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser) {
        super(source);
        this.applyMatchingRuleContext = applyMatchingRuleContext;
        this.cubaUser = cubaUser;
    }
}
