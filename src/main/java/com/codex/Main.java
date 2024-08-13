package com.codex;

import com.codex.framework.DIContainer.Injector;
import com.codex.framework.Kernel;
import com.codex.testing.Components.Book;
import com.codex.testing.Components.interfaces.IBook;

public class Main {

    public static void main(String[] args) throws Exception {
        Kernel.run(Main.class);

        IBook book = (IBook) Injector.getInstance().getBean(Book.class);
        book.index();
    }
}
