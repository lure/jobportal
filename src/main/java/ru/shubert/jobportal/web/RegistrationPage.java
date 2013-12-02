package ru.shubert.jobportal.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.prototype.RoleEnum;
import ru.shubert.jobportal.service.IAccountService;
import ru.shubert.jobportal.web.component.CancelButton;
import ru.shubert.jobportal.web.component.HighlightUtils;
import ru.shubert.jobportal.web.component.SaveButton;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * User: user
 * Date: 01.05.12 13:54
 */
@AuthorizeInstantiation()
public class RegistrationPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationPage.class);
    @SpringBean
    private IAccountService service;

    private PasswordTextField password;

    //1 digit, 1 lower, 1 upper, 1 symbol "@#$%", from 6 to 20
    //private final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";


    public RegistrationPage() {
        super();
    }


    @Override
    final protected void onInitialize() {
        super.onInitialize();

        IModel<User> formModel = new CompoundPropertyModel<>(new User());
        final Form<User> form = new Form<User>("form", formModel) {
            @Override
            protected void onSubmit() {
                super.onSubmit();

                User user = getModelObject();

                if (user.getId() == null) {
                    user.setPassword(password.getModelObject());
                    user.setLoginToken(service.getTokenGenerator().generate());
                    user.initRole();
                }

                try {
                    service.save(user);
                    LOGGER.info("Saved new user {}", user);
                    getSession().info(getString("status.save", getModel()));
                    JobPortalSession.get().forceAuthenticate(user);
                    setResponsePage(HomePage.class);
                } catch (Exception e) {
                    LOGGER.error("Saving user {} with {}", user, e.getLocalizedMessage());
                    this.fatal(getString("status.error") + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        };

        TextField<String> login = newRequiredEmailField("login", User.SHORT_STRING);
        TextField<String> firstName = newRequiredTextField("firstName", User.SHORT_STRING);
        TextField<String> middleName = newRequiredTextField("middleName", User.SHORT_STRING);
        TextField<String> lastName = newRequiredTextField("lastName", User.SHORT_STRING);
        //DropDownChoice<RoleEnum> role = dropDownForEnum("role", RoleEnum.class, this);

        RadioGroup<RoleEnum> role = new RadioGroup<>("role");
        role.add(new Radio<>("PERSON", Model.of(RoleEnum.PERSON)));
        role.add(new Radio<>("EMPLOYER", Model.of(RoleEnum.EMPLOYER)));



        // Passwords. if id == null, they are required. Else - null-valued password fields leads to keep password
        // Independent model lets us assign new password only if it really exists, otherwise keep old password
        password = new PasswordTextField("password", Model.of(""));
        //password.add(new PatternValidator(PASSWORD_PATTERN));
        PasswordTextField passwordConfirmation = new PasswordTextField("password-confirmation", Model.of(""));
        if (formModel.getObject().getId() != null) {
            passwordConfirmation.setRequired(false).add(StringValidator.lengthBetween(User.SMALL_STRING, User.SHORT_STRING));
            password.setRequired(false).add(StringValidator.lengthBetween(User.SMALL_STRING, User.SHORT_STRING));
        }

        // VALIDATION
        form.add(new EqualPasswordInputValidator(password, passwordConfirmation));
        //getFeedbackPanel().setFilter(new ComponentFeedbackMessageFilter(form));

        // Component rendering
        form.add(
                role, //feedbackFor(role),
                login, //feedbackFor(login),
                password, //feedbackFor(password),
                passwordConfirmation, //feedbackFor(passwordConfirmation),
                firstName, //feedbackFor(firstName),
                middleName, //feedbackFor(middleName),
                lastName, //feedbackFor(lastName),

                new SaveButton(), new CancelButton());
        HighlightUtils.highlightForm(form);
        add(form, new FeedbackPanel("feedback"));
    }
}
