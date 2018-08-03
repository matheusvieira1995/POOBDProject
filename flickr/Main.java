package flickr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import flickr.exception.FotoJaVisualizadaException;
import flickr.exception.PerfilSemPermissaoException;
import flickr.modelo.Album;
import flickr.modelo.Foto;
import flickr.modelo.Genero;
import flickr.modelo.Perfil;
import flickr.modelo.Privacidade;
import flickr.persistencia.FotoDAO;
import flickr.persistencia.PerfilDAO;
import flickr.persistencia.TagDAO;


@SuppressWarnings("unused")
public class Main {
	public static void main(String[] args) {
		
//		Perfil matheus = Perfil.fazerLogin("matheusvieira", "agua123");
//		Perfil israel = Perfil.fazerLogin("israeltduarte", "vento123");
//		Perfil israeldelete = Perfil.fazerLogin("israeltduartedelete", "ventod123");
		
		Perfil matheus = new Perfil("matheusvieira", "agua123", "Matheus Vieira", "matheusvieira1995", LocalDate.of(1998, 05, 12), Genero.Feminino, "Grande fotografo ucraniano");
		Perfil israel = new Perfil("israeltduarte", "vento123", "Israel Duarte", "israeltduarte@hotmail.com", LocalDate.of(1990, 05, 04), Genero.Masculino, "Grande fotografo brasileiro");
		System.out.println(israel.isPersistent()==false);
		System.out.println(israel.isTransient()==true);
		Perfil israeldelete = new Perfil("israeltduartedelete", "ventod123", "Israel Delete", "israel@hotmail.com", LocalDate.of(1990, 05, 04), Genero.Masculino, "Grande fotografo brasileiro deletado");

		System.out.println((matheus instanceof Perfil));
		matheus.salvar();
		System.out.println(matheus.isPersistent()==true);
		System.out.println((matheus instanceof Perfil) == true);
		israel.salvar();
		System.out.println((israel instanceof Perfil) == true);

		israeldelete.salvar();
		System.out.println(israeldelete.isPersistent() == true);
		System.out.println(israeldelete.isTransient() == false);
		System.out.println((israeldelete instanceof Perfil) == true);
		israeldelete.deletarPerfil();
		System.out.println(israeldelete.isPersistent() == false);
		System.out.println(israeldelete.isTransient() == true);
		
		System.out.println("-----");
		
		israel.editarPerfil("vento123", "Nilmar", LocalDate.of(1990, 06, 06), Genero.Masculino, "Grande atacante brasileiro");
		System.out.println(israel.getNome().equals("Nilmar"));
		System.out.println(israel.getDescricao().equals("Grande atacante brasileiro"));
		
		matheus.seguir(israel);
		System.out.println(matheus.segue(israel) == true);
		System.out.println(matheus.segue(matheus) == false);
		israel.seguir(matheus);
		System.out.println(israel.segue(matheus) == true);
		
		System.out.println("-----");
		
		matheus.subirFoto("matheushorror.jpg", "foto dos horrores", Privacidade.Publica, "Sony master", "5522");
		matheus.subirFoto("matheussombra.jpg", "foto dos sombras", Privacidade.Publica, "Sony bronze", "5456");
		matheus.subirFoto("matheustv.jpg", "foto dos televisoes", Privacidade.Publica, "Sony renomada", "555");
		israel.subirFoto("israelvale.jpg", "foto dos vales", Privacidade.Publica, "Sony renovada smart", "1522");
		israel.subirFoto("israeltambor.jpg", "foto dos tambores", Privacidade.Publica, "Sony recauchutada", "12");
		israel.subirFoto("israelvale.jpg", "foto dos vales", Privacidade.Publica, "Sony renovada smart", "1522");

		
		Foto foto1 = Foto.carregarFoto(4);
		System.out.println((foto1.getDescricao().equals("foto dos vales")));
		israel.editarFoto(foto1, "foto dos meio vales", Privacidade.Publica);
		matheus.editarFoto(foto1, "quero mudar", Privacidade.Publica);
		System.out.println((foto1.getDescricao().equals("quero mudar") == false));
		System.out.println((foto1.getDescricao().equals("foto dos meio vales")));
		
		Foto foto2 = Foto.carregarFoto(4);
		System.out.println(foto2.isTransient()==false);
		System.out.println(foto2.isPersistent()==true);
		israel.deletarFoto(foto2);
		
		Foto foto3 = Foto.carregarFoto(2);
		israel.verFoto(foto3);
		System.out.println(foto3.getVisualizacoes()==1);
				
		System.out.println("-----");
		
		Foto foto4 = Foto.carregarFoto(2);
		israel.verFoto(foto4).adicionarTag(israel, "sombra");
		israel.verFoto(foto4).adicionarTag(israel, "veneno");
		System.out.println(foto4.verTags().toString().equals("[sombra, veneno]"));
		israel.verFoto(foto4).removerTag(israel, "veneno");
		System.out.println(foto4.verTags().toString().equals("[sombra]"));
		matheus.verFoto(foto4).removerTag(matheus, "sombra");
		System.out.println(foto4.verTags().toString().equals("[sombra]"));

		Foto foto5 = Foto.carregarFoto(1);
		israel.verFoto(foto5).adicionarTag(israel, "valedo");
		System.out.println(foto5.verTags().toString().equals("[valedo]"));
		matheus.verFoto(foto5).removerTag(matheus, "valedo");
		System.out.println(foto5.verTags().toString().equals("[valedo]"));
		israel.verFoto(foto5).removerTag(israel, "valedo");
		System.out.println(foto5.verTags().toString().equals("[]"));
		
		System.out.println("-----");
		
		Foto foto6 = Foto.carregarFoto(2);
		System.out.println(foto6.verComentarios().toString().equals("[]"));
		israel.comentar(foto6, "que belo horror");
		System.out.println(foto6.verComentarios().toString().equals("[que belo horror]"));
		matheus.comentar(foto6, "tambem achei");
		System.out.println(foto6.verComentarios().toString().equals("[que belo horror, tambem achei]"));
		
		Foto foto7 = Foto.carregarFoto(5);
		foto7.adicionarMarcacao(israel, "israeltduarte");
		System.out.println(foto7.verMarcacoes().toString().equals("[israeltduarte]"));
		foto7.adicionarMarcacao(israel, "matheusvieira");
		System.out.println(foto7.verMarcacoes().toString().equals("[israeltduarte, matheusvieira]"));
		foto7.removerMarcacao(matheus, "matheusvieira");
		System.out.println(foto7.verMarcacoes().toString().equals("[israeltduarte]"));
		foto7.removerMarcacao(matheus, "israeltduarte"); // erro
		System.out.println(foto7.verMarcacoes().toString().equals("[israeltduarte]"));
		
		System.out.println("-----");
		
		Foto foto8 = Foto.carregarFoto(6);
		Foto foto9 = Foto.carregarFoto(5);
		israel.verFoto(foto8);
		System.out.println(israel.fotosSelecionadas().toString().equals("[]"));
		israel.selecionarFoto(foto8);
		israel.selecionarFoto(foto9);
		System.out.println(israel.fotosSelecionadas().toString().equals("[foto dos vales, foto dos tambores]"));
		Foto foto10 = Foto.carregarFoto(3);
		//israel.selecionarFoto(foto10); //sem permissao
		israel.criarAlbum(israel.fotosSelecionadas(), "Album Instrumentos" , "Veja soh que legal esses instrumentos bacanas");
		
		matheus.selecionarFoto(Foto.carregarFoto(2));
		matheus.selecionarFoto(Foto.carregarFoto(3));
		matheus.criarAlbum(matheus.fotosSelecionadas(), "Album Diverso" , "Soh as sombras da tv");
		Album album1 = Album.carregarAlbum(2);
		System.out.println(matheus.verAlbum(album1).toString().equals("[foto dos sombras, foto dos televisoes]"));
		matheus.verAlbum(album1).adicionarFoto(Foto.carregarFoto(1));
		System.out.println(matheus.verAlbum(album1).toString().equals("[foto dos sombras, foto dos televisoes, foto dos horrores]"));
		matheus.verAlbum(album1).removerFoto(Foto.carregarFoto(1));
		System.out.println(matheus.verAlbum(album1).toString().equals("[foto dos sombras, foto dos televisoes]"));
		
		israel.editarAlbum(Album.carregarAlbum(1), "Album Instrumentos", "Os instrumentos continuam bacanas");
		System.out.println(israel.verAlbum(Album.carregarAlbum(1)).getDescricao().equals("Os instrumentos continuam bacanas"));
		
		System.out.println("-----");
		
		Foto foto12 = Foto.carregarFoto(5);
		Foto foto13 = Foto.carregarFoto(6);
		System.out.println(israel.verFavoritos().toString().equals("[]"));
		israel.favoritar(foto12);
		System.out.println(israel.verFavoritos().toString());

		System.out.println(israel.verFavoritos().toString().equals("[foto dos tambores]"));
		israel.desfavoritar(foto12);
		System.out.println(israel.verFavoritos().toString().equals("[]"));
		israel.favoritar(israel.verFoto(Foto.carregarFoto(1)));
		System.out.println(israel.verFavoritos().toString().equals("[foto dos horrores]"));
		
		Foto foto11 = Foto.carregarFoto(6);
		System.out.println(foto11.getPrivacidade().toString().equals("Publica"));
		israel.privarFoto(foto11);
		System.out.println(foto11.getPrivacidade().toString().equals("Privada"));
		israel.desprivarFoto(foto11);
		System.out.println(foto11.getPrivacidade().toString().equals("Publica"));
		
		System.out.println("fim");
	}
}
