package app.repositories;

import app.models.entities.ApiRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApiRequestsRepository extends ReactiveMongoRepository<ApiRequest, String> {

    @Override
    <S extends ApiRequest> Mono<S> save(S entity);

    @Override
    Mono<ApiRequest> findById(String id);
}
