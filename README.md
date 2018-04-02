Please pay attention that the component is still being developed and not stable.

# Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Additional Information](#additional-information)
- [Features](#features)
    - [LDAP Config](#ldap-config)
    - [LDAP Matching Rules](#ldap-matching-rules)
    - [Testing LDAP Matching Rules](#testing-ldap-matching-rules)
    - [LDAP Log](#ldap-log)
- [API](#api)
- [Known Issues](#known-issues)

# Overview

The LDAP Integration CUBA component provides a readily available instrument for employing features of a directory
server, e.g. Active Directory, in any CUBA-based application.
The component is available for CUBA applications of any complexity and does not require any additional third-party 
frameworks or libraries to be installed.

The component comprises the following functionalities:

* Storing user credentials in the LDAP database;
* Using a single set of credentials in all applications having LDAP enabled;
* Configuring LDAP parameters and the directory server schema;
* Setting up rules for assigning roles and access groups to users.

# Getting Started

## Prerequisites

Before enabling the component, it is required to configure a directory server, so that it was accessible for the 
component features, and set up LDAP connection parameters.

## Installation

To add the LDAP component to your project, the following steps should be taken:

1. Open the component in CUBA Studio and invoke Run → Install app component.

2. Open your application in CUBA Studio. Click 'Edit' in the 'Project Properties' section and, after that, open the
'Advanced' tab. Check that the 'Use local maven repository' property is enabled.

3. Add a custom application component to your project via CUBA Studio.

![Adding the component in Studio](img/adding-component-in-studio.png)

* Artifact group: *com.haulmont.addon.ldap*
* Artifact name: *ldap-global*
* Version: *0.1-SNAPSHOT*

When specifying the component version, you should select the one, which is compatible with the platform version used
in your project.

| Platform Version | Component Version |
|------------------|-------------------|
| 6.8.1            | 0.1-SNAPSHOT      |


2. Before using the component as a part of your application, it is vital to configure initial values for connecting to
the LDAP server, and to set up basic attribute names for the LDAP user in the *app.properties* file.
An example of how to set up these properties is given below.

```properties
ldap.contextSourceUrl = ldap://localhost:10389
ldap.contextSourceBase = dc=example,dc=com
ldap.contextSourceUserName = uid=admin,ou=system
ldap.contextSourcePassword = secret
```

## Additional Information

You should keep in mind, that the LDAP component should be installed for each CUBA application, which is expected to use
Single Sign-On.

# Features

## LDAP Config

Once you have successfully installed the component, check that all configured property values are displayed properly 
on LDAP Config Screen (Menu: LDAP Component → LDAP Config).

![LDAP-Config-menu](img/ldap-component-menu.png)

![LDAP-Config-Screen](img/ldap-config-screen.png)

The screen comprises three sections: 'Connection settings', 'Attribute Settings' and 'Schema Settings'. The description
of each section is given below.

### LDAP Connection Settings

The 'Connection settings' section of LDAP Config Screen allows viewing and testing LDAP connection properties right from the
application UI.

![LDAP-Config-Connection](img/ldap-config-connection.png)

Clicking the 'Test Connection' button at the bottom of the screen launches connection testing. If the connection is 
successfully established, the corresponding message is displayed.

### Basic Attributes

When a user logs in using LDAP credentials for the first time, a new user entity is created in the CUBA application.
All details about the user are taken from the LDAP database (configuring these details is a part of preparation 
activities). In order to match LDAP attributes and the fields of the User entity, use the 'Attribute Settings' section of 
Load Config Screen.

![LDAP-Config-Basic-Attributes](img/ldap-config-basic-attributes.png)

### LDAP Schema

The 'Schema Settings' section allows configuring a set of rules that define what can be stored as entries in an LDAP 
directory. 

![LDAP_Schema-Settings](img/ldap-schema-settings.png)

Using the table provided in the section, it is possible to set up attributes that can be used as conditions when applying
matching rules. Clicking the 'Refresh LDAP attributes' button uploads all attributes of the specified LDAP user object class.
However, it is possible to add attributes manually by using the 'Create' button.

## LDAP Matching Rules

LDAP matching rules are special rules for configuring access rights for users. When a user logs in using LDAP credentials
for the first time, a new user entity is created in the CUBA application. Using matching rules, it is possible to set up
access groups and roles for new application users. There are four rule types intended for this purpose: custom,
default, simple and scripting. Creating and managing LDAP matching rules is available from LDAP Matching Rule Screen
(Menu: LDAP Component → LDAP Matching Rules).

![LDAP Matching Rules Screen](img/ldap-matching-rules.png)

The screen comprises the table of matching rules and the section for testing how the existing matching rules can be
applied to a particular user (see [Testing LDAP Matching Rules](#testing-ldap-matching-rules)).
Using the table, it is possible to enable/disable certain rules by ticking checkboxes in the 'Active' column. Another
important thing to remember is that rules have their order numbers, according to which they are applied. 
The default rule is always applied the last.

The description of all rule types and their peculiarities is provided in the sections below.

### Custom Rule

The LDAP component provides means to process custom rules defined programmatically. These rules can be created only by 
adding new classes to the source code of your application. Custom rules can be viewed from the application UI, however,
they cannot be configured or amended there.

One of the advantages of custom rules is that they allow specifying additional conditions not related to LDAP attributes.
The example of a custom rule is provided below.

```java
@LdapMatchingRule(description = "Test Custom Rule")
public class TestCustomLdapRule implements CustomLdapMatchingRule {

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private LdapConfig ldapConfig;

    @Inject
    private CubaUserDao cubaUserDao;

    @Override
    public boolean applyCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext) {
        if (applyMatchingRuleContext.getLdapUser().getLogin().equalsIgnoreCase("barts")) {
            User admin = cubaUserDao.getCubaUserByLogin("admin");
            applyMatchingRuleContext.getCurrentRoles().add(admin.getUserRoles().get(0).getRole());
            applyMatchingRuleContext.setCurrentGroup(admin.getGroup());
            applyMatchingRuleContext.getAppliedRules().add(new CustomLdapMatchingRuleWrapper(this));
        }
        return false;
    }
}
```

### Default Rule

When launching your application with the component installed for the first time, the default rule is automatically 
created in the system.

The default rule is used if none of other rules were applied, e.g. conditions for applying existing rules were not met.
That is why it contains the 'LAST' value in the 'Order' field.

The default rule can be amended by clicking the 'Edit Default Rule' button. All fields and settings present in Default
Matching Rule Editor are described in the section below.

#### Default Matching Rule Editor

![Default Rule Editor](img/default-rule-editor.png)

* *Access group*: defines an access group to be assigned to a user, if the default rule is applied.
* *Terminal rule*: if checked, then other rules cannot be applied, if the default rule was used.
* *Override existing roles*: if checked, then all roles that were previously assigned to a user are removed and the ones
specified in the 'Roles' section are used instead.
* *Override existing access group*: if checked, then an access group that was previously assigned to a user is removed
and the group specified in the 'Access group' field is used instead.
* *Description*: a short description of the default rule.

The 'Roles' table allows creating a set of roles, which are to be assigned to a user, if the default rule is used.

### Simple Rule

Simple rules allow granting access rights (an access group and CUBA roles) to users, if particular conditions are met.
To create a simple rule, click the 'Create simple rule' button. 
All fields and settings present in Simple Matching Rule Editor are described in the section below.

#### Simple Matching Rule Editor

![Simple Rule Editor](img/simple-rule-editor.png)

Simple Matching Rule Editor comprises three sections for configuring a simple matching rule:

1. __General details and settings__. The fields included in the section are similar to the ones described in [Default Matching
Rule Editor](#default-matching-rule-editor).
2. __Conditions__. The section enables to add conditions, which have to be met for successful rule application. Clicking
the 'Create' button opens Simple Rule Condition Editor.

![Simple Rule Condition Editor](img/simple-rule-condition-editor.png)

The editor contains the following fields:

* *Attribute*: defines an LDAP attribute, which will be checked before applying a current simple rule.

**Note:** Before creating conditions it is required to add them to the existing LDAP Schema (for more details, please
refer to [LDAP Schema](#ldap-schema)).

* *Attribute Value*: defines a value of the selected attribute. The rule will be applied to those user entities, which
have the specified value of the selected attribute.

3. __Roles__. The section allows adding user roles, which will be assigned to a user in case of successful rule application.

### Scripting Rule

Using scripting rules it is possible to specify a Groovy script with a set of conditions to be met to grant a user
access rights. You can create a new scripting rule by clicking the 'Create Scripting Rule' button.

#### Scripting Matching Rule Editor



## Testing LDAP Matching Rules

## LDAP Log
