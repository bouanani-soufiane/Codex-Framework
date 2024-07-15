
package com.codex;

import com.codex.framework.Injector;

public class App {
   public static void main(String[] args) {
       long startTime = System.currentTimeMillis();
       Injector.startApplication(UserAccountApplication.class);
       Injector.getService(UserAccountClientComponent.class).displayUserAccount();
       long endime = System.currentTimeMillis();
   }





}
