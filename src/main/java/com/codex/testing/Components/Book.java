    package com.codex.testing.Components;
    import com.codex.framework.DIContainer.annotations.Autowired;
    import com.codex.framework.DIContainer.annotations.Component;
    import com.codex.framework.DIContainer.annotations.Qualifier;
    import com.codex.testing.Components.interfaces.IBook;
    import com.codex.testing.Components.interfaces.IUser;

    @Component
    public class Book implements IBook {
        private IUser user;
        private Pay pay;
        public String title = "kitab 3ajib";
        @Autowired

        @Qualifier(User.class)
        public Book(IUser user ,Pay pay   ){
            this.pay = pay;
            this.user = user;
        }

        @Override
        public void index () {
           System.out.println( user.getName());
        }
    }
