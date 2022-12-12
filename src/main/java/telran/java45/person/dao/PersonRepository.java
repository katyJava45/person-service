package telran.java45.person.dao;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import telran.java45.person.model.Person;


public interface PersonRepository extends CrudRepository<Person, Integer> {

}
