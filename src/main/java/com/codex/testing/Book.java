    package com.codex.testing;
    import com.codex.framework.annotations.Autowired;
    import com.codex.framework.annotations.Component;
    import com.codex.framework.annotations.Qualifier;
    import com.codex.testing.interfaces.IBook;
    import com.codex.testing.interfaces.IUser;

    @Component
    public class Book implements IBook {
        private final IUser user;
        private String title;

        @Autowired
        public Book( IUser user){
            this.user = user;
        }


        @Override
        public void index () {
           System.out.println( user.getName());
        }
    }
