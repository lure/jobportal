package ru.shubert.jobportal.model.prototype;

/**
 * Alternative role system, using finite set of string constants
 * It may be fasten, extensible and so on.
 * NOTE: unused roles can be marked only using direct code modification.
 *
 * NOTE2: spring-security would go better...
 *
 */
@SuppressWarnings({"UnusedDeclaration"})
public enum RoleEnum {
    PERSON,
    EMPLOYER,
    ADMIN;


    private static final long serialVersionUID = 7096630990899203501L;
}
