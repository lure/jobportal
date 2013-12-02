package ru.shubert.jobportal.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shubert.jobportal.model.employer.Vacancy;
import ru.shubert.jobportal.model.person.Person;
import ru.shubert.jobportal.service.IService;
import ru.shubert.jobportal.web.panel.PersonPanel;
import ru.shubert.jobportal.web.panel.UserPanel;
import ru.shubert.jobportal.web.panel.VacancyPanel;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * Renders a single bookmarkable page for a vacancy \ resume
 */
@AuthorizeInstantiation({"PERSON", "ADMIN", "EMPLOYER"})
public class ShowItemPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowItemPage.class);

    public final static String ID = "id";
    public final static String ITEM = "item";


    public static enum ItemType {
        vacancy,
        resume
    }

    @SpringBean(name="baseService")
    private IService service;

    private ItemType item;  // item type to be shown
    private ObjectId id;        // item id to be loaded from persistence storage


    public ShowItemPage(PageParameters parameters) {
        super(parameters);
        setVersioned(false);
        setStatelessHint(true);
        try {
            item = ItemType.valueOf(parameters.get(ITEM).toString());
            id = new ObjectId(parameters.get(ID).toString());
        } catch (Exception e) {
            item = null;
            id = null;
            LOGGER.error("ShowItemPage failed to parse parameters {}", e);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label header = new Label("header", new ResourceModel(item == null ? "emptyheader" : item.toString()));

        Object o = null;
        if (null != item) {
            if (ItemType.vacancy.equals(item)) {
                o = service.findOne(id, Vacancy.class);
                if (null != o) {
                    add(new VacancyPanel("panel", new CompoundPropertyModel<>(o)));
                }
            } else {
                o = service.findOne(id, Person.class);
                if (null != o) {
                    add(new PersonPanel("panel", new CompoundPropertyModel<>(o)));
                }
            }
        }

        if (null == o || null == item) {

            add(new WebMarkupContainer("panel"));
        }
        add(header, new MenuAllResume(), new MenuAllVacancy(), new MenuMyResume(), new MenuMyVacancy(), new UserPanel());
    }
}