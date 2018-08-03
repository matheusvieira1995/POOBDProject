package flickr.modelo;

import java.time.LocalDateTime;

import flickr.persistencia.PerfilDAO;

public class Comentario implements IEntity {
	private Integer codigoComentario;
	private final Perfil perfil;
	private final Foto foto;
	private String descricao;
	final private LocalDateTime dataHoraCriacao;
	private LocalDateTime dataHoraAtualizacao;

	static PerfilDAO perfilDAO = new PerfilDAO();

	public Comentario(Perfil perfil, Foto foto, String descricao, LocalDateTime dataHoraCriacao,
			LocalDateTime dataHoraAtualizacao) {
		this.perfil = perfil;
		this.foto = foto;
		this.descricao = descricao;
		this.dataHoraCriacao = dataHoraCriacao;
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}

	@Override
	public Integer getId() {
		return this.codigoComentario;
	}

	public void setCodigoComentario(int codigoComentario) {
		this.codigoComentario = codigoComentario;
	}

	public Perfil getPerfil() {
		return this.perfil;
	}

	public Foto getFoto() {
		return this.foto;
	}

	public String getDescricao() {
		return this.descricao;
	}

	@Override
	public LocalDateTime getDateTimeCreation() {
		return this.dataHoraCriacao;
	}

	@Override
	public LocalDateTime getDateTimeLastUpdate() {
		return this.dataHoraAtualizacao;
	}

	@Override
	public String toString() {
		return this.descricao;
	}
}
