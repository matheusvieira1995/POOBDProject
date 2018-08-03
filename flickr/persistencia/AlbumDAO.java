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

public class AlbumDAO extends AbstractDAO<Album> {
	PerfilDAO perfilDAO = new PerfilDAO();
	FotoDAO fotoDAO = new FotoDAO();

	@Override
	public void save(Album a) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"Album\" (codigo_perfil, nome, descricao, data_hora_criacao, data_hora_atualizacao) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			cmd.setInt(1, a.getPerfil().getId());
			cmd.setString(2, a.getNome());
			cmd.setString(3, a.getDescricao());
			cmd.setTimestamp(4, Timestamp.valueOf(a.getDateTimeCreation()));
			cmd.setTimestamp(5, Timestamp.valueOf(a.getDateTimeLastUpdate()));
			cmd.execute();
			
			ResultSet rs1 = cmd.getGeneratedKeys();
			if (rs1.next()) {
				a.setCodigoAlbum(rs1.getInt(1));
			}
			ArrayList<Foto> fotos = a.getFotos();
			for (Foto foto : fotos) {
				adicionarFotoAlbum(foto, a);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Album load(int codigoAlbum) throws EntityNotFoundException {
		try (Connection con = openConnection()) {
			ArrayList<Foto> fotos = new ArrayList<Foto>();
			String sql = "SELECT * FROM \"Album\" WHERE codigo_album = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoAlbum);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				String sql2 = "SELECT * FROM \"FotosDoAlbum\" WHERE codigo_album = ?";
				PreparedStatement cmd2 = con.prepareStatement(sql2);
				cmd2.setInt(1, codigoAlbum);
				ResultSet rs2 = cmd2.executeQuery();
				while (rs2.next()) {
					Foto f = fotoDAO.load(rs2.getInt("codigo_foto"));
					fotos.add(f);
				}
				
				Album a = new Album(
						perfilDAO.load(rs.getInt("codigo_perfil")),
						rs.getString("nome"),
						rs.getString("descricao"),
						fotos,
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				a.setCodigoAlbum((rs.getInt("codigo_album")));
				return a;
			} else {
				throw new EntityNotFoundException();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Album find(int codigoAlbum) {
		try (Connection con = openConnection()) {
			ArrayList<Foto> fotos = new ArrayList<Foto>();
			String sql = "SELECT * FROM \"Album\" WHERE codigo_album = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setInt(1, codigoAlbum);
			ResultSet rs = cmd.executeQuery();
			if (rs.next()) {
				String sql2 = "SELECT * FROM \"FotosDoAlbum\" WHERE codigo_album = ?";
				PreparedStatement cmd2 = con.prepareStatement(sql2);
				ResultSet rs2 = cmd2.executeQuery();
				if (rs2.next()) {
					Foto f = fotoDAO.load(rs2.getInt("codigo_foto"));
					fotos.add(f);
				}
				
				Album a = new Album(perfilDAO.load(rs.getInt("codigo_perfil")), rs.getString("nome"),
						rs.getString("descricao"), fotos, rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				a.setCodigoAlbum((rs.getInt("codigo_album")));
				return a;
			} 
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public List<Album> loadAll() {
		ArrayList<Album> albuns = new ArrayList<Album>();
		try (Connection con = openConnection()) {
			ArrayList<Foto> fotos = new ArrayList<Foto>();
			String sql = "SELECT * FROM \"Comentario\"";
			PreparedStatement cmd = con.prepareStatement(sql);
			ResultSet rs = cmd.executeQuery();
			while (rs.next()) {
				//Montando as fotos do album
				String sql2 = "SELECT * FROM \"FotosDoAlbum\" WHERE codigo_album = ?";
				PreparedStatement cmd2 = con.prepareStatement(sql2);
				ResultSet rs2 = cmd2.executeQuery();
				while (rs2.next()) {
					Foto f = fotoDAO.load(rs2.getInt("codigo_foto"));
					fotos.add(f);
				}
				
				Album a = new Album(
						perfilDAO.load(rs.getInt("codigo_perfil")),
						rs.getString("nome"),
						rs.getString("descricao"),
						fotos,
						rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
						rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
				a.setCodigoAlbum((rs.getInt("codigo_comentario")));
				albuns.add(a);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return albuns;
	}

	@Override
	public List<Album> loadPage(int offset, int limit) {
		ArrayList<Album> albuns = new ArrayList<Album>();
		try (Connection con = openConnection()) {
			ArrayList<Foto> fotos = new ArrayList<Foto>();
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
					String sql2 = "SELECT * FROM \"FotosDoAlbum\" WHERE codigo_album = ?";
					PreparedStatement cmd2 = con.prepareStatement(sql2);
					ResultSet rs2 = cmd2.executeQuery();
					while (rs2.next()) {
						Foto f = fotoDAO.load(rs2.getInt("codigo_foto"));
						fotos.add(f);
					}
					
					Album a = new Album(perfilDAO.load(rs.getInt("codigo_perfil")), rs.getString("nome"),
							rs.getString("descricao"), fotos, rs.getTimestamp("data_hora_criacao").toLocalDateTime(),
							rs.getTimestamp("data_hora_atualizacao").toLocalDateTime());
					a.setCodigoAlbum((rs.getInt("codigo_comentario")));
					albuns.add(a);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return albuns;
	}

	@Override
	public void delete(Album a) throws EntityTransientException {
		if (load(a.getId()) != null) {
			try (Connection con = openConnection()) {
				String sql = "DELETE FROM \"Album\" WHERE codigo_album = ?";
				PreparedStatement cmd = con.prepareStatement(sql);
				cmd.setInt(1, a.getId());
				cmd.execute();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new EntityTransientException();
		}
	}
	
	public void update(Album a) {
		try (Connection con = openConnection()) {
			String sql = "UPDATE \"Album\" SET nome = ?, descricao = ?, data_hora_atualizacao = now() WHERE codigo_album = ?";
			PreparedStatement cmd = con.prepareStatement(sql);
			cmd.setString(1, a.getNome());
			cmd.setString(2, a.getDescricao());
			cmd.setInt(3, a.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void adicionarFotoAlbum(Foto f, Album a) {
		try (Connection con = openConnection()) {
			String sql = "INSERT INTO \"FotosDoAlbum\" (codigo_foto, codigo_album) VALUES (?, ?)";
			PreparedStatement cmd = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			cmd.setInt(1, f.getId());
			cmd.setInt(2, a.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void deletarFotoAlbum(Foto f, Album a) {
		try (Connection con = openConnection()) {
			String sql = "DELETE FROM \"FotosDoAlbum\" WHERE codigo_foto = ? AND codigo_album = ?";
			PreparedStatement cmd = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			cmd.setInt(1, f.getId());
			cmd.setInt(2, a.getId());
			cmd.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
