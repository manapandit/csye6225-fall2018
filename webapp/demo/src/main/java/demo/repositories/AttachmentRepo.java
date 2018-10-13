package demo.repositories;

import demo.models.Reciept;
import org.springframework.data.repository.CrudRepository;

public interface AttachmentRepo extends CrudRepository<Reciept, String> {
}
