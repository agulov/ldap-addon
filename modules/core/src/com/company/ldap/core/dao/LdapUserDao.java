package com.company.ldap.core.dao;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import com.company.ldap.core.dto.LdapUserWrapper;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.core.utils.LdapConstants;
import com.company.ldap.core.utils.LdapUserMapper;
import com.company.ldap.core.utils.LdapUserWrapperMapper;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.global.LoginException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static com.company.ldap.core.dao.LdapUserDao.NAME;

@Service(NAME)
public class LdapUserDao {

    public final static String NAME = "ldap_LdapUserDao";

    @Inject
    @Qualifier(LdapConstants.LDAP_TEMPLATE_BEAN_NAME)
    private LdapTemplate ldapTemplate;

    @Inject
    private LdapConfig ldapConfig;

    @Inject
    private Messages messages;

    //TODO: для POC метод вызывается только после успешнйо лдап аутентификации, поэтому пользователь будет всегда
    public ApplyMatchingRuleContext getLdapUserWrapper(String login) {
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(10_000)
                .countLimit(1)
                .filter(createUserBaseAndLoginFilter(login));
        LdapUserWrapper ldapUserWrapper = ldapTemplate.search(query, new LdapUserWrapperMapper(ldapConfig)).get(0);
        return new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes());
    }

    public LdapUser findLdapUserByFilter(String filter, String login) {
        LdapQuery query = LdapQueryBuilder.query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(10_000)
                .countLimit(1)
                .filter(addUserBaseAndLoginFilter(login, new HardcodedFilter(filter)));
        List<LdapUser> list = ldapTemplate.search(query, new LdapUserMapper(ldapConfig));
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    public void authenticateLdapUser(String login, String password, Locale messagesLocale) throws LoginException {
        if (!ldapTemplate.authenticate(LdapUtils.emptyLdapName(), createUserBaseAndLoginFilter(login).encode(), password)) {
            throw new LoginException(
                    messages.formatMessage(LdapUserDao.class, "LoginException.InvalidLoginOrPassword", messagesLocale, login)
            );
        }
    }


    //TODO: ldap injection
    private Filter createUserBaseAndLoginFilter(String login) {
        Filter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        if (StringUtils.isEmpty(ldapConfig.getUserBase())) {
            return ef;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(ef);
        andFilter.and(new HardcodedFilter("(" + ldapConfig.getUserBase() + ")"));

        return andFilter;
    }

    private Filter addUserBaseAndLoginFilter(String login, Filter filter) {
        Filter resultFilter = null;
        Filter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        resultFilter = ef;
        if (StringUtils.isNotEmpty(ldapConfig.getUserBase())) {
            AndFilter andFilter = new AndFilter();
            andFilter.and(ef);
            andFilter.and(new HardcodedFilter("(" + ldapConfig.getUserBase() + ")"));
            resultFilter = andFilter;
        }
        AndFilter andFilter = new AndFilter();
        andFilter.and(resultFilter);
        andFilter.and(filter);
        return andFilter;
    }
}

