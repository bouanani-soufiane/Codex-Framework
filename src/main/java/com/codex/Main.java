package com.codex;
import com.codex.framework.DIContainer.Injector;
import com.codex.framework.EntityManager.core.DatabaseConnection;
import com.codex.framework.EntityManager.core.ProcessManager;
import com.codex.testing.Components.Book;
import com.codex.testing.Components.interfaces.IBook;


public class Main {

    public static void main(String[] args) throws Exception {
        Injector injector = new Injector();
        injector.initFramework(Main.class);
        IBook book = (IBook) injector.getBean(Book.class);
        book.index();
        DatabaseConnection.getInstance().getConnection();

        ProcessManager processManager = new ProcessManager(Main.class);
        processManager.run();


    }

}

