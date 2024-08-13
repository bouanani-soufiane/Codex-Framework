package com.codex.framework;

import com.codex.framework.EntityManager.Core.ProcessManager;

import java.sql.SQLException;

public class Kernel {

    public static void run(Class<?> applicationClass) throws SQLException {
        try {
            ProcessManager processManager = new ProcessManager(applicationClass);
            processManager.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to run the application", e);
        }
    }
}
