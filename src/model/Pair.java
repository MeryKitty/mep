package model;

import java.util.Objects;

@__inline__
public class Pair<T, U> {
	private final T first;
	private final U second;
	private int hashCode;
	
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
		this.hashCode = Objects.hashCode(first) + Objects.hashCode(second);
	}
	
	public T first() {
		return this.first;
	}
	
	public U second() {
		return this.second;
	}
	
	@Override
	public String toString() {
		return "<" + first.toString() + ", " + second.toString() + ">";
	}
	
	@Override
	public int hashCode() {
		return this.hashCode;
	}
}
