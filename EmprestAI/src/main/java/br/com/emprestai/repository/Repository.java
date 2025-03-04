package br.com.emprestai.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    T save(T entity); // Create ou Update

    Optional<T> findById(ID id); // Read

    List<T> findAll(); // Read

    void deleteById(ID id);
}
