    package com.codex.testing.Components;
    import com.codex.framework.DIContainer.annotations.Autowired;
    import com.codex.framework.DIContainer.annotations.Component;
    import com.codex.framework.DIContainer.annotations.Qualifier;
    import com.codex.testing.Components.interfaces.IBook;
    import com.codex.testing.Components.interfaces.IUser;

    @Component
    public class Book implements IBook {
        private IUser user;

        @Autowired
        @Qualifier(User2.class)
        public Book(IUser user){
            this.user = user;
        }

        @Override
        public void index () {
           System.out.println( user.getName());
        }
    }
