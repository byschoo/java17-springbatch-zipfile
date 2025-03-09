package com.byschoo.csv_processing_batch.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byschoo.csv_processing_batch.Entities.Person;
import com.byschoo.csv_processing_batch.Repository.IPersonDAO;

@Service
public class PersonServiceImpl implements IPersonService {

    @Autowired
    private IPersonDAO personDAO;

    @Override
    @Transactional
    public void saveAllPersons(List<Person> personList) {
        //  1. Implementación del método para guardar una lista de personas
        //  1.1. Delegación de la lógica de guardado al DAO
        personDAO.saveAll(personList);
    }
}
