package ru.shubert.jobportal.web.proto;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shubert.jobportal.web.panel.UserPanel;

import java.util.Arrays;


/**
 * Defines abstract preseted web page with base layouts, security restrictions
 * and {@link ru.shubert.jobportal.web.panel.UserPanel}
 */
public abstract class BasePage extends WebPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);


    /**
     * Default 'no params' contructor. Adds {@link UserPanel} and
     * feedback panel
     */
    public BasePage() {
        super();
    }

    /**
     * Constructor for List pages  with one only parameter of Entity.class value
     *
     * @param parameters used in page building process
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public BasePage(final PageParameters parameters) {
        super(parameters);
    }

    /**
     * Convinient constructor with "where to go when back button is pressed" Edit pages.
     * and "what we gonna delete/edit/add"
     * Transmitted object must be obtained from a service layer
     *
     * @param model for Edit/Add/Delete pages
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public BasePage(final IModel<?> model) {
        super(model);
    }

    /**
     * <pre>
     * Как делается меню:
     * - базовый класс страницы запрашивает полное меню для текущего юзера
     * - возвращенное меню это LinkedList значений [ заголовок - путь - страница ]
     * - в процессе итерации сравнивается "страница" с "собой". Если равно, то текущий пункт меню рендерится как лейбл, а не как ссылка.
     * Вот только времени уже нет. Делаем, чтобы просто работало.
     * </pre>
     */

    @AuthorizeAction(action = Action.RENDER, roles = {"ADMIN", "PERSON"})
    final public class MenuMyResume extends WebMarkupContainer {
        public MenuMyResume() {
            super("myresume");
        }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {"ADMIN", "EMPLOYER"})
    final public class MenuMyVacancy extends WebMarkupContainer {
        public MenuMyVacancy() {
            super("myvacancy");
        }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {"ADMIN", "EMPLOYER"})
    final public class MenuAllResume extends WebMarkupContainer {
        public MenuAllResume() {
            super("allresume");
        }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {"ADMIN", "PERSON"})
    final public class MenuAllVacancy extends WebMarkupContainer {
        public MenuAllVacancy() {
            super("allvacancy");
        }
    }

    /**
     * Shortcut for new Text Field with required and max length validators
     *
     * @param id        markup identifier
     * @param maxLength max string length
     * @return new {@link org.apache.wicket.markup.html.form.RequiredTextField}
     */
    static public RequiredTextField<String> newRequiredTextField(String id, int maxLength) {
        RequiredTextField<String> textField = new RequiredTextField<>(id);
        textField.add(StringValidator.maximumLength(maxLength));
        return textField;
    }

    /**
     * Shortcut for new Text Field with required and max length validators
     *
     * @param id        markup identifier
     * @param maxLength max string length
     * @return new {@link org.apache.wicket.markup.html.form.RequiredTextField}
     */
    static public TextField<String> newTextField(String id, int maxLength) {
        TextField<String> textField = new TextField<>(id);
        textField.add(StringValidator.maximumLength(maxLength));
        return textField;
    }


    /**
     * Builds and returns text field with required and email validators
     *
     * @param id        html id
     * @param maxLenght maximum field length
     * @return new text field with not empty and e-mail validators
     */
    static public RequiredTextField<String> newRequiredEmailField(String id, int maxLenght) {
        return (RequiredTextField<String>) newRequiredTextField(id, maxLenght).add(EmailAddressValidator.getInstance());
    }

    /**
     * creates and returns {@link Label}  for a given component with id equal to component's id with "-label" append
     * It uses {@link org.apache.wicket.Page#getString(String)} so the method cannot be made static.
     *
     * @param component to be labeled
     * @return label object
     */
    static public Label labelFor(final FormComponent component) {
        return labelFor(component, new ResourceModel(component.getId()));
    }

    /**
     * creates and returns {@link Label}  for a given component with id equal to component's id with "-label" append
     * It uses {@link org.apache.wicket.Page#getString(String)} so the method cannot be made static.
     *
     * @param component to be labeled
     * @param model     resource string model
     * @return label object
     */
    static public Label labelFor(final FormComponent component, IModel model) {
        return new Label(component.getId() + "-label", model);
    }

    /**
     * Returns {@link FeedbackPanel} build on received component. It's wicket:id is set to
     * component.getId()+"-feedback" suffix. {@link org.apache.wicket.feedback.ComponentFeedbackMessageFilter} makes panel show only messages
     * for that component
     *
     * @param component providing id and serving as a filter
     * @return same component
     */
    static public FeedbackPanel feedbackFor(final FormComponent component) {
        if (component.getLabel() == null) {
            component.setLabel(new ResourceModel(component.getId()));
        }

        FeedbackPanel panel = new FeedbackPanel(component.getId() + "-feedback");
        panel.setFilter(new ComponentFeedbackMessageFilter(component));
        panel.setOutputMarkupId(true);
        return panel;
    }

    /**
     * Returns new DropDownChoice for an enumeration
     * <p/>
     * It will attempt to lookup strings used for the display value using a localizer of a given
     * component (stringSource parameter). If the component is not specified, the global instance of
     * localizer will be used for lookup.
     * <p>
     * display value resource key format: {@code <enum.getSimpleClassName()>.<enum.name()>}
     * </p>
     * <p>
     * id value format: {@code <enum.name()>}
     * </p>
     *
     * @param id           for component (wicket:id)
     * @param enumClass    to use as values
     * @param stringSource component serving as a localized string source (ussuale 'this')
     * @param <T>          enumeration generic
     * @return new {@code DropDownChoice<T>}
     */
    static public <T extends Enum<T>> DropDownChoice<T> dropDownForEnum(String id, Class<T> enumClass, Component stringSource) {
        DropDownChoice<T> types = new DropDownChoice<>(id, Arrays.asList(enumClass.getEnumConstants()));
        types.setChoiceRenderer(new EnumChoiceRenderer<T>(stringSource));
        types.setRequired(true);
        return types;
    }

    public static IModel<String> getEnumerationModel(IModel formModel, String property, final Component component) {
        return new PropertyModel<String>(formModel, property) {
            @Override
            public String getObject() {
                Object o = super.getObject();
                if (o != null)
                    return component.getString(
                            o.getClass().getSimpleName() + "." + o.toString(), null
                    );
                else
                    return null;
            }
        };
    }


}
