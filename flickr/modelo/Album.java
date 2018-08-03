package flickr.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;

import flickr.exception.PerfilSemPermissaoException;
import flickr.persistencia.AlbumDAO;

public class Album implements IEntity {
	private Integer codigoAlbum;
	private final Perfil perfil;
	private String nome;
	private String descricao;
	private ArrayList<Foto> fotos = new ArrayList<Foto>();
	private LocalDateTime dataHoraCriacao, dataHoraAtualizacao;

	static AlbumDAO albumDAO = new AlbumDAO();

	public Album(Perfil perfil, String nome, String descricao, ArrayList<Foto> fotos, LocalDateTime dataHoraCriacao,
			LocalDateTime dataHoraAtualizacao) {
		this.perfil = perfil;
		this.nome = nome;
		this.descricao = descricao;
		this.fotos = fotos;
		this.dataHoraCriacao = dataHoraCriacao;
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}

	@Override
	public Integer getId() {
		return this.codigoAlbum;
	}

	public void setCodigoAlbum(int codigoAlbum) {
		this.codigoAlbum = codigoAlbum;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Perfil getPerfil() {
		return this.perfil;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ArrayList<Foto> getFotos() {
		return fotos;
	}

	@Override
	public LocalDateTime getDateTimeCreation() {
		return this.dataHoraCriacao;
	}

	@Override
	public LocalDateTime getDateTimeLastUpdate() {
		return this.dataHoraAtualizacao;
	}

	public static Album carregarAlbum(int codigoAlbum) {
		return albumDAO.load(codigoAlbum);
	}

	public void adicionarFoto(Foto f) {
		if (this.getPerfil().getId().equals(f.getPerfil().getId())) {
			albumDAO.adicionarFotoAlbum(f, this);
		} else {
			try {
				throw new PerfilSemPermissaoException();
			} catch (PerfilSemPermissaoException e) {
				e.printStackTrace();
			}
		}
	}

	public void removerFoto(Foto f) {
		albumDAO.deletarFotoAlbum(f, this);
	}

	@Override
	public String toString() {
		return this.fotos.toString();
	}
}
