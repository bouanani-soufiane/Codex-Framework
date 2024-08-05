    package com.codex.testing;

    import com.codex.framework.annotations.Autowired;
    import com.codex.framework.annotations.Component;
    import com.codex.framework.annotations.Qualifier;
    import com.codex.testing.interfaces.IBook;

    @Component
    public class Book implements IBook {
        private String title;

        @Autowired
        private User user;

        public void index(){
            System.out.println("here : " + user.getName());
        }

    }
