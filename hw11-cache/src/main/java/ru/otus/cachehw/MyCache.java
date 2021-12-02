package ru.otus.cachehw;


import java.util.ArrayList;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private final WeakHashMap<K,V> cache;
    private final ArrayList<HwListener<K, V>> listeners;

    public MyCache() {
        this.cache = new WeakHashMap<>();
        this.listeners = new ArrayList<>();
    }
   //Надо реализовать эти методы

    @Override
    public void put(K key, V value) {
        listeners.forEach(listener -> {
            try {
                listener.notify(key, value, "put");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        if (cache.get(key) == null) {
            cache.put(key,value);
        }
    }

    @Override
    public void remove(K key) {
        V removedValue = cache.get(key);
        listeners.forEach(listener -> {
            try {
                listener.notify(key, removedValue, "remove");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (removedValue != null) {
                cache.remove(key);
            }
        });
    }

    @Override
    public V get(K key) {
        V getValue = cache.get(key);
        listeners.forEach(listener -> {
            try {
                listener.notify(key, getValue, "get");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return getValue;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }
}
