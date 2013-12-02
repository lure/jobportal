package ru.shubert.jobportal.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * <p> Add ability to highlight themself on errors to control. Whenever control must be highlighted depends on false
 * value returned from {@link org.apache.wicket.markup.html.form.FormComponent#isValid()} } </p>
 * <p/>
 * <p>If component is not valid then css style attribute is modofied.
 */
abstract public class HighlightUtils {

    private static final String ERRORDIVCSSCLASS = "feedbackmessage";
    private static final HighlightBehaviour HIGHLIGHT_BEHAVIOUR = new HighlightBehaviour();
    private static final FeedbackBehaviour FEEDBACK_BEHAVIOUR = new FeedbackBehaviour();

    /*
    private static final String HIGHLIGHTCSS = "/css/feedback.css";
    // This implementation adds css class to tag  and does not work if there are more narrow definition
    private static class HighlightBehaviour extends Behavior {
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            response.renderCSSReference(HIGHLIGHTCSS);
        }

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            if (!((FormComponent) component).isValid()) {
                //AttributeAppender
                String cssClass =  tag.getAttribute("class");
                if (!StringUtils.contains(cssClass, HIGHLIGHTCSSCLASS)) {
                    StringBuilder sb = new StringBuilder(cssClass);
                    sb.append(" ").append(HIGHLIGHTCSSCLASS);
                    tag.put("class",  sb.toString());
                }
            }
        }
    }
    */

    /**
     * This implementation modifies tag style attribute, adding carmin colored background.
     * Any pre-existing style values are kept.
     */
    private static class HighlightBehaviour extends Behavior {
        private final static String HLSTYLE ="background-color: #fcc";
        private final static HashSet<String> TAGS = new HashSet<>();
        static {
            TAGS.add("input");
            TAGS.add("textarea");
            TAGS.add("select");
        }

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            if (!((FormComponent) component).isValid()) {
                if ( TAGS.contains(tag.getName()) ) {
                    String cssStyle =  tag.getAttribute("style");
                    StringBuilder sb = new StringBuilder();
                    if (! Strings.isEmpty(cssStyle)) {
                        sb.append(cssStyle).append(";");
                    }
                    sb.append(HLSTYLE);
                    tag.put("style",  sb.toString());
                }
            }
        }
    }

    private static class FeedbackBehaviour extends Behavior {
        @Override
        public void afterRender(Component component) {
            if (!((FormComponent) component).isValid()) {

                if (component.hasFeedbackMessage()) {
                    String message = component.getFeedbackMessage().getMessage().toString();
                    String div = String.format("<div class='%s'>%s</div>", ERRORDIVCSSCLASS, message);
                    component.getResponse().write(div);
                }
            }
        }
    }

    private static class FormVisitor implements IVisitor<FormComponent, Void> {
        private Set<FormComponent> visited = new HashSet<>();

        @Override
        public void component(FormComponent component, IVisit iVisit) {
            if (!visited.contains(component)) {
                visited.add(component);
                addHighlightBehaviour(component);
            }
        }
    }

    /**
     * Adds ability to highlight himself to one component
     *
     * @param component to be fancied with highlight ability
     * @return same component
     */
    static public Component addHighlightBehaviour(@NotNull final FormComponent component) {
        return component.add(HIGHLIGHT_BEHAVIOUR);
    }

    /**
     * Adds ability to highligh himself to one component
     *
     * @param components to be fancied with highlight ability
     */
    static public void addHighlightBehaviour(@NotNull final FormComponent... components) {
        for (FormComponent c : components) {
            c.add(HIGHLIGHT_BEHAVIOUR);
        }
    }

    /**
     * Adds ability to show div element with error feedback message to one component
     * div appearance may be customized with {@link #ERRORDIVCSSCLASS} css class
     *
     * @param component to be fancied with feedback ability
     * @return same component
     */
    static public Component addFeedbackBehaviour(@NotNull final FormComponent component) {
        return component.add(FEEDBACK_BEHAVIOUR);
    }

    /**
     * Visit {@link FormComponent} childs of the Form and executes {@link #addHighlightBehaviour(FormComponent)} on every
     * of them
     *
     * @param form whos children must be iterated
     * @return same form
     */
    static public Form highlightForm(@NotNull final Form form) {
        form.visitChildren(FormComponent.class, new FormVisitor());
        return form;
    }


}