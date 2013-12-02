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
import ru.shubert.jobportal.model.employer.Employer;
import ru.shubert.jobportal.model.employer.Vacancy;
import ru.shubert.jobportal.service.IService;
import ru.shubert.jobportal.web.component.BookmarkableItemLink;
import ru.shubert.jobportal.web.panel.UserPanel;
import ru.shubert.jobportal.web.proto.BasePage;
import ru.shubert.jobportal.web.strategy.SortableFilteredDataProvider;

import java.util.ArrayList;
import java.util.List;

@AuthorizeInstantiation({"PERSON", "ADMIN"})
public class VacancyListPage extends BasePage {
    private Object originalState;

    @SpringBean(name="baseService")
    IService service;

    // HTML table with list of entityes
    private DataTable<Vacancy> view;

    public VacancyListPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // building new DataProvider
        final SortableFilteredDataProvider<Vacancy> dataProvider = new SortableFilteredDataProvider<>(service, Vacancy.class);
        dataProvider.getFilterState().setEmployer(new Employer());
        // store clear state
        originalState = WicketObjects.cloneModel(dataProvider.getFilterState());

        // Form with filtering futures
        final Form<Vacancy> filterForm = new Form<Vacancy>("filter-form", new CompoundPropertyModel<>(dataProvider.getFilterState())) {
            @Override
            protected void onSubmit() {
                view.setCurrentPage(0);
            }
        };
        filterForm.add(new TextField<>("position"),
                new TextField<>("employer", new PropertyModel<>(filterForm.getModel(),"employer.name")),
                new TextField<>("salary").setType(Integer.class),
                dropDownForEnum("currency", Currency.class, this).setRequired(false),
                new Button("filter-button"),
                new Button("reset-button"){
                    @Override
                    public void onSubmit() {
                        dataProvider.setFilterState((Vacancy)WicketObjects.cloneModel(originalState));
                        filterForm.setDefaultModelObject(dataProvider.getFilterState());
                    }
                }.setDefaultFormProcessing(true));


        // Column list
        List<IColumn<Vacancy>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<Vacancy>(new ResourceModel("position"), "position"));
        columns.add(new PropertyColumn<Vacancy>(new ResourceModel("employer"), "employer.name"));
        // salary column need speciall treatment for it's complicated nature
        columns.add(new AbstractColumn<Vacancy>(new ResourceModel("salary")){
            @Override
            public void populateItem(Item<ICellPopulator<Vacancy>> item, String componentId, final IModel<Vacancy> rowModel) {
                item.add(new Label(componentId, new AbstractReadOnlyModel<String>(){
                    @Override
                    public String getObject() {
                        Vacancy v = rowModel.getObject();
                        return String.format("%s %s", v.getSalary(), getString("Currency." + v.getCurrency()));
                    }
                }));
            }
        });

        final IModel<String> SHOW = Model.of(getString("show"));
        columns.add(new HeaderlessColumn<Vacancy>() {
            @Override
            public void populateItem(Item<ICellPopulator<Vacancy>> item, String componentId, IModel<Vacancy> rowModel) {
                PageParameters parameters = new PageParameters();
                parameters.add(ShowItemPage.ITEM, ShowItemPage.ItemType.vacancy);
                parameters.add(ShowItemPage.ID, rowModel.getObject().getId());
                item.add(new BookmarkableItemLink<Vacancy>(componentId, ShowItemPage.class, parameters, SHOW));
            }
        });


        view = new DefaultDataTable<>("view", columns, dataProvider, 10);
        //view.addTopToolbar(new FilterToolbar(view, filterForm, dataProvider));
        view.setOutputMarkupId(true);
        //filterForm.add(view);
        add(view, filterForm, new UserPanel(), new MenuMyResume(), new MenuMyVacancy(), new MenuAllResume());
    }
}
