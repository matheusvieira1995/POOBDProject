package flickr.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import flickr.exception.EntityNotFoundException;
import flickr.exception.EntityTransientException;
import flickr.modelo.*;

public class ComentarioDAO extends AbstractDAO<Comentario> {
	PerfilDAO perfilDAO = new PerfilDAO();
	FotoDAO fotoDAO = new FotoDAO();

	@Override
	public void save(Comentario c) {
		Perfil p = perfilDAO.load(c.getPerfil().getLogin());
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Comentario\" (codigo_perfil, codigo_foto, descricao, data_hora_criacao, data_hora_atualizacao) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			cmd.setInt(1, p.getId());
			cmd.setInt(2, c.getFoto().getId());
			cmd.setString(3, c.getDescricao());
			cmd.setTimestamp(4, Timestamp.valueOf(c.getDateTimeCreation()));
			cmd.setTimestamp(5, Timestamp.valueOf(c.getDateTimeLastUpdate()));
			cmd.execute();
			ResultSet rs1 = cmd.getGeneratedKeys();
			if (rs1.next()) {
				c.setCodigoComentario(rs1.getInt(1));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Comentario load(int codigo_comentario) throws EntityNotFoundException {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Comentario\" WHERE codigo_comentario = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigo_comentario);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Comentario c2 = new Comentario(perfilDAO.load(rs.getInt("codigo_perfil")),
						fotoDAO.load(rs.getInt("codigo_foto")), rs.getString("descricao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				return c2;
			} else {
				throw new EntityNotFoundException();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Comentario find(int codigo_comentario) {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Comentario\" WHERE codigo_comentario = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigo_comentario);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Comentario c2 = new Comentario(perfilDAO.load(rs.getInt("codigo_perfil")),
						fotoDAO.load(rs.getInt("codigo_foto")), rs.getString("descricao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				return c2;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public ArrayList<Comentario> loadAll() {
		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Comentario\"";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Comentario c = new Comentario(perfilDAO.load(rs.getInt("codigo_perfil")),
						fotoDAO.load(rs.getInt("codigo_foto")), rs.getString("descricao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				c.setCodigoComentario((rs.getInt("codigo_comentario")));
				comentarios.add(c);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return comentarios;
	}

	@Override
	public ArrayList<Comentario> loadPage(int offset, int limit) {
		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Comentario\"";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			int c1 = 1;
			int c2 = 0;
			while (rs.next()) {
				c2++;
			}
			if (c2 > offset) {
				while (rs.next() || c1 == limit) {
					Comentario c = new Comentario(perfilDAO.load(rs.getInt("codigo_perfil")),
							fotoDAO.load(rs.getInt("codigo_foto")), rs.getString("descricao"),
							rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
							rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
					c.setCodigoComentario((rs.getInt("codigo_comentario")));
					comentarios.add(c);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return comentarios;
	}

	@Override
	public void delete(Comentario c) throws EntityTransientException {
		if (load(c.getId()) != null) {
			try (Connection con = openConnection()) {
				String sql = "DELETE FROM \"Comentario\" WHERE login = ?";
				PreparedStatement cmd = con.prepareStatement(sql);
				cmd.setInt(1, c.getId());
				cmd.execute();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new EntityTransientException();
		}
	}

	public ArrayList<Comentario> selectComentariosDaFoto(Foto f) {
		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Comentario\" WHERE codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, f.getId());
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Comentario c = new Comentario(perfilDAO.load(rs.getInt("codigo_perfil")),
						fotoDAO.load(rs.getInt("codigo_foto")), rs.getString("descricao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				c.setCodigoComentario((rs.getInt("codigo_comentario")));
				comentarios.add(c);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return comentarios;
	}
}
