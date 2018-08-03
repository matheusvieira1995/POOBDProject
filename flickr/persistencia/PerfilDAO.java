package flickr.persistencia;

import java.sql.*;
import java.util.ArrayList;

import flickr.exception.EntityNotFoundException;
import flickr.exception.EntityTransientException;
import flickr.exception.PerfilInvalidoException;
import flickr.modelo.*;

public class PerfilDAO extends AbstractDAO<Perfil> {
	@Override
	public void save(Perfil p) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Perfil\" (login, senha, nome, email, data_Nascimento, descricao, genero, data_hora_criacao, data_hora_atualizacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			cmd.setString(1, p.getLogin());
			cmd.setString(2, p.getSenha());
			cmd.setString(3, p.getNome());
			cmd.setString(4, p.getEmail());
			cmd.setDate(5, java.sql.Date.valueOf(p.getDataNascimento()));
			cmd.setString(6, p.getDescricao());
			cmd.setInt(7, p.getGenero().ordinal());
			cmd.setTimestamp(8, Timestamp.valueOf(p.getDateTimeCreation()));
			cmd.setTimestamp(9, Timestamp.valueOf(p.getDateTimeLastUpdate()));
			cmd.execute();
			ResultSet rs1 = cmd.getGeneratedKeys();
			if (rs1.next()) {
				p.setCodigoPerfil(rs1.getInt(1));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Perfil load(int codigoPerfil) throws EntityNotFoundException {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Perfil\" WHERE codigo_perfil = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoPerfil);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Perfil p = new Perfil(rs.getString("login"), rs.getString("senha"), rs.getString("nome"),
						rs.getString("email"), rs.getDate("data_nascimento").toLocalDate(),
						Genero.values()[rs.getInt("genero")], rs.getString("descricao"));
				p.setCodigoPerfil(rs.getInt("codigo_perfil"));
				return p;
			} else {
				throw new EntityNotFoundException();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Perfil find(int codigoPerfil) {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Perfil\" WHERE codigo_perfil = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoPerfil);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Perfil p = new Perfil(rs.getString("login"), rs.getString("senha"), rs.getString("nome"),
						rs.getString("email"), rs.getDate("data_nascimento").toLocalDate(),
						Genero.values()[rs.getInt("genero")], rs.getString("descricao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				p.setCodigoPerfil(rs.getInt("codigo_perfil"));
				return p;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public ArrayList<Perfil> loadAll() {
		ArrayList<Perfil> perfis = new ArrayList<Perfil>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Perfil\"";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Perfil p = new Perfil(rs.getString("login"), rs.getString("senha"), rs.getString("nome"),
						rs.getString("email"), rs.getDate("data_nascimento").toLocalDate(),
						Genero.values()[rs.getInt("genero")], rs.getString("descricao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				p.setCodigoPerfil(rs.getInt("codigo_perfil"));
				perfis.add(p);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return perfis;
	}

	@Override
	public ArrayList<Perfil> loadPage(int offset, int limit) {
		ArrayList<Perfil> perfis = new ArrayList<Perfil>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Perfil\"";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			int c = 1;
			int c2 = 0;
			while (rs.next()) {
				c2++;
			}
			if (c2 > offset) {
				while (rs.next() || c == limit) {
					Perfil p = new Perfil(rs.getString("login"), rs.getString("senha"), rs.getString("nome"),
							rs.getString("email"), rs.getDate("data_nascimento").toLocalDate(),
							Genero.values()[rs.getInt("genero")], rs.getString("descricao"),
							rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
							rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
					p.setCodigoPerfil(rs.getInt("codigo_perfil"));
					perfis.add(p);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return perfis;
	}

	@Override
	public void delete(Perfil p) throws EntityTransientException {
		if (load(p.getId()) != null) {
			try (Connection con = openConnection()) {
				String sql = "DELETE FROM \"Perfil\" WHERE login = ?";
				PreparedStatement cmd = con.prepareStatement(sql);
				cmd.setString(1, p.getLogin());
				cmd.execute();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new EntityTransientException();
		}
	}

	public Perfil login(String login, String senha) throws EntityNotFoundException {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Perfil\" WHERE login = ? AND senha = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setString(1, login);
			cmd.setString(2, senha);

			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Perfil p = load(login);
				return p;
			} else {
				try {
					throw new PerfilInvalidoException();
				} catch (PerfilInvalidoException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public Perfil load(String login) {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Perfil\" WHERE login = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setString(1, login);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Perfil p = new Perfil(login, rs.getString("senha"), rs.getString("nome"), rs.getString("email"),
						rs.getDate("data_nascimento").toLocalDate(), Genero.values()[rs.getInt("genero")],
						rs.getString("descricao"), rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				p.setCodigoPerfil(rs.getInt("codigo_perfil"));
				return p;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public void update(Perfil p) {
		try (Connection con = openConnection()) {
			String sql = "UPDATE \"Perfil\" SET senha = ?, nome = ?, data_nascimento = ?, genero = ?, descricao = ?, data_hora_atualizacao = now() WHERE login = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setString(1, p.getSenha());
			cmd.setString(2, p.getNome());
			cmd.setDate(3, java.sql.Date.valueOf(p.getDataNascimento()));
			cmd.setInt(4, p.getGenero().ordinal());
			cmd.setString(5, p.getDescricao());
			cmd.setString(6, p.getLogin());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void insertSeguir(Perfil perfilSeguidor, Perfil perfilSeguido) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Segue\" (codigo_perfil_seguidor, codigo_perfil_seguido) VALUES (?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql);

			cmd.setInt(1, perfilSeguidor.getId());
			cmd.setInt(2, perfilSeguido.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean segue(Perfil seguidor, Perfil seguido) {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Segue\" WHERE codigo_perfil_seguidor = ? AND codigo_perfil_seguido = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, seguidor.getId());
			cmd.setInt(2, seguido.getId());

			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public void deleteSeguir(Perfil perfilSeguidor, Perfil perfilSeguido) {
		try (Connection con = openConnection()) {
			String sql = "DELETE FROM \"Segue\" WHERE codigo_perfil_seguidor = ? AND codigo_perfil_seguido = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, perfilSeguidor.getId());
			cmd.setInt(2, perfilSeguido.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void visualiza(Foto f, Perfil p) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Visualiza\" (codigo_foto, codigo_perfil_visualizador, data_hora_criacao) VALUES (? ,? , now())";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, f.getId());
			cmd.setInt(2, p.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void favoritar(Perfil p, Foto f) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Favorita\" (codigo_foto, codigo_perfil) VALUES (?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, f.getId());
			cmd.setInt(2, p.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void desfavoritar(Perfil p, Foto f) {
		try (Connection con = openConnection()) {
			String sql = "DELETE FROM \"Favorita\" WHERE codigo_perfil = ? AND codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, p.getId());
			cmd.setInt(2, f.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public ArrayList<Foto> selectFavoritos(Perfil p) {
		FotoDAO fotoDAO = new FotoDAO();
		ArrayList<Foto> fotos = new ArrayList<Foto>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Favorita\" WHERE codigo_perfil = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, p.getId());
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Foto f = fotoDAO.load(rs.getInt("codigo_foto"));
				fotos.add(f);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return fotos;
	}

	public ArrayList<Foto> selectGaleria(Perfil p) {
		ArrayList<Foto> galeria = new ArrayList<Foto>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM galeria WHERE codigo_perfil = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, p.getId());
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Foto f = new Foto(rs.getString("arquivo"), p, rs.getString("descricao"),
						Privacidade.values()[rs.getInt("privacidade")], rs.getString("camera"),
						rs.getString("resolucao"), rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				f.setCodigoFoto(rs.getInt("codigo_foto"));
				galeria.add(f);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return galeria;
	}
}
