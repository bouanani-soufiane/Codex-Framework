    package com.codex.testing;

    import com.codex.framework.annotations.Autowired;
    import com.codex.framework.annotations.Component;
    import com.codex.testing.interfaces.IBook;
    import com.codex.testing.interfaces.IUser;

    @Component
    public class Book implements IBook {
        private String title;

        @Autowired
        private IUser user;

        public void index(){
            System.out.println("here : " + user.getName());
        }

    }
