
package com.example.mgadan.projet_lp_miar;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Pool implements Serializable {

    private int position;

    private String commune;
    private String tel;
    private String infosComplementaires;
    private String nomUsuel;
    private String adresse;
    private String web;
    private String idobj;
    private String nomComplet;
    private String cp;
    private List<Double> location = null;
    private String moyenPaiement;
    private String accesTransportsCommun;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private transient double  distanceBetweenUserAndPool = 0.00;

    public double getDistanceBetweenUserAndPool() {
        return distanceBetweenUserAndPool;
    }

    public void setDistanceBetweenUserAndPool(double distanceBetweenUserAndPool) {
        this.distanceBetweenUserAndPool = distanceBetweenUserAndPool;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

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


    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
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

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
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
