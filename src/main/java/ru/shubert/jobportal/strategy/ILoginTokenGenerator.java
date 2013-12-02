package ru.shubert.jobportal.strategy;

/**
 * Login token generator for http sessions.
 * @see ru.shubert.jobportal.web.JobPortalSession
 *
 */
public interface ILoginTokenGenerator {

    /**
     * implements custom token generation strategy
     *
     * @return string representation of generated token
     */
    String generate();
}
