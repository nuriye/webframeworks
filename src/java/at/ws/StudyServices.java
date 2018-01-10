/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ws;

import at.database.HibernateUtil;
import at.database.Person;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author PU
 */
@WebService(serviceName = "StudyServices")
public class StudyServices {


    /**
     * Web service operation
     */
    @WebMethod(operationName = "login")
    public OutputPayloadLogin login(@WebParam(name = "parameter") InputPayloadLogin parameter) {
        
        OutputPayloadLogin opl = new OutputPayloadLogin();        
        
        //Hibernate 
        
        SessionFactory sf = HibernateUtil.getSessionFactory();  //Initialisierung der SessionFactory
        Session s = sf.openSession();                           //Öffne eine Session 
        Transaction tx = null;
        
        try{
            
            tx = s.beginTransaction();                          //Beginne Transaktion
            String hql = "FROM Person P WHERE P.username = :name";  //HQL Query um Person zu suchen
            Query query = s.createQuery(hql);                   //HQL Query zuweisen
            query.setParameter("name",parameter.getUsername()); //Wert für den namen einfügen (gegen SQL Injection!)
            List results = query.list();                        //Abfrage durchführen
               
            /*
            select P, C from Person.p join Course.c on 
            
            select P from PersonCourseMembership as pcm join pcm.person P
            //bekomme ich Personen

            */
            Person personFromDb = (Person)results.get(0);       //Resultat in person casten
            
            if(personFromDb.getPassword().equals(parameter.getPassword())){             //Überprüfen des Passworts und entsprechend Response mit Sccuess/Failure info befüllen
                opl.setFehlerbeschreibung("Success!");
                opl.setUserid(personFromDb.getPersonPk());
                opl.setRolle(personFromDb.getRole());  
            }else{
                opl.setFehlerbeschreibung("Failure!");
            }
            
            tx.commit();            //Transaktion durchführen
        } catch (Exception e) {
        
            if(tx !=null){
                tx.rollback();      //Bei Fehlerfall => Rollback!
            }
        } finally {
            s.close();              //Session schließen egal ob Erfolg oder Fehler
        }
        
        //Hibernate
        
        return opl;
    }
    
    
}
