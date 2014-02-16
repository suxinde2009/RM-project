package com.jan.rm.dao.ds;

public interface TreeCursorable<T> {

	public T next();
	public T previous();
	
	public T childNext();
	public T childPrevious();
	
	public boolean hasNext();
	public boolean hasPrevious();
	
	public boolean hasChildNext();
	public boolean hasChildPrevious();
}
