package ru.shubert.jobportal.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.web.panel.SignInPanel;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * User: user
 * Date: 03.05.12 21:11
 */
@AuthorizeInstantiation()
public class LoginPage extends BasePage {
    private static final long serialVersionUID = 1L;

    /**
     * Construct
     */
    public LoginPage()
    {
        this(null);
    }

    /**
     * Constructor
     *
     * @param parameters
     *            The page parameters
     */
    public LoginPage(@Nullable final PageParameters parameters)
    {
        add(new SignInPanel("signInPanel"),
            new BookmarkablePageLink<>("registrationMain", RegistrationPage.class),
            new BookmarkablePageLink<>("registrationBottom", RegistrationPage.class));
    }



}
