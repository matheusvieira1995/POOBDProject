package flickr.modelo;

import java.time.LocalDateTime;

import flickr.persistencia.TagDAO;

public class Tag implements IEntity {

	private int codigoTag;
	private Foto foto;
	private final Perfil perfil;
	private String nome;
	private LocalDateTime dataHoraCriacao, dataHoraAtualizacao;

	TagDAO tagDAO = new TagDAO();

	public Tag(String nome, Foto foto, Perfil perfil, LocalDateTime dataHoraCriacao, LocalDateTime dataHoraAtualizacao) {
		this.nome = nome;
		this.foto = foto;
		this.perfil=perfil;
		this.dataHoraCriacao = dataHoraCriacao;
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}
	
	public Tag(String nome, Perfil perfil, LocalDateTime dataHoraCriacao, LocalDateTime dataHoraAtualizacao) {
		this.nome = nome;
		this.perfil=perfil;
		this.dataHoraCriacao = dataHoraCriacao;
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}

	@Override
	public Integer getId() {
		return this.codigoTag;
	}

	public void setCodigoTag(int codigoTag) {
		this.codigoTag = codigoTag;
	}

	public Foto getFoto() {
		return this.foto;
	}

	public Perfil getPerfil() {
		return this.perfil;
	}

	public String getNome() {
		return this.nome;
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
		return this.nome;
	}
}
