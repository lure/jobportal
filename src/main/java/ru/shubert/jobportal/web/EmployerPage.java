package ru.shubert.jobportal.web;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.bson.types.ObjectId;
import ru.shubert.jobportal.model.employer.Employer;
import ru.shubert.jobportal.model.employer.Vacancy;
import ru.shubert.jobportal.service.IService;
import ru.shubert.jobportal.web.component.*;
import ru.shubert.jobportal.web.panel.UserPanel;
import ru.shubert.jobportal.web.panel.VacancyModalPanel;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * User: user
 * Date: 02.05.12 21:07
 */
@AuthorizeInstantiation({"EMPLOYER", "ADMIN"})
public class EmployerPage extends BasePage {
    // modal window answer, chosen by user
    private ConfirmationAnswer confirmationAnswer = new ConfirmationAnswer();
    // vacancy list feedback
    private Component vFeedbackPanel;
    //  Item id, marked for deletion
    private Vacancy itemToDelete;
    // container to be reloaded on ajax operations. <tr> tag doesn't work for it's nested nature
    private WebMarkupContainer body;
    // multitarget ajax window. Here we need it to render vacancy
    private ComplexModalWindow<Vacancy> vacancyWindow;


    @SpringBean(name="baseService")
    protected IService employerService;


    @SuppressWarnings({"UnusedDeclaration"})
    public EmployerPage() {
        super();
    }


    @Override
    public void onInitialize() {
        super.onInitialize();

        ObjectId empId = JobPortalSession.get().getUser().getEmployer().getId();
        final IModel<Employer> formModel = new CompoundPropertyModel<>(employerService.findOne(empId, Employer.class));
        setDefaultModel(formModel);
        Form<Employer> form = new Form<Employer>("form", formModel) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                for(Vacancy v :getModelObject().getVacancies()){
                    employerService.save(v);
                }
                employerService.save(getModelObject());
                getSession().info(getString("status.save", getModel()));
                setResponsePage(HomePage.class);
            }
        };

        TextField name = newRequiredTextField("name", Employer.LONG_STRING);
        TextField url = newRequiredTextField("url", Employer.LONG_STRING);
        TextArea<String> description = new TextArea<>("description");
        description.setRequired(true);
        description.add(new StringValidator.LengthBetweenValidator(100, 2048));

        form.add(name, url, description, new SaveButton(), new CancelButton());
        HighlightUtils.highlightForm(form);
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setFilter(new ComponentFeedbackMessageFilter(form));
        add(form, feedback);

        // L I S T  [ queryes the PAGE (!!) deafault model
        final ConfirmationWindow confirmationWindow = newConfirmationWindow();
        vFeedbackPanel = new FeedbackPanel("vFeedbackPanel").setOutputMarkupId(true);
        body = new WebMarkupContainer("viewContainer");
        body.setOutputMarkupId(true);
        body.add(newVacancyList(confirmationWindow));

        // add\edit vacancy: show modal window with vacancy panel whos model is set to null or existing model
        AjaxLink<Vacancy> addVacancyLink = new AjaxLink<Vacancy>("add-vacancy-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Vacancy v = new Vacancy();
                vacancyWindow.setResultObject(v);
                VacancyModalPanel panel = new VacancyModalPanel(vacancyWindow.getContentId(), vacancyWindow);
                vacancyWindow.setContent(panel);
                vacancyWindow.show(target);
            }
        };

        vacancyWindow = new ComplexModalWindow<Vacancy>("complex-modal-window") {

            @Override
            public void onClose(AjaxRequestTarget target) {
                if (isConfirmed()) {
                    Vacancy v = vacancyWindow.getResultObject();
                    Employer e = formModel.getObject();
                    v.setEmployer(e);
                    if (!v.isPersisted()) {
                        e.getVacancies().add(v);
                    }
                    employerService.save(v);
                    target.add(vFeedbackPanel, body);
                }
            }
        };

        add(addVacancyLink, body, confirmationWindow, vacancyWindow, vFeedbackPanel, new UserPanel(),
                new MenuMyResume(), new MenuAllResume(), new MenuAllVacancy());
    }

    /**
     * builds an repeater for vacancy list. First of all it is property list so it looks for any compound model.
     * That's why wee assign pagedefault model to form model
     */
    private PropertyListView<Vacancy> newVacancyList(final ConfirmationWindow confirmationWindow) {
        return new PropertyListView<Vacancy>("vacancies") {
            @Override
            protected void populateItem(final ListItem<Vacancy> item) {
                item.add(new Label("position"),
                        new Label("salary").setRenderBodyOnly(true),
                        new Label("currency", getEnumerationModel(item.getModel(),
                                "currency",
                                EmployerPage.this)).setRenderBodyOnly(true),
                        new AjaxLink<Vacancy>("edit") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                vacancyWindow.setResultObject(item.getModelObject());
                                VacancyModalPanel panel = new VacancyModalPanel(vacancyWindow.getContentId(), vacancyWindow);
                                vacancyWindow.setContent(panel);
                                vacancyWindow.show(target);
                            }
                        },
                        new AjaxLink<Vacancy>("delete") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                itemToDelete = item.getModelObject();
                                confirmationAnswer.setConfirmed(false);
                                confirmationWindow.setMessage(getString("delete-vacancy", item.getModel()));
                                confirmationWindow.show(target);
                            }
                        },
                        new AjaxLink<Vacancy>("search") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                            }
                        }
                );
            }
        };
    }

    /**
     * Creates and returns  Ajax confirmation window for fast deletions.
     * On "yes" removes implicitly selected item and reloads view
     *
     * @return built Ajax Confirmation window
     */
    private ConfirmationWindow newConfirmationWindow() {
        ConfirmationWindow cw = new ConfirmationWindow("entity-confirmation", confirmationAnswer);

        cw.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                if (confirmationAnswer.isConfirmed()) {
                    if (itemToDelete != null) {
                        employerService.delete(itemToDelete);
                        ((Employer) getDefaultModelObject()).getVacancies().remove(itemToDelete);
                    }
                    target.add(body);
                }
                itemToDelete = null;
                target.add(vFeedbackPanel);
            }
        });
        return cw;
    }
}
