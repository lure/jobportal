package ru.shubert.jobportal.web.component;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * External link descendant. Adds prefix "mailto:" to email address value so
 * it renders as &lt;a href="mailto:recipient@address.domain"&gt;...&lt;/a&gt;
 */
public class EmailPropertyLink extends ExternalLink {
    public EmailPropertyLink(String id, IModel model) {
        this(id, model, id);
    }

    public EmailPropertyLink(String id, IModel model, String expression) {
        super(id, new PropertyModel<String>(model, expression) {
                    @Override
                    public String getObject() {
                        String object = super.getObject();
                        return (object != null) ? "mailto:" + object : "";
                    }
                }, new PropertyModel<String>(model, expression){
                    @Override
                    public String getObject() {
                        String object = super.getObject();
                        return object != null? object: "";
                    }
                });
    }
}
