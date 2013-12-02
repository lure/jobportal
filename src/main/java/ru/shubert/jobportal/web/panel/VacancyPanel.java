package ru.shubert.jobportal.web.panel;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import ru.shubert.jobportal.web.ShowItemPage;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * Renders single vacancy in a view mode
 */
@AuthorizeInstantiation({"ADMIN","PERSON"})
public class VacancyPanel extends Panel {
    public VacancyPanel(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("position"),
                new Label("education"),
                new Label("salary"),
                new Label("currency", BasePage.getEnumerationModel(getDefaultModel(), "currency", getPage())),
                new Label("description"),
                new Label("employer.name"),
                new SmartLinkLabel("employer.url"),
                new Label("employer.description"));
    }
}
