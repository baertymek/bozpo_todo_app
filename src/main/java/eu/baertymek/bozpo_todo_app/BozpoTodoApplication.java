package eu.baertymek.bozpo_todo_app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "my-theme", variant = Lumo.DARK)
public class BozpoTodoApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(BozpoTodoApplication.class, args);
    }
}
