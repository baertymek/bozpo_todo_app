package eu.baertymek.bozpo_todo_app.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.Route;
import eu.baertymek.bozpo_todo_app.entities.Task;
import eu.baertymek.bozpo_todo_app.services.CrmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Route("")
public class MainView extends VerticalLayout {

    CrmService service;
    private static final Logger logger = LoggerFactory.getLogger(MainView.class);

    private final NumberField pageCountNumber, pageCurrentNumber;
    private final Button buttonBack, buttonForward, buttonFirst, buttonLast;
    private final MessageInput input;
    private final VerticalLayout pageTasks;
    private final HorizontalLayout paginatorLayout;

    public MainView(CrmService service) {
        this.service = service;

        add(new H1("To-do aplikácia"));

         pageTasks = new VerticalLayout();
         paginatorLayout = new HorizontalLayout();

        // Input field with button
        input = new MessageInput();
        input.setI18n(new MessageInputI18n().setMessage("Popis aktivity").setSend("Uložiť"));
        input.setWidth(25, Unit.EM);
        input.addSubmitListener(submitEvent -> {
            Task task = new Task(submitEvent.getValue());
            service.saveTask(task);
            // If task page is not full, add task to current page.
            if (pageTasks.getComponentCount() < 10)
                pageTasks.add(createTaskItem(task));
            else
                updateMaxPages();
            logger.debug("Added new task '{}'", task.getDescription());
        });

        // Paginator layout
        pageCountNumber = new NumberField();
        pageCountNumber.setReadOnly(true);
        pageCountNumber.setWidth(3f, Unit.EM);
        pageCountNumber.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        updateMaxPages();

        pageCurrentNumber = new NumberField();
        pageCurrentNumber.setValue(1.0);
        pageCurrentNumber.setMin(1);
        pageCurrentNumber.setWidth(3f, Unit.EM);
        pageCurrentNumber.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        pageCurrentNumber.addValueChangeListener(listener -> {
            if (listener.getValue() < 1) {
                pageCurrentNumber.setValue(1.0);
            }
            if (listener.getValue() > pageCountNumber.getValue()) {
                pageCurrentNumber.setValue(pageCountNumber.getValue());
            }

            loadPagedTasks(pageTasks);
            updateMaxPages();
        });

        buttonBack = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
        buttonForward = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        buttonFirst = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
        buttonLast = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));

        buttonBack.addClickListener(listener -> {
            Double nextPage = pageCurrentNumber.getValue()-1.0;
            pageCurrentNumber.setValue(nextPage);
            logger.debug("Change to task page {}", nextPage.intValue());
        });
        buttonForward.addClickListener(listener -> {
            Double nextPage = pageCurrentNumber.getValue()+1.0;
            pageCurrentNumber.setValue(nextPage);
            logger.debug("Change to task page {}", nextPage.intValue());
        });
        buttonFirst.addClickListener(listener -> {
            pageCurrentNumber.setValue(1.0);
            logger.debug("Change to first task page");
        });
        buttonLast.addClickListener(listener -> {
            pageCurrentNumber.setValue(pageCountNumber.getValue());
            logger.debug("Change to last task page");
        });

        paginatorLayout.add(
                buttonFirst,
                buttonBack,
                pageCurrentNumber,
                new Paragraph("/"),
                pageCountNumber,
                buttonForward,
                buttonLast
        );


        // Add everything together and center items
        add(input, pageTasks, paginatorLayout);
        addClassName("todo-div");
        setAlignItems(Alignment.CENTER);
        pageTasks.setAlignItems(Alignment.CENTER);
        loadPagedTasks(pageTasks);
    }

    private Component createTaskItem(Task task) {
        HorizontalLayout layout = new HorizontalLayout(JustifyContentMode.CENTER);

        Checkbox checkbox = new Checkbox(task.getDescription(), task.getFinished());
        checkbox.setWidth(20, Unit.EM);
        if (task.getFinished()) checkbox.addClassNames("task-finished");
        checkbox.addValueChangeListener(changeEvent -> {
            task.setFinished(changeEvent.getValue());
            if (task.getFinished())
                checkbox.addClassNames("task-finished");
            else
                checkbox.removeClassName("task-finished");
            service.saveTask(task);
            logger.debug("Set task status to '{}'", (task.getFinished() ? "finished" : "ready to be finished"));
        });

        Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
        deleteButton.setWidth(2, Unit.EM);
        deleteButton.addClassName("remove-button");
        deleteButton.addClickListener(listener -> {
            service.deleteTask(task);
            Optional<Component> parent = layout.getParent();
            parent.ifPresent(component -> {
                VerticalLayout page = (VerticalLayout) component;
                // If it's the last task on page, move user 1 page back.
                if (pageTasks.getComponentCount() == 1)
                    pageCurrentNumber.setValue(pageCurrentNumber.getValue()-1.0);
                loadPagedTasks(page);
                if (page.getComponentCount() == 10)
                    updateMaxPages();
            });
            logger.debug("Removed task '{}'", task.getDescription());
        });

        layout.add(checkbox, deleteButton);
        return layout;
    }

    private void loadPagedTasks(VerticalLayout page) {
        page.removeAll();
        service.findTasksByPage(pageCurrentNumber.getValue().intValue()).forEach(task -> page.add(createTaskItem(task)));
    }

    private void updateMaxPages() {
        pageCountNumber.setValue(Math.ceil(service.countTasks()/10.0));
    }
}
