package com.company.ldap.core.service;

import com.company.ldap.core.dao.MatchingRuleDao;
import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.FixedMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.service.MatchingRuleService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(MatchingRuleService.NAME)
public class MatchingRuleServiceBean implements MatchingRuleService{

    @Inject
    @Qualifier(MatchingRuleDao.NAME)
    private MatchingRuleDao matchingRuleDao;

    @Override
    public List<MatchingRule> getMatchingRules() {
        return matchingRuleDao.getMatchingRules();
    }

    @Override
    public int getMatchingRulesCount() {
        return matchingRuleDao.getMatchingRulesCount();
    }

    @Override
    public List<AbstractMatchingRule> getMatchingRulesGui() {
        return matchingRuleDao.getMatchingRulesGui();
    }

    @Override
    public FixedMatchingRule getFixedMatchingRule() {
        return matchingRuleDao.getFixedMatchingRule();
    }

    @Override
    public void updateDisabledStateForMatchingRule(UUID id, Boolean isDisabled) {
        matchingRuleDao.updateDisabledStateForMatchingRule(id, isDisabled);
    }
}
