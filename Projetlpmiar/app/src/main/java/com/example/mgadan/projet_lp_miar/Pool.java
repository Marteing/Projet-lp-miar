
package com.example.mgadan.projet_lp_miar;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Pool implements Serializable {

    private int bassinLoisir;
    private String commune;
    private int accesPmrEquipt;
    private String tel;
    private String infosComplementaires;
    private String nomUsuel;
    private String adresse;
    private int solarium;
    private int libreService;
    private int bassinSportif;
    private int bassinApprentissage;
    private String web;
    private int plongeoir;
    private String idobj;
    private String nomComplet;
    private int toboggan;
    private int pataugeoire;
    private int accessibiliteHandicap;
    private String cp;
    private List<Double> location = null;
    private String moyenPaiement;
    private String accesTransportsCommun;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    private Map<String, Integer> information = new HashMap<>();

    public Map<String, Integer> getInformation() {
        return information;
    }

    public void setInformation(Map<String, Integer> information) {
        this.information = information;
    }

    private int rate;

    private boolean isVisited;

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getBassinLoisir() {
        return bassinLoisir;
    }

    public void setBassinLoisir(int bassinLoisir) {
        this.bassinLoisir = bassinLoisir;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public int getAccesPmrEquipt() {
        return accesPmrEquipt;
    }

    public void setAccesPmrEquipt(int accesPmrEquipt) {
        this.accesPmrEquipt = accesPmrEquipt;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getInfosComplementaires() {
        return infosComplementaires;
    }

    public void setInfosComplementaires(String infosComplementaires) {
        this.infosComplementaires = infosComplementaires;
    }

    public String getNomUsuel() {
        return nomUsuel;
    }

    public void setNomUsuel(String nomUsuel) {
        this.nomUsuel = nomUsuel;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getSolarium() {
        return solarium;
    }

    public void setSolarium(int solarium) {
        this.solarium = solarium;
    }

    public int getLibreService() {
        return libreService;
    }

    public void setLibreService(int libreService) {
        this.libreService = libreService;
    }

    public int getBassinSportif() {
        return bassinSportif;
    }

    public void setBassinSportif(int bassinSportif) {
        this.bassinSportif = bassinSportif;
    }

    public int getBassinApprentissage() {
        return bassinApprentissage;
    }

    public void setBassinApprentissage(int bassinApprentissage) {
        this.bassinApprentissage = bassinApprentissage;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public int getPlongeoir() {
        return plongeoir;
    }

    public void setPlongeoir(int plongeoir) {
        this.plongeoir = plongeoir;
    }

    public String getIdobj() {
        return idobj;
    }

    public void setIdobj(String idobj) {
        this.idobj = idobj;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public int getToboggan() {
        return toboggan;
    }

    public void setToboggan(int toboggan) {
        this.toboggan = toboggan;
    }

    public int getPataugeoire() {
        return pataugeoire;
    }

    public void setPataugeoire(int pataugeoire) {
        this.pataugeoire = pataugeoire;
    }

    public int getAccessibiliteHandicap() {
        return accessibiliteHandicap;
    }

    public void setAccessibiliteHandicap(int accessibiliteHandicap) {
        this.accessibiliteHandicap = accessibiliteHandicap;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public String getMoyenPaiement() {
        return moyenPaiement;
    }

    public void setMoyenPaiement(String moyenPaiement) {
        this.moyenPaiement = moyenPaiement;
    }

    public String getAccesTransportsCommun() {
        return accesTransportsCommun;
    }

    public void setAccesTransportsCommun(String accesTransportsCommun) {
        this.accesTransportsCommun = accesTransportsCommun;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return this.nomUsuel;
    }


}
