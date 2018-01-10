package com.company.ldap.core.dto;

import java.util.List;

public class LdapUser {

    private String login;
    private String cn;
    private String sn;
    private String email;
    private List<String> memberOf;
    private List<String> accessGroups;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(List<String> memberOf) {
        this.memberOf = memberOf;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public List<String> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<String> accessGroups) {
        this.accessGroups = accessGroups;
    }
}
