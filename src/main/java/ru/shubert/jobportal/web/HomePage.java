package ru.shubert.jobportal.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.shubert.jobportal.web.panel.UserPanel;
import ru.shubert.jobportal.web.proto.BasePage;

/**
 * Home page of application
 * Contains:
 * - logged in user panel
 * - menu
 * - useful stuff
 * Anyone may see the page
 */
@AuthorizeInstantiation({"ADMIN", "PERSON", "EMPLOYER"})
public class HomePage extends BasePage {
    private static final long serialVersionUID = 1L;

    protected FeedbackPanel feedbackPanel;


    @SuppressWarnings({"UnusedDeclaration"})
    public HomePage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
         add(feedbackPanel, new MenuAllResume(), new MenuAllVacancy(),
                 new MenuMyResume(), new MenuMyVacancy(), new UserPanel());
    }


}
