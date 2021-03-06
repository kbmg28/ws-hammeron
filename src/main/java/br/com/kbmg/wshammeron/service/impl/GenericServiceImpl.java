package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.exception.ServiceException;
import br.com.kbmg.wshammeron.model.AbstractEntity;
import br.com.kbmg.wshammeron.service.GenericService;
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

public abstract class GenericServiceImpl<T extends AbstractEntity, R extends JpaRepository<T, String>> implements GenericService<T> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    protected R repository;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    public LogService logService;

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
    public Optional<T> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAllById(Set<String> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<T> findAll() {
        return this.repository.findAll();
    }

    @Override
    public <F> void validateIfAlreadyExist(String id, F fieldUnique, Function<F, Optional<T>> method, String keyMessage) {
        T entityFound = method.apply(fieldUnique).orElse(null);
        this.verifyIfAlreadyExist(id, entityFound, keyMessage);
    }

    @Override
    public <F, O> void validateIfAlreadyExist(String id, F field, O otherField, BiFunction<F, O, Optional<T>> method, String keyMessage) {
        T entityFound = method.apply(field, otherField).orElse(null);
        this.verifyIfAlreadyExist(id, entityFound, keyMessage);
    }

    @Override
    public void verifyIfAlreadyExist(String id, T entityFound, String keyMessage) {
        if (entityFound != null && !entityFound.getId().equals(id)) {
            throw new ServiceException(messagesService.get(keyMessage));
        }
    }

    @Override
    public void deleteById(String id) {
        this.findById(id);
        repository.deleteById(id);
    }

    @Override
    public void deleteByIdValidated(String id, String msgError) {
        this.findByIdValidated(id, msgError);
        repository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<String> ids) {
        Set<String> notExist = ids.stream().filter(id -> repository.findById(id).isEmpty()).collect(Collectors.toSet());

        if (!notExist.isEmpty())
            throw new EntityNotFoundException("Not found: " + notExist);

        ids.forEach(id -> repository.deleteById(id));
    }

    @Override
    public void deleteInBatch(Collection<T> entities) {
        this.deleteInBatch(entities, 1000);
    }

    @Override
    public void deleteInBatch(Collection<T> entities, int size) {
        Lists.partition(new ArrayList<>(entities), size).forEach(repository::deleteAllInBatch);
    }

    @Override
    public Page<T> findPaginated(int page, int size, Direction direction, String sortProperty) {
        return repository.findAll(PageRequest.of(page, size, direction, sortProperty));
    }

    @Override
    public List<T> saveAll(Iterable<T> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public T findByIdValidated(String id, String msgError) {
        Optional<T> entity = findById(id);
        return entity.orElseThrow(() -> new EntityNotFoundException(msgError));
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public void flushAndClear() {
        this.flush();
        this.entityManager.clear();
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    private T saveOrUpdate(T entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Failed to save");
        }
    }

}
