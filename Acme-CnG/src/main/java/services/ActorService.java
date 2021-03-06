
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ActorRepository;
import security.LoginService;
import security.UserAccount;
import domain.Actor;

@Service
@Transactional
public class ActorService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private ActorRepository	actorRepository;


	// Supporting services ----------------------------------------------------

	// Constructors -----------------------------------------------------------

	public ActorService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------

	public Actor findOne(final int actorId) {
		Assert.isNull(actorId != 0);

		Actor result;

		result = this.actorRepository.findOne(actorId);
		Assert.notNull(result);

		return result;

	}

	public Collection<Actor> findAll() {
		Collection<Actor> result;

		result = this.actorRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public Actor save(final Actor actor) {
		Assert.notNull(actor);

		Actor result;

		result = this.actorRepository.save(actor);

		return result;
	}

	public void delete(final Actor actor) {
		Assert.notNull(actor);
		Assert.isTrue(actor.getId() != 0);
		Assert.isTrue(this.actorRepository.exists(actor.getId()));

		this.actorRepository.delete(actor);
	}

	public Actor findOneToSent(final int actorId) {

		Actor result;

		result = this.actorRepository.findOne(actorId);
		Assert.notNull(result);

		return result;

	}

	// Other business methods -------------------------------------------------

	public Actor findByPrincipal() {
		Actor result;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();
		result = this.findByUserAccount(userAccount);

		return result;
	}

	public Actor findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);

		Actor result;

		result = this.actorRepository.findByUserAccountId(userAccount.getId());

		return result;
	}

	public Collection<Actor> findActorsWhoPostedPlus10PercentOfAverageNumberOfComments() {
		final Collection<Actor> res = this.actorRepository.findActorsWhoPostedPlus10PercentOfAverageNumberOfComments();
		return res;
	}

	public Collection<Actor> findActorsWhoPostedLess10PercentOfAverageNumberOfComments() {
		final Collection<Actor> res = this.actorRepository.findActorsWhoPostedLess10PercentOfAverageNumberOfComments();
		return res;
	}

	public Collection<Actor> findActorwithMoreMessagesSent() {
		final Collection<Actor> res = this.actorRepository.findActorwithMoreMessagesSent();
		return res;
	}

	public Collection<Actor> findActorwithMoreMessagesReceived() {
		final Collection<Actor> res = this.actorRepository.findActorWithMoreMessagesReceived();
		return res;
	}

}
