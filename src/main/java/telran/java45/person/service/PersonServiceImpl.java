package telran.java45.person.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.tomcat.jni.Address;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction.COUNT;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java45.person.dao.PersonRepository;
import telran.java45.person.dto.AddressDto;
import telran.java45.person.dto.CityPopulationDto;
import telran.java45.person.dto.PersonDto;
import telran.java45.person.dto.exceptions.PersonNotFoundException;
import telran.java45.person.model.Person;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

	final PersonRepository personRepository;
	final ModelMapper modelMapper;
	
	@Override
	@Transactional
	public Boolean addPerson(PersonDto personDto) {
		if(personRepository.existsById(personDto.getId())) {
			return false;
		}
		personRepository.save(modelMapper.map(personDto, Person.class));
		return true;
	}

	@Override
	public PersonDto findPersonById(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		return modelMapper.map(person, PersonDto.class);

	}

	@Override
	public PersonDto removePerson(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		personRepository.delete(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	public PersonDto updatePersonName(Integer id, String name) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		if(name!=null) {
			person.setName(name);
		}
		personRepository.save(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	public PersonDto updateAddress(Integer id, AddressDto addressDto) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		
		if(addressDto.getCity()!=null) {
			person.getAddress().setCity(addressDto.getCity());
		}
		if(addressDto.getStreet()!=null) {
			person.getAddress().setStreet(addressDto.getStreet());
		}
		if(addressDto.getBuilding()!=null) {
			person.getAddress().setBuilding(addressDto.getBuilding());
		}
		personRepository.save(person);
		
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	public Iterable<PersonDto> findPersonsByCity(String city) {

		return StreamSupport.stream(personRepository.findAll().spliterator(), false)
				.filter(p -> p.getAddress().getCity().equalsIgnoreCase(city))
				.map(p -> modelMapper.map(p, PersonDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<PersonDto> findPersonsByName(String name) {
		return StreamSupport.stream(personRepository.findAll().spliterator(), false)
				.filter(p -> p.getName().equalsIgnoreCase(name))
				.map(p -> modelMapper.map(p, PersonDto.class))
				.collect(Collectors.toList());
				
	}

	@Override
	public Iterable<PersonDto> findPersonsBetweenAges(Integer minAge, Integer maxAge) {
		LocalDate date = LocalDate.now();
		return StreamSupport.stream(personRepository.findAll().spliterator(), false)
				.filter(p -> p.getBirthDate().isBefore(date.minusYears(minAge))
						&& p.getBirthDate().isAfter(date.minusYears(maxAge)))
				.map(p -> modelMapper.map(p, PersonDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<CityPopulationDto> getCityPopulation() {
		Long count = (long) 0;
		return (Iterable<CityPopulationDto>) StreamSupport.stream(personRepository.findAll().spliterator(), false)
				.map(p -> new CityPopulationDto(p.getAddress().getCity(), count+1))
				.collect(Collectors.toList());
	}

}
