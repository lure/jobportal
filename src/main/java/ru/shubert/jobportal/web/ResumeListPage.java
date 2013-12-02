package ru.shubert.jobportal.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.WicketObjects;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.person.Education;
import ru.shubert.jobportal.model.person.EducationGrade;
import ru.shubert.jobportal.model.person.JobExperience;
import ru.shubert.jobportal.model.person.Person;
import ru.shubert.jobportal.service.IAccountService;
import ru.shubert.jobportal.web.component.BookmarkableItemLink;
import ru.shubert.jobportal.web.panel.UserPanel;
import ru.shubert.jobportal.web.proto.BasePage;
import ru.shubert.jobportal.web.strategy.SortableFilteredDataProvider;

import java.util.ArrayList;
import java.util.List;

@AuthorizeInstantiation({"EMPLOYER", "ADMIN"})
public class ResumeListPage extends BasePage {
    @SpringBean
    IAccountService service;

    private Object originalState;

    // HTML table with list of entityes
    private DataTable<Person> view;

    public ResumeListPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // building new DataProvider
        final SortableFilteredDataProvider<Person> dataProvider = new SortableFilteredDataProvider<>(service, Person.class);
        dataProvider.getFilterState().getExperiences().add(new JobExperience());
        dataProvider.getFilterState().getEducation().add(new Education());
        // store clear state
        originalState = WicketObjects.cloneModel(dataProvider.getFilterState());

        // Form with filtering futures
        final Form<Person> filterForm = new Form<Person>("filter-form", new CompoundPropertyModel<>(dataProvider.getFilterState())) {
            @Override
            protected void onSubmit() {
                view.setCurrentPage(0);
            }
        };
        filterForm.add(new TextField<>("position"),
                new TextField<>("employer", new PropertyModel<>(filterForm.getModel(), "experiences[0].company")),
                new TextField<>("salary").setType(Integer.class),
                dropDownForEnum("currency", Currency.class, this).setRequired(false),
                dropDownForEnum("education[0].grade", EducationGrade.class, this).setRequired(false),
                new Button("filter-button"),
                new Button("reset-button"){
                    @Override
                    public void onSubmit() {
                        dataProvider.setFilterState((Person)WicketObjects.cloneModel(originalState));
                        filterForm.setDefaultModelObject(dataProvider.getFilterState());
                    }
                }.setDefaultFormProcessing(true));

        // Column list
        List<IColumn<Person>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<Person>(new ResourceModel("lastName"), "user.lastName"));
        columns.add(new PropertyColumn<Person>(new ResourceModel("position"), "position"));
        // salary column need speciall treatment for it's complicated nature
        columns.add(new AbstractColumn<Person>(new ResourceModel("salary")) {
            @Override
            public void populateItem(Item<ICellPopulator<Person>> item, String componentId, final IModel<Person> rowModel) {
                item.add(new Label(componentId, new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        Person v = rowModel.getObject();
                        return String.format("%s %s", v.getSalary(), getString("Currency." + v.getCurrency()));
                    }
                }));
            }
        });

        final IModel<String> SHOW = Model.of(getString("show"));
        columns.add(new HeaderlessColumn<Person>() {
            @Override
            public void populateItem(Item<ICellPopulator<Person>> item, String componentId, IModel<Person> rowModel) {
                PageParameters parameters = new PageParameters();
                parameters.add(ShowItemPage.ITEM, ShowItemPage.ItemType.resume);
                parameters.add(ShowItemPage.ID, rowModel.getObject().getId());
                item.add(new BookmarkableItemLink<Person>(componentId, ShowItemPage.class, parameters, SHOW));
            }
        });


        view = new DefaultDataTable<>("view", columns, dataProvider, 10);
        //view.addTopToolbar(new FilterToolbar(view, filterForm, dataProvider));
        view.setOutputMarkupId(true);
        //filterForm.add(view);
        add(view, filterForm, new UserPanel(), new MenuMyResume(), new MenuMyVacancy(), new MenuAllVacancy());
    }
}
