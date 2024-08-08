package com.codex;
import com.codex.framework.DI.Injector;
import com.codex.framework.ORM.DatabaseConnection;
import com.codex.testing.Book;
import com.codex.testing.User2;
import com.codex.testing.interfaces.IBook;

import java.sql.Connection;


public class Main {

    public static void main(String[] args) throws Exception {
        Injector injector = new Injector();
        injector.initFramework(Main.class);
        IBook book = (IBook) injector.getBean(Book.class);
        book.index();
        DatabaseConnection.getInstance().getConnection();


    }

}

