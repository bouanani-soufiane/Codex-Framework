package com.codex;

import com.codex.framework.EntityManager.Core.ProcessManager;
import com.codex.testing.Components.Book;
import com.codex.testing.Components.interfaces.IBook;

public class Main {

    public static void main(String[] args) throws Exception {
        ProcessManager processManager = new ProcessManager(Main.class);
        processManager.run();

        IBook book = (IBook) processManager.getInjector().getBean(Book.class);
        book.index();
    }
}
