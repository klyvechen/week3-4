/*
 * This class not only produces the global org.hibernate.SessionFactory reference in its static initializer;
 * it also hides the fact that it uses a static singleton
*/

package org.test.hibernate.util;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
    	Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
    	ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = cfg.buildSessionFactory(registry);
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}