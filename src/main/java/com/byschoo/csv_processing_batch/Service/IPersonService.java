package com.byschoo.csv_processing_batch.Service;

import java.util.List;

import com.byschoo.csv_processing_batch.Entities.Person;


public interface IPersonService {

    //  1. Definición del método para guardar una lista de personas
    public void saveAllPersons(List<Person> personList);
    //  1.1. Este método recibe una lista de objetos Person y los guarda en la base de datos.
}
