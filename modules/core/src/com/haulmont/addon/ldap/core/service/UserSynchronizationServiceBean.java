package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.MatchingRuleApplierInitializer;
import com.haulmont.addon.ldap.core.spring.events.*;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service(UserSynchronizationService.NAME)
public class UserSynchronizationServiceBean implements UserSynchronizationService {

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Inject
    @Qualifier(CubaUserDao.NAME)
    private CubaUserDao cubaUserDao;

    @Inject
    @Qualifier(MatchingRuleDao.NAME)
    private MatchingRuleDao matchingRuleDao;

    @Inject
    @Qualifier(MatchingRuleApplierInitializer.NAME)
    private MatchingRuleApplierInitializer matchingRuleApplierInitializer;

    @Inject
    private Metadata metadata;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void synchronizeUser(String login) {
        ApplyMatchingRuleContext ldapUser = ldapUserDao.getLdapUserWrapper(login);
        User tmp = cubaUserDao.getCubaUserByLogin(login);
        User cubaUser = setCommonAttributesFromLdapUser(ldapUser, tmp, login);
        List<MatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
        List<UserRole> originalUserRoles = new ArrayList<>(cubaUser.getUserRoles());
        applicationEventPublisher.publishEvent(new BeforeUserUpdatedFromLdapEvent(this, ldapUser, cubaUser));
        matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRules(matchingRules, ldapUser, cubaUser);
        applicationEventPublisher.publishEvent(new AfterUserUpdatedFromLdapEvent(this, ldapUser, cubaUser));
        cubaUserDao.saveCubaUser(cubaUser, originalUserRoles);
    }

    private User setCommonAttributesFromLdapUser(ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser, String login) {
        //TODO: position, language
        User cu = cubaUser == null ? metadata.create(User.class) : cubaUser;
        if (cubaUser == null) {//only for new users
            applicationEventPublisher.publishEvent(new BeforeNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cu));
            cu.setLogin(login);
            cu.setUserRoles(new ArrayList<>());
        }
        cu.setEmail(applyMatchingRuleContext.getLdapUser().getEmail());
        cu.setName(applyMatchingRuleContext.getLdapUser().getCn());
        cu.setLastName(applyMatchingRuleContext.getLdapUser().getSn());

        Boolean userDisabled = applyMatchingRuleContext.getLdapUser().getDisabled();
        if (userDisabled) {
            applicationEventPublisher.publishEvent(new BeforeUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cu));
            cu.setActive(false);
            applicationEventPublisher.publishEvent(new AfterUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cu));
        } else {
            cu.setActive(true);
        }

        if (cubaUser == null) {//only for new users
            applicationEventPublisher.publishEvent(new AfterNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cu));
        }
        return cu;
    }
}
