package com.codex;
import com.codex.framework.DIContainer.Injector;
import com.codex.framework.EntityManager.DatabaseConnection;
import com.codex.testing.Book;
import com.codex.testing.interfaces.IBook;


public class Main {

    public static void main(String[] args) throws Exception {
        Injector injector = new Injector();
        injector.initFramework(Main.class);
        IBook book = (IBook) injector.getBean(Book.class);
        book.index();
        DatabaseConnection.getInstance().getConnection();


    }

}

