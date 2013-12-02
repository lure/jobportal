package ru.shubert.jobportal.web.component;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Renders bookmarkable link in a {@link org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable} columns.
 * This class solves the problem of rendering link caption.
 */
public class BookmarkableItemLink<T> extends Panel {

    final private Class<? extends Page> pageClass;

    final private PageParameters parameters;

    IModel<String> model;

    public <C extends Page> BookmarkableItemLink(final String id, final Class<C> pageClass, final PageParameters parameters, IModel<String> model) {
        super(id);
        this.parameters = parameters;
        this.pageClass = pageClass;
        this.model = model;
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        Link<T> link = new BookmarkablePageLink<>("link", pageClass, parameters);
        link.add(new Label("label", model));
        add(link);
    }
}
