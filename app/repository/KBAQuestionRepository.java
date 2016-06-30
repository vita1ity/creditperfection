package repository;

import com.google.inject.ImplementedBy;

import repository.impl.KBAQuestionRepositoryImpl;

@ImplementedBy(KBAQuestionRepositoryImpl.class)
public interface KBAQuestionRepository {

}
