package ru.shubert.jobportal.web.panel;


import org.apache.wicket.Component;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.person.Education;
import ru.shubert.jobportal.model.person.JobExperience;
import ru.shubert.jobportal.web.component.HighlightUtils;
import ru.shubert.jobportal.web.repeater.EditableListItem;

import java.util.Collection;
import java.util.Date;

import static ru.shubert.jobportal.web.proto.BasePage.dropDownForEnum;
import static ru.shubert.jobportal.web.proto.BasePage.newRequiredTextField;

public class JobExperiencePanel extends AbstractEditablePanel<JobExperience> {
    /**
     * Html markup id used to determine property name in owner's compound model
     *
     * @param id html id
     */
    public JobExperiencePanel(final String id) {
        super(id);
    }

    /**
     * As opposed to previous constructor this one receives model explicitly and this is the only difference
     *
     * @param id    html markup id
     * @param model explicit model
     */
    @SuppressWarnings("UnusedDeclaration")
    public JobExperiencePanel(final String id, final @Nullable IModel<Collection<JobExperience>> model) {
        super(id, model);
    }

    @Override
    protected void onPopulatePanel(final EditableListItem<JobExperience> listItem) {
        TextField<String> company = newRequiredTextField("company", Education.LONG_STRING);
        TextField<String> position = newRequiredTextField("position", Education.LONG_STRING);

        final DateTextField start = DateTextField.forDatePattern("start", "dd.MM.yyyy");
        final DateTextField end = DateTextField.forDatePattern("end", "dd.MM.yyyy");
        //setDatePickers(end, start);  TODO раскомментировать, если с jquery не получится!
        end.add(new IValidator<Date>() {
            @Override
            public void validate(IValidatable<Date> iValidatable) {
                if (end.getConvertedInput() == null && start.getConvertedInput() == null) {
                    return;
                }

                if (end.getConvertedInput() != null && start.getConvertedInput() == null) {
                    error("Дата увольнения троебует наличия даты начала работы!");
                    return;
                }

                if (end.getConvertedInput() != null && start.getConvertedInput().compareTo(end.getConvertedInput()) == 1) {
                    error("Дата увольнения меньше даты поступления на работу");
                }
            }
        });

        TextField<Integer> salary = new TextField<>("salary", Integer.class);
        salary.setRequired(true);
        DropDownChoice<Currency> currency = dropDownForEnum("currency", Currency.class, this);
        HighlightUtils.addHighlightBehaviour(company, position, start, end, salary, currency);

        listItem.add(company, position, start, end, currency, salary,
                newRemoveItemLink("remove-panel", listItem),
                new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(listItem))
        );
    }

    // Uncomment this if jquery fails to provide date pickers.
    @SuppressWarnings("UnusedDeclaration")
    void setDatePickers(Component... components){
        DatePicker datePicker = new DatePicker() {
            @Override
            protected String getAdditionalJavaScript() {
                return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
            }
        };
        datePicker.setShowOnFieldClick(true);
        datePicker.setAutoHide(true);

        for (Component c : components)
            c.add(datePicker);
    }

    @Override
    protected JobExperience newObject() {
        return new JobExperience();
    }


}
