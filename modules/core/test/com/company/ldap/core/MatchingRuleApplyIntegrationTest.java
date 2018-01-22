package com.company.ldap.core;

import com.company.ldap.LdapTestContainer;
import com.company.ldap.core.rule.MatchingRuleApplierInitializer;
import com.company.ldap.core.rule.appliers.MatchingRuleChain;
import com.company.ldap.entity.FixedMatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.company.ldap.entity.SimpleMatchingRule;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import org.junit.*;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MatchingRuleApplyIntegrationTest {
    @ClassRule
    public static LdapTestContainer cont = LdapTestContainer.Common.INSTANCE;

    private Persistence persistence;
    private Metadata metadata;
    private ApplicationContext applicationContext;
    private final String login = "joes";
    private final String password = "joespassword";
    private FixedMatchingRule fixedMatchingRule;
    private SimpleMatchingRule simpleMatchingRule1;
    private SimpleMatchingRule simpleMatchingRule2;
    private Role role1;
    private Role role2;
    private Role role3;
    private Group group1;
    private Group group2;

    @Before
    public void setUp() throws Exception {
        persistence = cont.persistence();
        applicationContext = cont.getSpringAppContext();
        metadata = cont.metadata();

        cont.persistence().createTransaction().execute(entityManager -> {

            role1 = metadata.create(Role.class);
            role1.setName("ROLE1");

            role2 = metadata.create(Role.class);
            role2.setName("ROLE2");

            role3 = metadata.create(Role.class);
            role3.setName("ROLE3");

            group1 = metadata.create(Group.class);
            group1.setName("GROUP1");

            group2 = metadata.create(Group.class);
            group2.setName("GROUP2");


            simpleMatchingRule1 = metadata.create(SimpleMatchingRule.class);
            simpleMatchingRule1.setAccessGroup(group1);
            simpleMatchingRule1.setIsOverrideExistingAccessGroup(true);
            simpleMatchingRule1.setRuleType(MatchingRuleType.SIMPLE);
            simpleMatchingRule1.getRoles().add(role1);
            simpleMatchingRule1.setLdapCondition("(sAMAccountName=" + login + ")");

            simpleMatchingRule2 = metadata.create(SimpleMatchingRule.class);
            simpleMatchingRule2.setAccessGroup(group2);
            simpleMatchingRule2.setRuleType(MatchingRuleType.SIMPLE);
            simpleMatchingRule2.getRoles().add(role2);
            simpleMatchingRule2.setLdapCondition("(sAMAccountName=fake)");

            fixedMatchingRule = metadata.create(FixedMatchingRule.class);
            fixedMatchingRule.setAccessGroup(group2);
            fixedMatchingRule.setRuleType(MatchingRuleType.FIXED);
            fixedMatchingRule.getRoles().add(role3);


            entityManager.persist(role1);
            entityManager.persist(role2);
            entityManager.persist(role3);

            entityManager.persist(group1);
            entityManager.persist(group2);

            entityManager.persist(simpleMatchingRule1);
            entityManager.persist(simpleMatchingRule2);
            entityManager.persist(fixedMatchingRule);
        });

    }

    @After
    public void tearDown() throws Exception {
        cont.persistence().createTransaction().execute(entityManager -> {
            entityManager.remove(role1);
            entityManager.remove(role2);
            entityManager.remove(role3);

            entityManager.remove(group1);
            entityManager.remove(group2);

            entityManager.remove(simpleMatchingRule1);
            entityManager.remove(simpleMatchingRule2);
            entityManager.remove(fixedMatchingRule);
        });
    }

    @Test
    public void testMatchingRulesOrder() {
        MatchingRuleApplierInitializer matchingRuleApplierInitializer = (MatchingRuleApplierInitializer) applicationContext.getBean(MatchingRuleApplierInitializer.NAME);
        MatchingRuleChain matchingRuleChain = matchingRuleApplierInitializer.getMatchingRuleChain();
        List<MatchingRuleType> typesByProcessOrderAsc = Arrays.stream(MatchingRuleType.values()).sorted(Comparator.comparing(MatchingRuleType::getProcessOrder))
                .collect(Collectors.toList());

        for (MatchingRuleType mrt : typesByProcessOrderAsc) {
            Assert.assertEquals(matchingRuleChain.getMatchingRuleType(), mrt);

            if (matchingRuleChain.getNext() == null) {
                Assert.assertEquals(MatchingRuleType.FIXED, mrt);
            }

            matchingRuleChain = matchingRuleChain.getNext();
        }

    }
}
