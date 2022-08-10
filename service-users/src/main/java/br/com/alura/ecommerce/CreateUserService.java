package br.com.alura.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService {

	private final Connection connection;

	private String sql = """
			CREATE TABLE USERS (
			  UUID VARCHAR(200) PRIMARY KEY,
			  EMAIL VARCHAR(200)
			)
			""";

	public CreateUserService() throws SQLException {
		String url = "jdbc:sqlite:target/users_database.db";
		this.connection = DriverManager.getConnection(url);

		try {
			// criar tabela
			this.connection.createStatement().execute(sql);
		} catch (SQLException ex) {
			//be careful, the sql could be wrong, be really careful
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException {
		var createUserService = new CreateUserService();
		try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER",
				createUserService::parse, Order.class, Map.of())) {
			service.run();
		}
	}

	private void parse(ConsumerRecord<String, Order> record) throws SQLException {
		System.out.println("------------------------------------------");
		System.out.println("Processing new order, checking for new user");
		System.out.println(record.value());

		var order = record.value();

		if (isNewUser(order.getEmail())) {
			insertNewUser(order.getEmail());
		}
	}

	private void insertNewUser(String email) throws SQLException {
		var insert = this.connection.prepareStatement("INSERT INTO USERS (UUID, EMAIL) VALUES (?, ?)");
		insert.setString(1, UUID.randomUUID().toString());
		insert.setString(2, email);
		insert.execute();
		System.out.println("Usuario uuid e " + email + "adicionado");
	}

	private boolean isNewUser(Object email) throws SQLException {
		var exists = this.connection.prepareStatement("SELECT UUID FROM USERS WHERE EMAIL = ? LIMIT 1");
		exists.setString(1, "email");
		
		var results = exists.executeQuery();
		return !results.next(); //se vai para a proxima consulta/linha, eh pq nao eh um usuario novo
	}
}
