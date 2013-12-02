package ru.shubert.jobportal.web.panel;


import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import ru.shubert.jobportal.web.RegistrationPage;
import ru.shubert.jobportal.web.component.HighlightUtils;


/**
 * Reusable user sign in panel with username and password as well as support for cookie persistence
 * of the both. When the SignInPanel's form is submitted, the method signIn(String, String) is
 * called, passing the username and password submitted. The signIn() method should authenticate the
 * user's session. The default implementation calls AuthenticatedWebSession.get().signIn().
 * <p/>
 * But the main reason it exists is just to make example of use wicket panels.
 */
public class SignInPanel extends Panel {
    private static final long serialVersionUID = 1L;
    // Field for password.
    private PasswordTextField password;
    // Field for user name.
    private TextField<String> username;

    // Sign in form
    public final class SignInForm extends StatelessForm<Void> {
        private static final long serialVersionUID = 1L;
        //El-cheapo model for form.
        private final ValueMap properties = new ValueMap();

        /**
         * Constructor.
         *
         * @param id id of the form component
         */
        public SignInForm(final String id) {
            super(id);
            // Attach textfield components that edit properties map
            // in lieu of a formal beans model
            username = new TextField<>("username", new PropertyModel<String>(properties, "username"), String.class);
            username.setRequired(true);
            password = new PasswordTextField("password", new PropertyModel<String>(properties, "password"));
            add(username, password, new BookmarkablePageLink<>("registration", RegistrationPage.class));
            HighlightUtils.addHighlightBehaviour(username, password);
        }

        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        public final void onSubmit() {
            if (signIn(getUsername(), getPassword())) {
                // If login has been called because the user was not yet
                // logged in, than continue to the original destination,
                // otherwise to the Home page
                if (!continueToOriginalDestination()) {
                    setResponsePage(getApplication().getHomePage());
                }
            } else {
                error(getLocalizer().getString("signInFailed", this, "Sign in failed"));
            }
        }
    }


    /**
     * @param id See Component constructor
     * @see org.apache.wicket.Component#Component(String)
     */
    public SignInPanel(final String id) {
        super(id);

        // Add sign-in form to page, passing feedback panel as
        // validation error handler
        add(new SignInForm("signInForm"), new FeedbackPanel("feedback"));
    }

    /**
     * Convenience method to access the password.
     *
     * @return The password
     */
    public String getPassword() {
        return password.getInput();
    }

    /**
     * Convenience method to access the username.
     *
     * @return The user name
     */
    public String getUsername() {
        return username.getDefaultModelObjectAsString();
    }

    /**
     * Sign in user if possible.
     *
     * @param username The username
     * @param password The password
     * @return True if signin was successful
     */
    public boolean signIn(String username, String password) {
        return AuthenticatedWebSession.get().signIn(username, password);
    }

}
