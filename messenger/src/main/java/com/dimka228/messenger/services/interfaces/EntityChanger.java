package com.dimka228.messenger.services.interfaces;
@FunctionalInterface
public interface  EntityChanger<T> {
    public void change(T entity);
}
