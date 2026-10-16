package com.example.service;

import com.example.model.Auteur;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class PerformanceTestService {
    private final EntityManager em;
    public PerformanceTestService(EntityManager em) { this.em = em; }
    public long testPerformanceSansOptimisation() {
        long start = System.nanoTime();
        List<Auteur> auteurs = em.createQuery("SELECT a FROM Auteur a", Auteur.class).getResultList();
        int total = 0;
        for (Auteur auteur : auteurs) total += auteur.getLivres().size();
        return report("Lazy (N+1)", start, auteurs.size(), total);
    }
    public long testPerformanceAvecJoinFetch() {
        long start = System.nanoTime();
        TypedQuery<Auteur> query = em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class);
        return report("JOIN FETCH", start, query.getResultList().size(), 50);
    }
    public long testPerformanceAvecEntityGraph() {
        long start = System.nanoTime();
        EntityGraph<?> graph = em.getEntityGraph("Auteur.livres");
        TypedQuery<Auteur> query = em.createQuery("SELECT a FROM Auteur a", Auteur.class);
        query.setHint("javax.persistence.fetchgraph", graph);
        return report("Entity Graph", start, query.getResultList().size(), 50);
    }
    public long testPerformanceAvecCache() {
        long start = System.nanoTime();
        TypedQuery<Auteur> query = em.createQuery("SELECT a FROM Auteur a", Auteur.class);
        query.setHint("org.hibernate.cacheable", true);
        List<Auteur> auteurs = query.getResultList();
        int total = 0;
        for (Auteur auteur : auteurs) total += auteur.getLivres().size();
        return report("Cache L2 (lecture répétée)", start, auteurs.size(), total);
    }
    private long report(String label, long start, int auteurs, int livres) {
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("%-28s | auteurs=%2d | livres=%2d | durée=%d ms%n", label, auteurs, livres, elapsed);
        return elapsed;
    }
}
