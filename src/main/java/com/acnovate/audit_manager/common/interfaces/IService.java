package com.acnovate.audit_manager.common.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.acnovate.audit_manager.common.dto.FilterDto;

public interface IService<T> {
	// -- find - one
	T findOne(final long id);

	/**
	 * - contract: if nothing is found, an empty list will be returned to the
	 * calling client <br>
	 */
	List<T> findAll();

	List<T> findAll(Specification<T> spec);

	List<T> findAll(Specification<T> spec, Sort sort);

	List<T> findAll(FilterDto filter);

	List<T> findAll(FilterDto filter, boolean sorting);

	T findOne(Specification<T> spec);

	T findOne(FilterDto filter);

	T findOne(Specification<T> spec, Sort sort);

	T findOne(FilterDto filter, boolean sorting);

	Page<T> findAll(Pageable pageable);

	Page<T> findAll(Integer size, Integer pageNo, FilterDto filter);

	Page<T> findAll(Integer size, Integer pageNo);

	Page<T> findAll(Specification<T> spec, Pageable pageable);

	Page<?> findAllWithDto(Specification<T> spec, Pageable pageable);

	List<T> findAllById(List<Long> ids);

	// -- create
	T create(final T resource);

	// -- update
	T update(final T resource);

	// delete
	void delete(final long id);

	void deleteAll();

	void deleteAll(List<T> entities);

	// --count
	long count();

	long count(FilterDto filter);

//	Page<?> domainToDto(Page<T> pages);

//	List<?> domainToDto(List<T> list);

//	Object domainToDto(T resource);

	Long getLoggedUserId();

}
