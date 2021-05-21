package general.util;


class Queue<E> extends List<E> {

    public abstract void offer(E e);
    
    public abstract E poll();
    
    public abstract E peek();

}