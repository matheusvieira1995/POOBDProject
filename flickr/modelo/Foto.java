package flickr.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;

import flickr.exception.PerfilSemPermissaoException;
import flickr.persistencia.ComentarioDAO;
import flickr.persistencia.FotoDAO;
import flickr.persistencia.PerfilDAO;
import flickr.persistencia.TagDAO;

public class Foto implements IEntity {
	private Integer codigoFoto;
	final private String arquivo;
	private final Perfil perfil;
	private String descricao;
	private Privacidade privacidade;
	private String camera, resolucao;
	private LocalDateTime dataHoraCriacao, dataHoraAtualizacao;

	static PerfilDAO perfilDAO = new PerfilDAO();
	static ComentarioDAO comentarioDAO = new ComentarioDAO();
	static FotoDAO fotoDAO = new FotoDAO();
	static TagDAO tagDAO = new TagDAO();

	public Foto(String arquivo, Perfil perfil, String descricao, Privacidade privacidade, String camera,
			String resolucao, LocalDateTime dataHoraCriacao, LocalDateTime dataHoraAtualizacao) {
		this.arquivo = arquivo;
		this.perfil = perfil;
		this.descricao = descricao;
		this.privacidade = privacidade;
		this.camera = camera;
		this.resolucao = resolucao;
		this.dataHoraCriacao = dataHoraCriacao;
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}

	public Foto() {
		this.arquivo = null;
		this.perfil = null;
		this.descricao = null;
		this.privacidade = null;
		this.camera = null;
		this.resolucao = null;
		this.dataHoraCriacao = null;
		this.dataHoraAtualizacao = null;
	}

	@Override
	public Integer getId() {
		return this.codigoFoto;
	}

	public void setCodigoFoto(int codigoFoto) {
		this.codigoFoto = codigoFoto;
	}

	public String getArquivo() {
		return this.arquivo;
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

	public Privacidade getPrivacidade() {
		return this.privacidade;
	}

	public void setPrivacidade(Privacidade privacidade) {
		this.privacidade = privacidade;
	}

	public void setPrivada() {
		this.setPrivacidade(Privacidade.Privada);
	}

	public void setPublica() {
		this.setPrivacidade(Privacidade.Publica);
	}

	public String getCamera() {
		return this.camera;
	}

	public String getResolucao() {
		return this.resolucao;
	}

	@Override
	public LocalDateTime getDateTimeCreation() {
		return this.dataHoraCriacao;
	}

	@Override
	public LocalDateTime getDateTimeLastUpdate() {
		return this.dataHoraAtualizacao;
	}

	public static Foto carregarFoto(Integer codigoFoto) {
		Foto f = fotoDAO.load(codigoFoto);
		return f;
	}

	public int getVisualizacoes() {
		return fotoDAO.selectVisualizacoes(this);
	}

	public ArrayList<Tag> verTags() {
		return tagDAO.selectTagsDaFoto(this);
	}

	public void adicionarTag(Perfil p, String nome) {
		Tag t = new Tag(nome, this, p, LocalDateTime.now(), LocalDateTime.now());
		try {
			tagDAO.save(t);
			tagDAO.addTagsDaFoto(t, this);
		} catch (RuntimeException e) {
			t = tagDAO.selectNome(nome);
			tagDAO.addTagsDaFoto(t, this);
		}
	}

	public void removerTag(Perfil p, String nome) {
		if (this.getPerfil().getId().equals(p.getId()) == false) {
			try {
				throw new PerfilSemPermissaoException();
			} catch (PerfilSemPermissaoException e) {
				e.printStackTrace();
			}
		}
		tagDAO.removerTag(this, p, nome);
	}

	public void removerTags(Perfil p) {
		tagDAO.removerTagsDaFoto(this);
	}

	public ArrayList<Perfil> verMarcacoes() {
		return fotoDAO.selectMarcacoesDaFoto(this);
	}

	public void adicionarMarcacao(Perfil p, String login) {
		fotoDAO.insertMarcacao(this, p, login);
	}

	public void removerMarcacao(Perfil p, String login) {
		if ((p.getId().equals(perfilDAO.load(login).getId())) || (this.getPerfil().getId().equals(p.getId()))) {
			fotoDAO.removerMarcacao(this, p, login);
		} else {
			try {
				throw new PerfilSemPermissaoException();
			} catch (PerfilSemPermissaoException e) {
				e.printStackTrace();
			}
		}
	}

	public void removerMarcacoes(Perfil p) {
		fotoDAO.removerMarcacoes(this, p);
	}

	public ArrayList<Comentario> verComentarios() {
		return comentarioDAO.selectComentariosDaFoto(this);
	}

	@Override
	public String toString() {
		return this.getDescricao();
	}
}
