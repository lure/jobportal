package ru.shubert.jobportal.preset;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * User: user
 * Date: 29.04.12 18:48
 */

@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@ContextConfiguration(classes = ru.shubert.jobportal.config.AppConfig.class)
public class TransactionalTestWithContext extends AbstractTransactionalTestNGSpringContextTests {

}
