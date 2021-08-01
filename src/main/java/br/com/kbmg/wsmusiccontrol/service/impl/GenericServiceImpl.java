package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.AbstractEntity;
import br.com.kbmg.wsmusiccontrol.service.GenericService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class GenericServiceImpl<T extends AbstractEntity, R extends JpaRepository<T, Long>> implements GenericService<T> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private R jpaRepository;

    @Override
    public T create(T entity) {
        entity.setId(null);
        return this.saveOrUpdate(entity);
    }

    @Override
    public T update(T actual) {
        if (actual == null || actual.getId() == null) {
            throw new ServiceException("Update Error");
        }
        return this.saveOrUpdate(actual);
    }

    @Override
    public Optional<T> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<T> findAllById(Set<Long> ids) {
        return jpaRepository.findAllById(ids);
    }

    @Override
    public List<T> findAll() {
        return this.jpaRepository.findAll();
    }

    @Override
    public <F> void validateIfAlreadyExist(Long id, F fieldUnique, Function<F, Optional<T>> method, String msgError) {
        T entityFound = method.apply(fieldUnique).orElse(null);
        this.verifyIfAlreadyExist(id, entityFound, msgError);
    }

    @Override
    public <F, O> void validateIfAlreadyExist(Long id, F field, O otherField, BiFunction<F, O, Optional<T>> method, String msgError) {
        T entityFound = method.apply(field, otherField).orElse(null);
        this.verifyIfAlreadyExist(id, entityFound, msgError);
    }

    @Override
    public void deleteById(Long id) {
        this.findById(id);
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByIdValidated(Long id, String msgError) {
        this.findByIdValidated(id, msgError);
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        Set<Long> notExist = ids.stream().filter(id -> jpaRepository.findById(id).isEmpty()).collect(Collectors.toSet());

        if (!notExist.isEmpty())
            throw new EntityNotFoundException("Not found: " + notExist);

        ids.forEach(id -> jpaRepository.deleteById(id));
    }

    @Override
    public void deleteInBatch(Collection<T> entities) {
        this.deleteInBatch(entities, 1000);
    }

    @Override
    public void deleteInBatch(Collection<T> entities, int size) {
        Lists.partition(new ArrayList<>(entities), size).forEach(jpaRepository::deleteAllInBatch);
    }

    @Override
    public Page<T> findPaginated(int page, int size, Direction direction, String sortProperty) {
        return jpaRepository.findAll(PageRequest.of(page, size, direction, sortProperty));
    }

    @Override
    public List<T> saveAll(Iterable<T> entities) {
        return jpaRepository.saveAll(entities);
    }

    @Override
    public T findByIdValidated(Long id, String msgError) {
        Optional<T> entity = findById(id);
        return entity.orElseThrow(() -> new EntityNotFoundException(msgError));
    }

    @Override
    public List<T> findAll(Sort sort) {
        return jpaRepository.findAll(sort);
    }

    @Override
    public void flush() {
        jpaRepository.flush();
    }

    @Override
    public void flushAndClear() {
        this.flush();
        this.entityManager.clear();
    }

    @Override
    public void delete(T entity) {
        jpaRepository.delete(entity);
    }

    private T saveOrUpdate(T entity) {
        try {
            return jpaRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Failed to save");
        }
    }

    private void verifyIfAlreadyExist(Long id, T entityFound, String msgError) {
        if (entityFound != null && !entityFound.getId().equals(id)) {
            throw new ServiceException(msgError);
        }
    }

}
