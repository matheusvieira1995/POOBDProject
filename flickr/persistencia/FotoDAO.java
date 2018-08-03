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

public class FotoDAO extends AbstractDAO<Foto> {
	PerfilDAO perfilDAO = new PerfilDAO();

	@Override
	public void save(Foto f) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Foto\" (arquivo, codigo_perfil, descricao, privacidade, data_hora_criacao, data_hora_atualizacao, camera, resolucao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			cmd.setString(1, f.getArquivo());
			cmd.setInt(2, f.getPerfil().getId());
			cmd.setString(3, f.getDescricao());
			cmd.setInt(4, f.getPrivacidade().ordinal());
			cmd.setTimestamp(5, Timestamp.valueOf(f.getDateTimeCreation()));
			cmd.setTimestamp(6, Timestamp.valueOf(f.getDateTimeLastUpdate()));
			cmd.setString(7, f.getCamera());
			cmd.setString(8, f.getResolucao());
			cmd.execute();
			ResultSet rs1 = cmd.getGeneratedKeys();
			if (rs1.next()) {
				f.setCodigoFoto(rs1.getInt(1));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Foto load(int codigoFoto) throws EntityNotFoundException {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Foto\" WHERE codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoFoto);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Foto f = new Foto(
						rs.getString("arquivo"),
						perfilDAO.load(rs.getInt("codigo_perfil")),
						rs.getString("descricao"),
						Privacidade.values()[rs.getInt("privacidade")],
						rs.getString("camera"),
						rs.getString("resolucao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				f.setCodigoFoto(rs.getInt("codigo_foto"));
				return f;
			} else {
				throw new EntityNotFoundException();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Foto find(int codigoFoto) {
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Foto\" WHERE codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoFoto);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				Foto f = new Foto(rs.getString("arquivo"), perfilDAO.load(rs.getInt("codigo_perfil")),
						rs.getString("descricao"), Privacidade.values()[rs.getInt("privacidade")],
						rs.getString("camera"), rs.getString("resolucao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				f.setCodigoFoto(rs.getInt("codigo_foto"));
				return f;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public ArrayList<Foto> loadAll() {
		ArrayList<Foto> fotos = new ArrayList<Foto>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Foto\"";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Foto f = new Foto(rs.getString("arquivo"), perfilDAO.load(rs.getInt("codigo_perfil")),
						rs.getString("descricao"), Privacidade.values()[rs.getInt("privacidade")],
						rs.getString("camera"), rs.getString("resolucao"),
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				f.setCodigoFoto(rs.getInt("codigo_foto"));
				fotos.add(f);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return fotos;
	}

	@Override
	public ArrayList<Foto> loadPage(int offset, int limit) {
		ArrayList<Foto> fotos = new ArrayList<Foto>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"Foto\" ";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			int c = 1;
			int c2 = 0;
			while (rs.next()) {
				c2++;
			}
			if (c2 > offset) {
				while (rs.next() || c == limit) {
					Foto f = new Foto(rs.getString("arquivo"), perfilDAO.load(rs.getInt("codigo_perfil")),
							rs.getString("descricao"), Privacidade.values()[rs.getInt("privacidade")],
							rs.getString("camera"), rs.getString("resolucao"),
							rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
							rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
					f.setCodigoFoto(rs.getInt("codigo_foto"));
					fotos.add(f);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return fotos;
	}

	@Override
	public void delete(Foto f) throws EntityTransientException {
		if (load(f.getId()) != null) {
			try (Connection con = openConnection()) {
				String sql = "DELETE FROM \"Foto\" WHERE codigo_foto = ? AND codigo_perfil = ?";
				PreparedStatement cmd = con.prepareStatement(sql);
				cmd.setInt(1, f.getId());
				cmd.setInt(2, f.getPerfil().getId());
				cmd.execute();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new EntityTransientException();
		}
	}

	public void update(Foto f) {
		try (Connection con = openConnection()) {
			String sql = "UPDATE \"Foto\" SET descricao = ?, privacidade = ?, data_hora_atualizacao = now() WHERE codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setString(1, f.getDescricao());
			cmd.setInt(2, f.getPrivacidade().ordinal());
			cmd.setInt(3, f.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int selectVisualizacoes(Foto foto) {
		int r = 0;
		try (Connection con = openConnection()) {
			String sql = "SELECT count(v.codigo_foto) FROM \"Visualiza\" v INNER JOIN \"Foto\" f ON v.codigo_foto = f.codigo_foto\r\n"
					+ "	WHERE v.codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, foto.getId());
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				r = rs.getInt("count");
			}
			return r;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ArrayList<Perfil> selectMarcacoesDaFoto(Foto foto) {
		ArrayList<Perfil> perfis = new ArrayList<Perfil>();
		try (Connection con = openConnection()) {
			String sql = "SELECT * FROM \"MarcacaoEmFoto\" WHERE codigo_foto = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, foto.getId());
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				Perfil p = perfilDAO.find(rs.getInt("codigo_perfil_marcado"));
				perfis.add(p);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return perfis;
	}
	
	public void insertMarcacao(Foto f, Perfil p, String login) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"MarcacaoEmFoto\" (codigo_foto, codigo_perfil_marcador, codigo_perfil_marcado) VALUES (?, ?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, f.getId());
			cmd.setInt(2, p.getId());
			cmd.setInt(3, perfilDAO.load(login).getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void removerMarcacao(Foto f, Perfil p, String login) {
		try (Connection con = openConnection()) {
			String sql = "DELETE FROM \"MarcacaoEmFoto\" m USING \"Foto\" f WHERE f.codigo_foto = m.codigo_foto AND m.codigo_foto = ? AND ((m.codigo_perfil_marcado = ? OR m.codigo_perfil_marcador = ?) AND m.codigo_perfil_marcado = ?)";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, f.getId());
			cmd.setInt(2, perfilDAO.load(login).getId());
			cmd.setInt(3, p.getId());
			cmd.setInt(4, perfilDAO.load(login).getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void removerMarcacoes(Foto f, Perfil p) {
		if(f.getPerfil().getId().equals(p.getId())) {
			try (Connection con = openConnection()) {
				String sql = "DELETE FROM \"MarcacaoEmFoto\" WHERE codigo_foto = ?";
				PreparedStatement cmd = con.prepareStatement(sql);
				cmd.setInt(1, f.getId());
				cmd.execute();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
