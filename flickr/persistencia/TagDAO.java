package flickr.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import flickr.exception.EntityNotFoundException;
import flickr.exception.EntityTransientException;
import flickr.modelo.*;

public class TagDAO extends AbstractDAO<Tag> {
	FotoDAO fotoDAO = new FotoDAO();
	PerfilDAO perfilDAO = new PerfilDAO();

	@Override
	public void save(Tag t) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Tag\" (nome, codigo_perfil_criador, data_hora_criacao, data_hora_atualizacao) VALUES (?, ?, ?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			cmd.setString(1, t.getNome());
			cmd.setInt(2, t.getPerfil().getId());
			cmd.setTimestamp(3, Timestamp.valueOf(t.getDateTimeCreation()));
			cmd.setTimestamp(4, Timestamp.valueOf(t.getDateTimeLastUpdate()));
			cmd.execute();
			ResultSet rs1 = cmd.getGeneratedKeys();
			if (rs1.next()) {
				t.setCodigoTag(rs1.getInt(1));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Tag load(int codigoTag) throws EntityNotFoundException {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Tag\" WHERE codigo_tag = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoTag);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Tag t = new Tag(rs.getString("nome"), perfilDAO.load(rs.getInt("codigo_perfil_criador")),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				t.setCodigoTag(rs.getInt("codigo_tag"));
				return t;
			} else {
				throw new EntityNotFoundException();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Tag find(int codigoTag) {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Tag\" WHERE codigo_tag = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoTag);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Tag t = new Tag(rs.getString("nome"), fotoDAO.load(rs.getInt("codigo_tag")),
						perfilDAO.load(rs.getInt("codigo_perfil_criador")),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				t.setCodigoTag(rs.getInt("codigo_tag"));
				return t;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public List<Tag> loadAll() {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Tag\"";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Tag t = new Tag(rs.getString("nome"), perfilDAO.load(rs.getInt("codigo_perfil_criador")),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				t.setCodigoTag((rs.getInt("codigo_tag")));
				tags.add(t);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return tags;
	}

	@Override
	public List<Tag> loadPage(int offset, int limit) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Tag\" ";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			int c1 = 1;
			int c2 = 0;
			while (rs.next()) {
				c2++;
			}
			if (c2 > offset) {
				while (rs.next() || c1 == limit) {
					Tag t = new Tag(rs.getString("nome"), perfilDAO.load(rs.getInt("codigo_perfil_criador")),
							rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
							rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
					t.setCodigoTag((rs.getInt("codigo_tag")));
					tags.add(t);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return tags;
	}

	@Override
	public void delete(Tag t) throws EntityTransientException {
		if (load(t.getId()) != null) {
			try (Connection con = openConnection()) {
				String sql = "DELETE FROM \"Tag\" WHERE nome_tag = ?";
				PreparedStatement cmd = con.prepareStatement(sql);
				cmd.setString(1, t.getNome());
				cmd.execute();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new EntityTransientException();
		}
	}

	public Tag selectNome(String nome) {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Tag\" WHERE nome = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setString(1, nome);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Tag t = new Tag(nome, perfilDAO.load(rs.getInt("codigo_perfil_criador")),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				t.setCodigoTag((rs.getInt("codigo_tag")));
				return t;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public void removerTag(Foto f, Perfil p, String nome) {
		try (Connection con = openConnection()) {
			Tag t = selectNome(nome);
			String sql = "DELETE FROM \"TagsDaFoto\" tf USING \"Tag\" t WHERE (tf.codigo_tag=t.codigo_tag) AND tf.codigo_tag = ? AND tf.codigo_foto = ? AND t.codigo_perfil_criador = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, t.getId());
			cmd.setInt(2, f.getId());
			cmd.setInt(3, p.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void removerTagsDaFoto(Foto f) {
		try (Connection con = openConnection()) {
			String sql = "DELETE FROM \"TagsDaFoto\" tf USING \"Tag\" t WHERE t.codigo_tag = tf.codigo_tag AND tf.codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, f.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public ArrayList<Tag> selectTagsDaFoto(Foto f) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"TagsDaFoto\" tf INNER JOIN \"Tag\" t ON t.codigo_tag = tf.codigo_tag WHERE tf.codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, f.getId());
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Tag t1 = new Tag(rs.getString("nome"), fotoDAO.load(rs.getInt("codigo_foto")),
						perfilDAO.load(rs.getInt("codigo_perfil_criador")),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				t1.setCodigoTag(rs.getInt("codigo_tag"));
				tags.add(t1);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return tags;
	}

	public void addTagsDaFoto(Tag t, Foto foto) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"TagsDaFoto\" (codigo_foto, codigo_tag) VALUES (?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, foto.getId());
			cmd.setInt(2, t.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
