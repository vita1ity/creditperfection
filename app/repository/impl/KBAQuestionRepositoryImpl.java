package repository.impl;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;

import models.KBAQuestions;
import repository.KBAQuestionRepository;

@Singleton
public class KBAQuestionRepositoryImpl implements KBAQuestionRepository {

	private Finder<Long, KBAQuestions> find = new Finder<Long, KBAQuestions>(KBAQuestions.class);
	
	
	
}
