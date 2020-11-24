package com.qa.ims.persistence.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qa.ims.persistence.domain.Customer;
import com.qa.ims.utils.DBUtils;

public class CustomerDAO implements Dao<Customer> {

	public static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Reads all customers from the database
	 * 
	 * @return A list of customers
	 */
	@Override
	public List<Customer> readAll() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("select * from customers");) {
			List<Customer> customers = new ArrayList<>();
			while (resultSet.next()) {
				customers.add(modelFromResultSet(resultSet));
			}
			return customers;
		} catch (SQLException e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return new ArrayList<>();
	}

	/**
	 * Fetches newest <code>Customer</code> entity from data source.
	 * 
	 * @return <code>Customer</code> entity or <code>null</code>
	 */
	public Customer readLatest() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM customers ORDER BY customer_id DESC LIMIT 1");) {
			resultSet.next();
			return modelFromResultSet(resultSet);
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Creates a customer in the database.
	 * 
	 * @param customer - takes in a customer object. id will be ignored
	 * @return New customer entity or <code>null</code>
	 */
	@Override
	public Customer create(Customer customer) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();) {
			statement.executeUpdate(String.format("INSERT INTO customers(first_name, surname) values('%s', '%s')",
					customer.getFirstName(), customer.getSurname()));
			return readLatest();
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 
	 * @param id - The id of the <code>Customer</code> to fetch from the data source
	 * @return The matching <code>Customer</code> entity or <code>null</code>
	 */
	public Customer readCustomer(Long id) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM customers where customer_id = " + id);) {
			resultSet.next();
			return modelFromResultSet(resultSet);
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Updates a customer in the database.
	 * 
	 * @param customer - takes in a <code>Customer</code> object, the id field will
	 *                 be used to update that customer in the database
	 * @return updated <code>Customer</code> entity or <strong>null</strong>
	 */
	@Override
	public Customer update(Customer customer) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();) {
			statement.executeUpdate(String.format("update customers set first_name = '%s', surname = '%s' where customer_id = %d",
					customer.getFirstName(), customer.getSurname(), customer.getId()));
					
			return readCustomer(customer.getId());
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Deletes a customer in the database.
	 * 
	 * @param id - id of the customer
	 */
	@Override
	public int delete(long id) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();) {
			return statement.executeUpdate("delete from customers where customer_id = " + id);
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return 0;
	}
	
	/**
	 * Converts a <code>ResultSet</code> into a <code>Customer</code> entity.
	 * 
	 * @param resultSet The <code>ResultSet</code> returned by a JDBC query.
	 * @return <code>Customer</code> entity.
	 */
	@Override
	public Customer modelFromResultSet(ResultSet resultSet) throws SQLException {
		Long id = resultSet.getLong("customer_id");
		String firstName = resultSet.getString("first_name");
		String surname = resultSet.getString("surname");
		return new Customer(id, firstName, surname);
	}

}
