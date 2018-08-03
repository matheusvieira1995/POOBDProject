package flickr.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import flickr.exception.PerfilSemPermissaoException;
import flickr.persistencia.AlbumDAO;
import flickr.persistencia.ComentarioDAO;
import flickr.persistencia.FotoDAO;
import flickr.persistencia.PerfilDAO;

public class Perfil implements IEntity {
	private Integer codigoPerfil;
	private String login;
	private String senha, nome, email;
	private LocalDate dataNascimento;
	private Genero genero;
	private String descricao;
	private LocalDateTime dataHoraCriacao, dataHoraAtualizacao;

	ArrayList<Foto> fotos = new ArrayList<Foto>();

	static PerfilDAO perfilDAO = new PerfilDAO();
	static FotoDAO fotoDAO = new FotoDAO();
	static ComentarioDAO comentarioDAO = new ComentarioDAO();
	static AlbumDAO albumDAO = new AlbumDAO();

	public Perfil(String login, String senha, String nome, String email, LocalDate dataNascimento, Genero genero,
			String descricao, LocalDateTime dataHoraCriacao, LocalDateTime dataHoraAtualizacao) {
		this.login = login;
		this.senha = senha;
		this.nome = nome;
		this.email = email;
		this.dataNascimento = dataNascimento;
		this.genero = genero;
		this.descricao = descricao;
		this.dataHoraCriacao = dataHoraCriacao;
		this.dataHoraAtualizacao = dataHoraAtualizacao;
	}

	public Perfil(String login, String senha, String nome, String email, LocalDate dataNascimento, Genero genero,
			String descricao) {
		this.login = login;
		this.senha = senha;
		this.nome = nome;
		this.email = email;
		this.dataNascimento = dataNascimento;
		this.genero = genero;
		this.descricao = descricao;
		this.dataHoraCriacao = LocalDateTime.now();
		this.dataHoraAtualizacao = LocalDateTime.now();
	}

	@Override
	public Integer getId() {
		return this.codigoPerfil;
	}

	public void setCodigoPerfil(int codigoPerfil) {
		this.codigoPerfil = codigoPerfil;
	}

	public String getLogin() {
		return this.login;
	}

	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return this.email;
	}

	public LocalDate getDataNascimento() {
		return this.dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Genero getGenero() {
		return this.genero;
	}

	public void setGenero(Genero genero) {
		this.genero = genero;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public LocalDateTime getDateTimeCreation() {
		return this.dataHoraCriacao;
	}

	@Override
	public LocalDateTime getDateTimeLastUpdate() {
		return this.dataHoraAtualizacao;
	}

	public void salvar() {
		perfilDAO.save(this);
	}

	public static Perfil fazerLogin(String login, String senha) {
		Perfil perfilLogado = perfilDAO.login(login, senha);
		return perfilLogado;
	}

	public static Perfil carregarPerfil(String login) {
		Perfil perfilCarregado = perfilDAO.load(login);
		return perfilCarregado;
	}

	public void editarPerfil(String senha, String nome, LocalDate dataNascimento, Genero genero, String descricao) {
		this.senha = senha;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
		this.genero = genero;
		this.descricao = descricao;
		perfilDAO.update(this);
	}

	public void seguir(Perfil p) {
		perfilDAO.insertSeguir(this, p);
	}

	public void deixarSeguir(Perfil p) {
		perfilDAO.deleteSeguir(this, p);
	}

	public boolean segue(Perfil p) {
		if (perfilDAO.segue(this, p)) {
			return true;
		}
		return false;
	}

	public Foto verFoto(Foto f) {
		if (this.getId() == f.getPerfil().getId() || f.getPrivacidade() == Privacidade.Publica) {
			try {
				perfilDAO.visualiza(f, this);
			} catch (RuntimeException e) {
				return fotoDAO.load(f.getId());
			}
			return fotoDAO.load(f.getId());
		}
		return null;
	}

	public void subirFoto(String arquivo, String descricao, Privacidade privacidade, String camera, String resolucao) {
		Foto f = new Foto(arquivo, this, descricao, privacidade, camera, resolucao, LocalDateTime.now(),
				LocalDateTime.now());
		fotoDAO.save(f);
	}

	public void editarFoto(Foto f, String descricao, Privacidade privacidade) {
		if (this.getId() == f.getPerfil().getId()) {
			f.setDescricao(descricao);
			f.setPrivacidade(privacidade);
			fotoDAO.update(f);
		}
	}

	public void deletarFoto(Foto f) {
		fotoDAO.delete(f);
	}

	public void privarFoto(Foto f) {
		f.setPrivada();
		fotoDAO.update(f);
	}

	public void desprivarFoto(Foto f) {
		f.setPublica();
		fotoDAO.update(f);
	}

	public void favoritar(Foto f) {
		perfilDAO.favoritar(this, f);
	}

	public void desfavoritar(Foto f) {
		perfilDAO.desfavoritar(this, f);
	}

	public ArrayList<Foto> verFavoritos() {
		return perfilDAO.selectFavoritos(this);
	}

	public void selecionarFoto(Foto f) {
		if (this.getId().equals(f.getPerfil().getId())) {
			fotos.add(f);
		} else {
			try {
				throw new PerfilSemPermissaoException();
			} catch (PerfilSemPermissaoException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Foto> fotosSelecionadas() {
		return fotos;
	}

	public void criarAlbum(ArrayList<Foto> fotos, String nome, String descricao) {
		Album album = new Album(this, nome, descricao, fotos, LocalDateTime.now(), LocalDateTime.now());
		albumDAO.save(album);
	}

	public Album verAlbum(Album a) {
		return albumDAO.load(a.getId());
	}

	public void editarAlbum(Album a, String nome, String descricao) {
		a = albumDAO.load(a.getId());
		if (this.getId() == a.getPerfil().getId()) {
			a.setDescricao(descricao);
			a.setNome(nome);
			albumDAO.update(a);
		}
	}

	public void deletarAlbum(Album a) {
		albumDAO.delete(a);
	}

	public void comentar(Foto foto, String descricao) {
		Comentario c = new Comentario(this, foto, descricao, LocalDateTime.now(), LocalDateTime.now());
		comentarioDAO.save(c);
	}

	public ArrayList<Foto> verGaleria() {
		return perfilDAO.selectGaleria(this);
	}

	public void deletarPerfil() {
		perfilDAO.delete(this);
		this.codigoPerfil = null;
//		this.login = null;
//		this.senha = null;
//		this.nome = null;
//		this.email = null;
//		this.dataNascimento = null;
//		this.genero = null;
//		this.descricao = null;
//		this.dataHoraCriacao = null;
//		this.dataHoraAtualizacao = null;
	}

	@Override
	public String toString() {
		return this.login;
	}

}
