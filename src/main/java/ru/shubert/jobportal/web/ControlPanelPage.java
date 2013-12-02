package ru.shubert.jobportal.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.service.UserService;
import ru.shubert.jobportal.web.component.HighlightUtils;
import ru.shubert.jobportal.web.component.SaveButton;
import ru.shubert.jobportal.web.panel.UserPanel;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * User: user
 * Date: 30.04.12 17:48
 */
@AuthorizeInstantiation({"ADMIN", "PERSON", "EMPLOYER"})
public class ControlPanelPage extends BasePage {
    @SpringBean
    private UserService service;

    private PasswordTextField password;

    public ControlPanelPage() {
        super();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final IModel<User> model = new CompoundPropertyModel<>(JobPortalSession.get().getUser());
        Form<User> form = new Form<User>("form", model) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                service.save(getModelObject());
                JobPortalSession.get().getUserModel().detach();
                getSession().info(getString("status.save", getModel()));
                setResponsePage(HomePage.class);
            }
        };

        TextField<String> login = newRequiredEmailField("login", User.LONG_STRING);
        TextField<String> firstName = newRequiredTextField("firstName", User.SHORT_STRING);
        TextField<String> middleName = newRequiredTextField("middleName", User.SHORT_STRING);
        TextField<String> lastName = newRequiredTextField("lastName", User.SHORT_STRING);
        form.add(new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(form)),
                login,
                firstName,
                lastName,
                middleName,
                new SaveButton());

        // P A S S W O R D S
        Form<User> formPasswords = new Form<User>("form-password", model) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                User user = getModelObject();
                user.setPassword(password.getModelObject());
                service.save(user);
                getSession().info(getString("status.save", getModel()));
                setResponsePage(HomePage.class);
            }
        };
        // Passwords. if id == null, they are required. Else - null-valued password fields leads to keep password
        // Independent model lets us assign new password only if it really exists, otherwise keep old password
        password = new PasswordTextField("password", Model.of(""));
        PasswordTextField passwordConfirmation = new PasswordTextField("password-confirmation", Model.of(""));
        passwordConfirmation.setRequired(true).add(StringValidator.lengthBetween(User.SMALL_STRING, User.SHORT_STRING));
        password.setRequired(true).add(StringValidator.lengthBetween(User.SMALL_STRING, User.SHORT_STRING));

        // VALIDATION
        formPasswords.add(new EqualPasswordInputValidator(password, passwordConfirmation));
        PasswordTextField passwordCheck = new PasswordTextField("password-check", Model.of(""));
        passwordCheck.add(new AbstractValidator<String>() {
            @Override
            protected void onValidate(IValidatable<String> validatable) {
                final String value = validatable.getValue();
                if (!value.equals(model.getObject().getPassword())) {
                    error(validatable);
                }
            }

            @Override
            protected String resourceKey() {
                return "password.check.failed";
            }
        });
        formPasswords.add(
                new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(formPasswords)),
                password,
                passwordConfirmation,
                passwordCheck,
                new SaveButton());

        HighlightUtils.highlightForm(form);
        HighlightUtils.highlightForm(formPasswords);


        add(form, formPasswords, new UserPanel(), new MenuMyResume(), new MenuMyVacancy(), new MenuAllResume(), new MenuAllVacancy());
    }
}
