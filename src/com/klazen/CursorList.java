package com.klazen;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class CursorList<T> implements Collection<T> {
	int cursor = 0;
	LinkedList<T> internalCollection = new LinkedList<T>();
	
	public T get() {
		if (isEmpty()) return null;
		if (++cursor >= size()) cursor = 0;
		return internalCollection.get(cursor);
	}
	
	public void setCursor(int cursor) {
		if (cursor < 0) cursor = 0;
		if (cursor > size()-1) cursor = size()-1;
		this.cursor = cursor;
	}

	@Override
	public boolean add(T item) {
		return internalCollection.add(item);
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		return internalCollection.addAll(arg0);
	}

	@Override
	public void clear() {
		internalCollection.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return internalCollection.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return internalCollection.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return internalCollection.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return internalCollection.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return internalCollection.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return internalCollection.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return internalCollection.retainAll(arg0);
	}

	@Override
	public int size() {
		return internalCollection.size();
	}

	@Override
	public Object[] toArray() {
		return internalCollection.toArray();
	}

	@Override
	public <Z> Z[] toArray(Z[] arg0) {
		return internalCollection.toArray(arg0);
	}
}
