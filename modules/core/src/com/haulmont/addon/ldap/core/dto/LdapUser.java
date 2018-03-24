package com.haulmont.addon.ldap.core.dto;

import java.util.List;

public class LdapUser {

    private String login;
    private String cn;
    private String sn;
    private String email;
    private List<String> memberOf;
    private List<String> accessGroups;
    private Boolean isDisabled;
    private String position;
    private String language;
    private String ou;

    public LdapUser() {
    }

    public LdapUser(LdapUser source) {
        this.login = source.login;
        this.cn = source.cn;
        this.sn = source.cn;
        this.email = source.email;
        this.memberOf = source.memberOf;
        this.accessGroups = source.accessGroups;
        this.isDisabled = source.isDisabled;
        this.position = source.position;
        this.language = source.language;
        this.ou = source.ou;
    }


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

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }
}
