package com.example.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auteurs")
@NamedEntityGraph(name = "Auteur.livres", attributeNodes = @NamedAttributeNode("livres"))
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Auteur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nom;
    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Livre> livres = new ArrayList<>();

    protected Auteur() { }
    public Auteur(String nom) { this.nom = nom; }
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public List<Livre> getLivres() { return livres; }
    public void ajouterLivre(Livre livre) { livres.add(livre); livre.setAuteur(this); }
    public void retirerLivre(Livre livre) { livres.remove(livre); livre.setAuteur(null); }
}
