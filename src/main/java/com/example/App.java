package com.example;

import com.example.service.DataInitService;
import com.example.service.PerformanceTestService;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        System.out.println("=== TP 7 : Hibernate, cache L2 et optimisation N+1 ===");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-cache-pu");
        try {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            new DataInitService().initData(em);
            em.getTransaction().commit();
            em.clear();
            PerformanceTestService tests = new PerformanceTestService(em);
            System.out.println("Données prêtes : 10 auteurs et 50 livres.");
            System.out.println("--- Comparaison des chargements ---");
            tests.testPerformanceSansOptimisation();
            em.clear();
            tests.testPerformanceAvecJoinFetch();
            em.clear();
            tests.testPerformanceAvecEntityGraph();
            em.clear();
            Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
            emf.getCache().evictAll();
            stats.clear();
            System.out.println("--- Cache de second niveau (entité répétée) ---");
            em.getTransaction().begin();
            em.find(com.example.model.Auteur.class, 1L);
            em.getTransaction().commit();
            em.clear();
            em.getTransaction().begin();
            em.find(com.example.model.Auteur.class, 1L);
            em.getTransaction().commit();
            System.out.printf("Cache L2 : hits=%d, misses=%d%n", stats.getSecondLevelCacheHitCount(), stats.getSecondLevelCacheMissCount());
            System.out.println("=== Exécution terminée ===");
            em.close();
        } finally { emf.close(); }
    }
}
