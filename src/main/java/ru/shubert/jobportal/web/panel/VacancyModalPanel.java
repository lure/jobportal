package ru.shubert.jobportal.web.panel;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.employer.Vacancy;
import ru.shubert.jobportal.model.person.EducationGrade;
import ru.shubert.jobportal.web.component.ComplexModalWindow;
import ru.shubert.jobportal.web.component.HighlightUtils;

import static ru.shubert.jobportal.web.proto.BasePage.dropDownForEnum;
import static ru.shubert.jobportal.web.proto.BasePage.newRequiredTextField;

public class VacancyModalPanel extends Panel {

    private ComplexModalWindow<Vacancy> window;

    public VacancyModalPanel(String id, ComplexModalWindow<Vacancy> window) {
        super(id);
        this.window = window;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        Form<Vacancy> form = new Form<Vacancy>("form", new CompoundPropertyModel<>(window.getResultObject())){
            @Override
            protected void onError() {
                AjaxRequestTarget.get().add(this, feedbackPanel);
            }
        };

        TextArea<String> description = new TextArea<>("description");
        description.setRequired(true);
        description.add(new StringValidator.LengthBetweenValidator(100, 2048));

        TextField salary = new TextField("salary");
        salary.setRequired(true);
        salary.setType(Integer.class);

        AjaxButton saveButton = new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                window.setConfirmed(true);
                window.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) { }
        };

        AjaxButton cancelButton = new AjaxButton("cancel") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                window.setConfirmed(false);
                window.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) { }
        };
        cancelButton.setDefaultFormProcessing(false);

        form.add(newRequiredTextField("position", Vacancy.LONG_STRING),
                dropDownForEnum("education", EducationGrade.class, getPage()),
                salary, dropDownForEnum("currency", Currency.class, getPage()),
                description, saveButton, cancelButton
        );
        HighlightUtils.highlightForm(form);
        feedbackPanel.setFilter(new ContainerFeedbackMessageFilter(form));
        add(form, feedbackPanel);
    }

}
