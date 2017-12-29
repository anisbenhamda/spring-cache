package org.springframework.democache.tests;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.democache.model.Parametres;
import org.springframework.democache.model.Reponse;
import org.springframework.democache.service.ServiceCacher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-application-context.xml"})
public class ServiceCacheTest {

    public static final Logger logger = LoggerFactory.getLogger(ServiceCacheTest.class);

    
    
    @Autowired
    private ServiceCacher service;

    @Test
    public void testCache() {
    	//Ex�cution de la m�thode mise en cache de r�sultat
    	logger.info("Appel 1");
    	Reponse rep=service.getDatas(new Parametres(10L,"param1","param2"));
        assertThat(rep).isNotNull();
        
      //La valeur de cache est retourn� sans ex�cuter la m�thode
        logger.info("Appel 2");
        Reponse rep2=service.getDatas(new Parametres(10L,"param1","param2"));
        assertThat(rep2).isNotNull();
        assertThat(rep).isSameAs(rep2);
    }

    @Test
    public void testCacheCondition() {
    	//La mise en cache est conditionn� par param1>100, donc pas de cache dans ce cas
    	logger.info("Appel 1");
    	Reponse rep1=service.getDatasWithCondition(new Parametres(90L,"param12","param22"));
        assertThat(rep1).isNotNull();
        logger.info("Appel 2");
        Reponse rep2=service.getDatasWithCondition(new Parametres(90L,"param12","param22"));
        assertThat(rep2).isNotNull();
        assertThat(rep1).isNotSameAs(rep2);
        assertThat(rep1).isEqualTo(rep2);

        
        //la condition de cache est bien v�rifi�
        logger.info("Appel 3");
        Reponse rep3=service.getDatasWithCondition(new Parametres(110L,"param13","param23"));
        assertThat(rep3).isNotNull();
        logger.info("Appel 4");
        Reponse rep4=service.getDatasWithCondition(new Parametres(110L,"param13","param23"));
        assertThat(rep4).isNotNull();
        assertThat(rep3).isSameAs(rep4);
    }
    
    @Test
    public void testCacheKey() {
    	/*La cl� de cache est calcul� sur la base de l'attribut param1,
    	dans ce cas la m�thode est mise en cache malgr� le changement des attributs param2 et param3*/
    	logger.info("Appel 1");
    	Reponse rep1=service.getDatasWithKey(new Parametres(90L,"param12","param22"));
        assertThat(rep1).isNotNull();
        logger.info("Appel 2");
        Reponse rep2=service.getDatasWithKey(new Parametres(90L,"param123","param223"));
        assertThat(rep2).isNotNull();
        assertThat(rep1).isSameAs(rep2);
        
        //Pas de cache,la valeur de la cl� est chang�
        logger.info("Appel 3");
        Reponse rep3=service.getDatasWithKey(new Parametres(110L,"param13","param23"));
        assertThat(rep3).isNotNull();
        logger.info("Appel 4");
        Reponse rep4=service.getDatasWithKey(new Parametres(30L,"param13","param23"));
        assertThat(rep4).isNotNull();
        assertThat(rep3).isNotSameAs(rep4);
        assertThat(rep3).isNotEqualTo(rep4);
    }
   
    @Test
    public void testCacheEvict() {
    	//Mise en cache
    	logger.info("Appel 1");
    	Reponse rep1=service.getDatas(new Parametres(90L,"param1","param2"));
        assertThat(rep1).isNotNull();
        logger.info("Appel 2");
        Reponse rep2=service.getDatas(new Parametres(90L,"param1","param2"));
        assertThat(rep2).isNotNull();
        assertThat(rep1).isSameAs(rep2);
        
        //Cache vid� 
        service.viderCacheData();
        
        //Ex�cution de la m�thode
        logger.info("Appel 3");
        Reponse rep3=service.getDatas(new Parametres(90L,"param1","param2"));
        assertThat(rep3).isNotNull();
        assertThat(rep3).isNotSameAs(rep2);
        assertThat(rep3).isEqualTo(rep2);
    }
    
    @Test
    public void testCachePut() {
    	//Mise en cache
    	logger.info("Appel 1");
    	Reponse rep1=service.getDatas(new Parametres(90L,"param1","param2"));
        assertThat(rep1).isNotNull();
        logger.info("Appel 2");
        Reponse rep2=service.getDatas(new Parametres(90L,"param1","param2"));
        assertThat(rep2).isNotNull();
        assertThat(rep1).isSameAs(rep2);
        
        //Forcer la mise � jour de cache, donc re-ex�cution de la m�thode      
        logger.info("Appel 3");
        Reponse rep3=service.getDatasWithPut(new Parametres(90L,"param1","param2"));
        assertThat(rep3).isNotNull();
        assertThat(rep3).isNotSameAs(rep2);
        assertThat(rep3).isEqualTo(rep2);
    }
}
