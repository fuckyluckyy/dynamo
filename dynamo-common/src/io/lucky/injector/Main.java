package io.lucky.injector;

public class Main {

    public static void main(String[] args) {
        Injector injector = Injector.create();
        B b = injector.createInstance(B.class);
        injector.add("", b);
        A a = injector.createInstance(A.class);
        injector.add("", a);
    }

    static class A {

        private final B b;


        @Inject
        A(B b) {
            this.b = b;
            System.out.println("initialized a");
        }
    }

    static class B {

        B() {
            System.out.println("initialized b");
        }
    }
}
