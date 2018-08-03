package flickr.modelo;

/*
* Todas as classes que representam o modelo devem
* direta ou indiretamente implementar IEntity
*/
public interface IEntity {
	Integer getId();

	java.time.LocalDateTime getDateTimeCreation();

	java.time.LocalDateTime getDateTimeLastUpdate();

	default boolean isPersistent() {
		return this.getId() != null;
	}

	default boolean isTransient() {
		return this.getId() == null;
	}
}