package crud.prontuario.dao;

import java.util.List;

public interface IEntityDAO <T> {

	public void create(T t);
	
	public T findById(Long id);
	
	public void delete(T t);
	
	public List<T> findAll();
	
	public void update(T t);
}
