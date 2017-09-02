package com.horizon.admin.config.persistence;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.horizon.dao" })
@PropertySource("classpath:/database.properties")
public class PersistencceConfig {

	@Autowired
	private Environment env;

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		String KEY_DRIVER_CLASS = env.getProperty("KEY_DRIVER_CLASS");
		String KEY_DB_URL = env.getProperty("KEY_DB_URL");
		String KEY_DB_USERNAME = env.getProperty("KEY_DB_USERNAME");
		String KEY_DB_PASSWORD = env.getProperty("KEY_DB_PASSWORD");

		BasicDataSource basic = new BasicDataSource();
		basic.setDriverClassName(KEY_DRIVER_CLASS);
		basic.setUrl(KEY_DB_URL);
		basic.setUsername(KEY_DB_USERNAME);
		basic.setPassword(KEY_DB_PASSWORD);

		return basic;
	}

	private Properties getProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		properties.put("hibernate.show_sql", false);
		properties.put("hibernate.bytecode.use_reflection_optimizer", false);
		properties.put("hibernate.check_nullability", false);
		properties.put("hibernate.search.autoregister_listeners", false);
		return properties;
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		return entityManagerFactoryBean().getObject();
	}
	
	@Bean
	public EntityManager entityManager() {
	    return entityManagerFactoryBean().getObject().createEntityManager();
	}
	
	

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
		adapter.setGenerateDdl(true);
		adapter.setShowSql(false);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

		factory.setJpaVendorAdapter(adapter);
		factory.setDataSource(this.dataSource());
		factory.setPackagesToScan(new String[] { "com.horizon.entity" });
		factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
		factory.setValidationMode(ValidationMode.NONE);
		factory.setJpaProperties(getProperties());
		return factory;
	}
	

	@Bean(name="transactionManager")
	public PlatformTransactionManager jpaTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(this.entityManagerFactory());
		return transactionManager;
	}
}
