package flickr.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import flickr.modelo.IEntity;

public abstract class AbstractDAO<T extends IEntity> implements IDAO<T> {
	private final String url = "jdbc:postgresql://localhost:5432/Flickr";
	private final String usuario = "postgres";
	private final String senha = "postgres";
	
	protected Connection con;

	protected Connection openConnection() throws SQLException {
		con = DriverManager.getConnection(url, usuario, senha);
		return con;
	}
}
