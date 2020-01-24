package ex1;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public abstract class ArrayList<T> implements ListADT<T> {

    protected T[] list;

    protected int rear;

    private static final int DEFAULT_LENGTH = 5;

    protected int modCount;

    public ArrayList() {
        this.list = (T[]) new Object[DEFAULT_LENGTH];
        this.rear = 0;
        this.modCount = 0;
    }

    public ArrayList(int size) {
        this.list = (T[]) new Object[DEFAULT_LENGTH];
        this.rear = 0;
        this.modCount = 0;
    }

    @Override
    public T removeFirst() throws EmptyException {
        if (this.isEmpty()) {
            throw new EmptyException("tá vazio");
        }

        T temp = this.first();

        for (int i = 0; i < this.size() - 1; i++) {
            list[i] = list[i + 1];
        }

        this.list[this.rear - 1] = null;
        this.rear--;
        this.modCount++;

        return temp;
    }

    @Override
    public T removeLast() throws EmptyException {
        if (this.isEmpty()) {
            throw new EmptyException("tá vazio");
        }

        T temp = this.last();

        this.list[this.rear - 1] = null;
        this.rear--;
        this.modCount++;

        return temp;
    }

    @Override
    public T remove(T element) throws EmptyException, NotFoundException {
        if (this.isEmpty()) {
            throw new EmptyException("tá vazio");
        }

        int remove;

        try {
            remove = this.find(element);
        } catch (NotFoundException n) {
            throw n;
        }

        T temp = this.list[remove];

        for (int i = remove; i < this.size() - 1; i++) {
            this.list[i] = this.list[i + 1];
        }

        this.list[this.rear - 1] = null;
        this.rear--;
        this.modCount++;

        return temp;
    }

    protected int find(T elem) throws NotFoundException {
        for (int i = 0; i < this.size(); i++) {
            if (this.list[i].equals(elem)) {
                return i;
            }
        }

        throw new NotFoundException("nao existe");
    }

    @Override
    public T first() throws EmptyException {
        if (this.isEmpty()) {
            throw new EmptyException("tá vazio!");
        }

        return this.list[0];
    }

    @Override
    public T last() throws EmptyException {
        if (this.isEmpty()) {
            throw new EmptyException("tá vazio!");
        }

        return this.list[this.rear - 1];
    }

    @Override
    public boolean contains(T target) {
        try {
            this.find(target);
        } catch (NotFoundException n) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.rear == 0;
    }

    @Override
    public int size() {
        return this.rear;
    }

    protected void expandCapacity() {
        T[] temp = (T[]) new Object[this.size() * 2];

        for (int i = 0; i < this.size(); i++) {
            temp[i] = this.list[i];
        }

        this.list = temp;
    }

    @Override
    public Iterator<T> iterator() {
        return new MyItr();
    }

    private class MyItr implements Iterator<T> {
        private int cursor;
        private int expectedModCount;
        private boolean okToRemove;

        public MyItr() {
            this.cursor = 0;
            this.expectedModCount = modCount;
            this.okToRemove = false;
        }

        @Override
        public boolean hasNext() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException("A LISTA FOI MUDADA");
            }

            this.okToRemove = false;

            return this.cursor != size();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new ArrayIndexOutOfBoundsException("já percorreu todo");
            }

            this.okToRemove = true;

            return list[this.cursor++];
        }

        @Override
        public void remove() {
            if (!this.okToRemove) {
                try {
                    throw new NotRemovableException("não removível");
                } catch (NotRemovableException e) {
                    throw new ConcurrentModificationException("Error");
                }
            }

            try {
                ArrayList.this.remove(list[this.cursor - 1]);
            } catch (NotFoundException | EmptyException e) {
                throw new ConcurrentModificationException("Error");
            }

            this.okToRemove = false;
            this.expectedModCount = modCount;
        }

    }
}
