package com.codex;
import com.codex.framework.Injector;
import com.codex.testing.Book;
import com.codex.testing.interfaces.IBook;


public class Main {
    public static void main(String[] args) throws IllegalAccessException, NoSuchMethodException {
        Injector injector = new Injector();
        injector.initFramework(Main.class);

        IBook book = (IBook) injector.getBean(Book.class);
        book.index();
    }

}

