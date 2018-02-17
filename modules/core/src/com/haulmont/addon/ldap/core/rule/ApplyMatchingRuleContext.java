package com.haulmont.addon.ldap.core.rule;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;

import javax.naming.directory.Attributes;
import java.util.HashSet;
import java.util.Set;

public class ApplyMatchingRuleContext {

    private final LdapUser ldapUser;
    private final Attributes ldapUserAttributes;
    private final Set<MatchingRule> appliedRules = new HashSet<>();
    private final Set<Role> appliedRoles = new HashSet<>();
    private final Set<Group> appliedGroups = new HashSet<>();
    private final User cubaUser;
    private boolean isAnyRuleApply = false;

    public ApplyMatchingRuleContext(LdapUser ldapUser, Attributes ldapUserAttributes, User cubaUser) {
        this.ldapUser = ldapUser;
        this.ldapUserAttributes = ldapUserAttributes;
        this.cubaUser= cubaUser;
    }

    public LdapUser getLdapUser() {
        return ldapUser;
    }

    public Attributes getLdapUserAttributes() {
        return ldapUserAttributes;
    }

    public boolean isAnyRuleApply() {
        return isAnyRuleApply;
    }

    public void setAnyRuleApply(boolean anyRuleApply) {
        isAnyRuleApply = anyRuleApply;
    }

    public Set<MatchingRule> getAppliedRules() {
        return appliedRules;
    }

    public Set<Role> getAppliedRoles() {
        return appliedRoles;
    }

    public Set<Group> getAppliedGroups() {
        return appliedGroups;
    }

    public User getCubaUser() {
        return cubaUser;
    }
}
