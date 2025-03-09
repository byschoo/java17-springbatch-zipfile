package com.byschoo.csv_processing_batch.Repository;

import org.springframework.data.repository.CrudRepository;

import com.byschoo.csv_processing_batch.Entities.Person;

public interface IPersonDAO extends CrudRepository<Person,Long> {

}
