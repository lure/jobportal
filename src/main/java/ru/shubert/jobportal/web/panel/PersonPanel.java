package ru.shubert.jobportal.web.panel;


import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import ru.shubert.jobportal.model.person.Education;
import ru.shubert.jobportal.model.person.JobExperience;

import static ru.shubert.jobportal.web.proto.BasePage.getEnumerationModel;

@AuthorizeInstantiation({"ADMIN","EMPLOYER"})
public class PersonPanel extends Panel {
    public PersonPanel(String id, final IModel<?> model) {
        super(id, model);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(
                new Label("user.firstName"),
                new Label("user.middleName"),
                new Label("user.lastName"),
                new SmartLinkLabel("user.login"),
                new Label("address").setRenderBodyOnly(true),
                new Label("description"),
                new Label("position"),
                new Label("salary").setRenderBodyOnly(true),
                new Label("currency", getEnumerationModel(getDefaultModel(), "currency", getPage())).setRenderBodyOnly(true)
        );

        PropertyListView<Education> education = new PropertyListView<Education>("education") {
            @Override
            protected void populateItem(ListItem<Education> item) {
                item.add(
                        new Label("grade", getEnumerationModel(item.getModel(), "grade", getPage())).setRenderBodyOnly(true),
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
                        new Label("currency", getEnumerationModel(item.getModel(), "currency", getPage())).setRenderBodyOnly(true),
                        new Label("period", new AbstractReadOnlyModel<String>() {
                            @Override
                            public String getObject() {
                                return item.getModelObject().getStart() + " - " + item.getModelObject().getEnd();
                            }
                        }).setRenderBodyOnly(true)

                );
            }
        };

        add(education, experience);
    }
}
