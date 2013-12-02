package ru.shubert.jobportal.web.panel;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.web.JobPortalSession;
import ru.shubert.jobportal.web.LoginPage;

public class UserPanel extends Panel {

    public UserPanel() {
        super("userPanel");

        IModel<User> userModel = ((JobPortalSession) Session.get()).getUserModel();
        Link signOut = new Link<Page>("signout") {
            @Override
            public boolean isVisible() {
                return JobPortalSession.get().isSignedIn();
            }

            @Override
            public void onClick() {
                JobPortalSession.get().invalidate();
                throw new RestartResponseAtInterceptPageException(LoginPage.class);
            }
        };
        Label login = new Label("login", new PropertyModel(userModel, "login"));
        login.setRenderBodyOnly(true);
        add(signOut, login);
    }
}