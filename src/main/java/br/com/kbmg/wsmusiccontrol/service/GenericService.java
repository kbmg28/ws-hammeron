package br.com.kbmg.wsmusiccontrol.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Generic service for CRUD operations
 *
 * @param <T> The entity in which the service is working on
 */
public interface GenericService<T> {

    T create(T entity);
    T update(T entity);

    Optional<T> findById(Long id);
    T findByIdValidated(Long id, String msgError);
    Page<T> findPaginated(int page, int size, Direction direction, String sortProperty);
    List<T> findAllById(Set<Long> ids);
    List<T> findAll();
    List<T> findAll(Sort sort);

    void delete(T entity);
    void deleteById(Long id);
    void deleteByIdValidated(Long id, String msgError);
    void deleteAllById(List<Long> ids);
    void deleteInBatch(Collection<T> entities);
    void deleteInBatch(Collection<T> entities, int size);

    List<T> saveAll(Iterable<T> entities);
    void flush();
    void flushAndClear();

    /**
     * <p>
     * Validate if already exists a entity by one field unique.
     * </p>
     *
     * @param id          id from entity
     * @param fieldUnique any entity field value that is unique
     * @param method      any method in your repository that accepts the type specified in <F> as a parameter and returns an Optional <T>
     * @param msgError    error message for exception
     * @param <F>         a field type of the entity
     */
    <F> void validateIfAlreadyExist(Long id, F fieldUnique, Function<F, Optional<T>> method, String msgError);

    /**
     * <p>
     * Validate that an entity already exists by two unique fields.
     * </p>
     *
     * @param id id from entity
     * @param field any field value
     * @param otherField other field value different
     * @param method any method in your repository that accepts the type specified in <F> and <O> as parameters and returns an Optional <T>
     * @param msgError error message for exception
     * @param <F> a field type of the entity
     * @param <O> other field type of the entity
     */
    <F, O> void validateIfAlreadyExist(Long id, F field, O otherField, BiFunction<F, O, Optional<T>> method, String msgError);
}
