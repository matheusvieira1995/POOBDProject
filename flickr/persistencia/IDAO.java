package flickr.persistencia;

import flickr.exception.EntityNotFoundException;
import flickr.exception.EntityTransientException;
import flickr.modelo.IEntity;

public interface IDAO<T extends IEntity> {

	/**
	 * Salva o objeto, se já for persistente ele atualiza, caso contrário um novo
	 * é inserido e o id atribuído
	 *
	 * @param objeto O objeto a ser persistido
	 */
	void save(T objeto);

	/**
	 * Carrega o objeto pelo seu id.
	 *
	 * @param id id do objeto na base de dados
	 * @return O objeto localizado ou
	 * @throws EntityNotFoundException se não for encontrado
	 */
	T load(int id) throws EntityNotFoundException;

	/**
	 * Carrega o objeto pelo seu id.
	 *
	 * @param id id do objeto na base de dados
	 * @return O objeto localizado ou null se não for encontrado
	 */
	T find(int id);

	/**
	 * Traz todos os registros
	 *
	 * @return Uma lista com todos objetos encontrados
	 */
	java.util.List<T> loadAll();

	/**
	 * Traz vários registros
	 *
	 * @param offset Deslocamento
	 * @param limit  Quantidade de registros a trazer
	 * @return Uma lista com os objetos localizados ou uma lista vazia caso nenhum
	 *         seja encontrado
	 */
	java.util.List<T> loadPage(int offset, int limit);

	/**
	 * Remove o registro de um objeto, setando seu código como nulo se for bem
	 * sucedido
	 *
	 * @param objeto O objeto a ser excluído
	 * @throws EntityTransientException se o objeto for transiente
	 */
	void delete(T objeto) throws EntityTransientException;
}