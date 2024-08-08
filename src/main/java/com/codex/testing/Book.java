    package com.codex.testing;
    import com.codex.framework.DI.annotations.Autowired;
    import com.codex.framework.DI.annotations.Component;
    import com.codex.framework.DI.annotations.Qualifier;
    import com.codex.testing.interfaces.IBook;
    import com.codex.testing.interfaces.IUser;

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
