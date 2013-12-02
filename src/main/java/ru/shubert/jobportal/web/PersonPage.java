package ru.shubert.jobportal.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.person.Education;
import ru.shubert.jobportal.model.person.JobExperience;
import ru.shubert.jobportal.model.person.Person;
import ru.shubert.jobportal.service.MongoService;
import ru.shubert.jobportal.service.UserService;
import ru.shubert.jobportal.web.component.CancelButton;
import ru.shubert.jobportal.web.component.EmailPropertyLink;
import ru.shubert.jobportal.web.component.HighlightUtils;
import ru.shubert.jobportal.web.component.SaveButton;
import ru.shubert.jobportal.web.panel.EducationPanel;
import ru.shubert.jobportal.web.panel.JobExperiencePanel;
import ru.shubert.jobportal.web.panel.UserPanel;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * User: user
 * Date: 30.04.12 17:47
 */

@AuthorizeInstantiation({"PERSON", "ADMIN"})
public class PersonPage extends BasePage {
    //private static final Logger LOGGER = LoggerFactory.getLogger(PersonPage.class);

    @SpringBean(name="baseService")
    private MongoService personService;

    @SpringBean
    private UserService userService;

    public PersonPage() {
        super();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final User user = userService.findOne(JobPortalSession.get().getUser().getId(), User.class);

        final IModel<Person> formModel = new CompoundPropertyModel<>(user.getPerson());
        final CompoundPropertyModel<User> userModel = new CompoundPropertyModel<>(user);

        // we nee that container to provide meaningfull feedback filter
        WebMarkupContainer feedbackContainer = new WebMarkupContainer("feedbackContainer");
        feedbackContainer.setRenderBodyOnly(true);

        Form<Person> form = new Form<Person>("form", formModel) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                Person person = getModelObject();

                for (Education e: person.getEducation())
                    personService.save(e);

                for (JobExperience j: person.getExperiences())
                    personService.save(j);

                personService.save(person);
                userService.save(user);
                getSession().info(getString("status.save", getModel()));
                setResponsePage(HomePage.class);
            }
        };


        // EDIT FUCNTIONALITY
        TextField firstname = newRequiredTextField("firstname", Person.LONG_STRING);
        firstname.setDefaultModel(userModel.bind("firstName"));
        TextField middleName = newRequiredTextField("middlename", Person.LONG_STRING);
        middleName.setDefaultModel(userModel.bind("middleName"));
        TextField lastname = newRequiredTextField("lastname", Person.LONG_STRING);
        lastname.setDefaultModel(userModel.bind("lastName"));

        TextField position = newRequiredTextField("position", Person.LONG_STRING);
        TextField address = newRequiredTextField("address", 2048);
        TextField salary = new TextField("salary");
        salary.setRequired(true);
        salary.setType(Integer.class);

        TextArea<String> description = new TextArea<>("description");
        description.add(new StringValidator.LengthBetweenValidator(100, 2048));
        description.setRequired(true);
        description.setLabel(Model.of("description"));
        CheckBox available = new CheckBox("available");

        feedbackContainer.add(firstname, middleName, lastname,
                position, address, salary, dropDownForEnum("currency", Currency.class, this),
                description, available);
        form.add(
                feedbackContainer,
                new EducationPanel("education"), new JobExperiencePanel("experiences"),
                new SaveButton(), new CancelButton()
        );

        HighlightUtils.highlightForm(form);

        add(form, new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(feedbackContainer)));

        // VIEW FUNCTIONALITY
        WebMarkupContainer viewPanel = new WebMarkupContainer("view-panel");
        viewPanel.setDefaultModel(formModel);
        viewPanel.add(
                new Label("firstname", new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return String.format("%s %s %s", user.getFirstName(), user.getMiddleName(), user.getLastName());
                    }
                }).setRenderBodyOnly(true),
                new EmailPropertyLink("login", userModel),
                new Label("address").setRenderBodyOnly(true),
                new Label("description"),
                new Label("position"),
                new Label("salary").setRenderBodyOnly(true),
                new Label("currency", getEnumerationModel(formModel, "currency", PersonPage.this)).setRenderBodyOnly(true)
        );

        PropertyListView<Education> education = new PropertyListView<Education>("education") {
            @Override
            protected void populateItem(ListItem<Education> item) {
                item.add(
                        new Label("grade", getEnumerationModel(item.getModel(), "grade", PersonPage.this)).setRenderBodyOnly(true),
                        new Label("speciality").setRenderBodyOnly(true),
                        new Label("place").setRenderBodyOnly(true),
                        new Label("end").setRenderBodyOnly(true)
                );
            }
        };
        PropertyListView<JobExperience> experience = new PropertyListView<JobExperience>("experiences") {
            @Override
            protected void populateItem(final ListItem<JobExperience> item) {
                item.add(
                        new Label("company").setRenderBodyOnly(true),
                        new Label("position").setRenderBodyOnly(true),
                        new Label("salary").setRenderBodyOnly(true),
                        new Label("currency", getEnumerationModel(item.getModel(), "currency", PersonPage.this)).setRenderBodyOnly(true),
                        new Label("period", new AbstractReadOnlyModel<String>() {
                            @Override
                            public String getObject() {
                                return item.getModelObject().getStart() + " - " + item.getModelObject().getEnd();
                            }
                        }).setRenderBodyOnly(true)

                );
            }
        };
        viewPanel.add(education, experience);
        add(viewPanel, new UserPanel(), new MenuMyVacancy(), new MenuAllResume(), new MenuAllVacancy());
    }
}
