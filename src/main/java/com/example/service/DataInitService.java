package com.example.service;

import com.example.model.Auteur;
import com.example.model.Livre;
import javax.persistence.EntityManager;

public class DataInitService {
    public void initData(EntityManager em) {
        for (int i = 1; i <= 10; i++) {
            Auteur auteur = new Auteur("Auteur " + i);
            for (int j = 1; j <= 5; j++) auteur.ajouterLivre(new Livre("Livre " + j + " de l'auteur " + i));
            em.persist(auteur);
        }
    }
}
