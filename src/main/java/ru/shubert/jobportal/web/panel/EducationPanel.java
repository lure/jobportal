package ru.shubert.jobportal.web.panel;

import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.model.person.Education;
import ru.shubert.jobportal.model.person.EducationGrade;
import ru.shubert.jobportal.web.component.HighlightUtils;
import ru.shubert.jobportal.web.repeater.EditableListItem;

import java.util.Collection;

import static ru.shubert.jobportal.web.proto.BasePage.dropDownForEnum;
import static ru.shubert.jobportal.web.proto.BasePage.newRequiredTextField;


public class EducationPanel extends AbstractEditablePanel<Education> {
    /**
     * Html markup id used to determine property name in owner's compound model
     *
     * @param id html id
     */
    public EducationPanel(final String id) {
        super(id);
    }

    /**
     * As opposed to previous constructor this one receives model explicitly and this is the only difference
     *
     * @param id         html markup id
     * @param model explicit model
     */
    @SuppressWarnings("UnusedDeclaration")
    public EducationPanel(final String id, final @Nullable IModel<Collection<Education>> model) {
        super(id, model);
    }

    @Override
    protected void onPopulatePanel(final EditableListItem<Education> listItem) {
        DropDownChoice<EducationGrade> education = dropDownForEnum("grade", EducationGrade.class, this);
        TextField<Integer> end = new TextField<>("end", Integer.class);
        end.setRequired(true);
        TextField<String> place = newRequiredTextField("place", Education.LONG_STRING);
        TextField<String> speciality = newRequiredTextField("speciality", Education.LONG_STRING);

        HighlightUtils.addHighlightBehaviour(education, end, place, speciality);

        listItem.add(education,
                    end,
                    place,
                    speciality,
                    newRemoveItemLink("remove-panel", listItem),
                    new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(listItem))
        );
    }

    @Override
    protected Education newObject() {
        return new Education();
    }
}
