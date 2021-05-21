package general.lang;

class Stack<E> extends List<E> {

    public abstract void push(E e);

    public abstract E pop();

    public abstract E peek();

}