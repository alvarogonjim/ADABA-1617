
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.RequestOfferRepository;
import domain.Application;
import domain.Comment;
import domain.Customer;
import domain.Place;
import domain.RequestOffer;
import domain.Status;
import forms.RequestOfferForm;

@Service
@Transactional
public class RequestOfferService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private RequestOfferRepository	requestOfferRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private CustomerService			customerService;


	// Constructors -----------------------------------------------------------

	public RequestOfferService() {
		super();
	}

	//Simple CRUD methods ---------------------------------

	public RequestOffer create() {

		RequestOffer res;

		res = new RequestOffer();

		return res;

	}

	public Collection<RequestOffer> findAll() {
		Collection<RequestOffer> res;
		res = this.requestOfferRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public RequestOffer findOne(final int requestOfferId) {
		RequestOffer res;
		res = this.requestOfferRepository.findOne(requestOfferId);
		Assert.notNull(res);
		return res;
	}

	public RequestOffer save(final RequestOffer ro) {
		Assert.notNull(ro);
		final Customer principal = this.customerService.findByPrincipal();
		Assert.isTrue(principal != null);
		return this.requestOfferRepository.save(ro);

	}

	public void delete(final RequestOffer ro) {
		Assert.notNull(ro);
		this.requestOfferRepository.delete(ro);
	}

	//Other business methods ---------------------------------------------------

	public Collection<RequestOffer> findAllRequest() {
		Collection<RequestOffer> requests;

		requests = this.requestOfferRepository.findAllRequest();

		return requests;
	}

	public Collection<RequestOffer> findAllOffers() {
		Collection<RequestOffer> offers;

		offers = this.requestOfferRepository.findAllOffers();

		return offers;
	}

	public void applyRequestOffer(final RequestOffer requestOffer) {
		Assert.notNull(requestOffer);

		RequestOffer reqOff;
		Application application;
		Customer customer;

		reqOff = this.requestOfferRepository.findOne(requestOffer.getId());
		application = this.applicationService.create();
		customer = this.customerService.findByPrincipal();

		Assert.isTrue(reqOff.getCustomer().getId() != customer.getId());          //Can't apply his own requestOffers
		Assert.isTrue(!(this.appliedRequestOffer(customer, requestOffer)));

		application.setStatus(Status.PENDING);
		application.setRequestOffer(reqOff);

		application.setCustomer(customer);

		customer.getApplications().add(application);

		this.customerService.save(customer);
		this.applicationService.save(application);

	}

	public boolean appliedRequestOffer(final Customer customer, final RequestOffer requestOffer) {
		boolean res = false;
		Collection<Application> applications;

		applications = customer.getApplications();
		for (final Application a : applications)
			if (a.getRequestOffer().getId() == requestOffer.getId()) {
				res = true;
				break;
			}

		return res;
	}

	public void banRequestOffer(final RequestOffer requestOffer) {
		Assert.notNull(requestOffer);

		RequestOffer res;

		res = this.requestOfferRepository.findOne(requestOffer.getId());

		Assert.isTrue(res.isBanned() == false);

		res.setBanned(true);

		this.requestOfferRepository.save(res);

	}

	public RequestOffer reconstruct(final RequestOfferForm requestOfferForm) {
		Assert.notNull(requestOfferForm);
		final RequestOffer requestOffer;
		Collection<Comment> commentsReceived;
		final Customer principal = this.customerService.findByPrincipal();
		Assert.isTrue(principal != null);

		Place originPlace;
		Place destinationPlace;

		requestOffer = this.create();
		commentsReceived = new ArrayList<Comment>();
		originPlace = new Place();
		destinationPlace = new Place();

		originPlace.setAddress(requestOfferForm.getOriginaddress());
		originPlace.setLatitude(requestOfferForm.getOriginlatitude());
		originPlace.setLength(requestOfferForm.getOriginlength());

		destinationPlace.setAddress(requestOfferForm.getDestinationaddress());
		destinationPlace.setLatitude(requestOfferForm.getDestinationlatitude());
		destinationPlace.setLength(requestOfferForm.getDestinationlength());

		requestOffer.setBanned(false);
		requestOffer.setCommentsReceived(commentsReceived);
		requestOffer.setCustomer(principal);
		requestOffer.setDescription(requestOfferForm.getDescription());
		requestOffer.setMoment(requestOfferForm.getMoment());
		requestOffer.setRequestOrOffer(requestOfferForm.getRequestOrOffer());
		requestOffer.setTitle(requestOfferForm.getTitle());
		requestOffer.setOriginPlace(originPlace);
		requestOffer.setDestinationPlace(destinationPlace);

		return requestOffer;

	}

	public RequestOffer reconstructForTests(final RequestOfferForm requestOfferForm) {
		Assert.notNull(requestOfferForm);
		final RequestOffer requestOffer;
		Collection<Comment> commentsReceived;
		final Customer principal = this.customerService.findByPrincipal();
		Assert.isTrue(principal != null);

		//NOTA:
		//Estas l�neas se a�aden con objeto de poder comprobar en los test funcionales que se cumplan las restricciones del datatype Place, ya que aunque este contiene
		// sus propias anotaciones que Spring comprueba correctamente durante el funcionamiento de la aplicaci�n, al ejecutar los test JUnit no se validan estos atributos		

		if (requestOfferForm.getMoment() != null) {
			final Date actual = new Date();
			Assert.isTrue(requestOfferForm.getMoment().after(actual));
		}

		Assert.isTrue(requestOfferForm.getOriginaddress() != "");
		Assert.isTrue(requestOfferForm.getDestinationaddress() != "");

		Assert.isTrue(requestOfferForm.getOriginlatitude() >= -90 && requestOfferForm.getOriginlatitude() <= 90);
		Assert.isTrue(requestOfferForm.getDestinationlatitude() >= -90 && requestOfferForm.getDestinationlatitude() <= 90);
		Assert.isTrue(requestOfferForm.getOriginlength() >= -180 && requestOfferForm.getOriginlength() <= 180);
		Assert.isTrue(requestOfferForm.getDestinationlength() >= -180 && requestOfferForm.getDestinationlength() <= 180);

		//FIN NOTA

		Place originPlace;
		Place destinationPlace;

		requestOffer = this.create();
		commentsReceived = new ArrayList<Comment>();
		originPlace = new Place();
		destinationPlace = new Place();

		originPlace.setAddress(requestOfferForm.getOriginaddress());
		originPlace.setLatitude(requestOfferForm.getOriginlatitude());
		originPlace.setLength(requestOfferForm.getOriginlength());

		destinationPlace.setAddress(requestOfferForm.getDestinationaddress());
		destinationPlace.setLatitude(requestOfferForm.getDestinationlatitude());
		destinationPlace.setLength(requestOfferForm.getDestinationlength());

		requestOffer.setBanned(false);
		requestOffer.setCommentsReceived(commentsReceived);
		requestOffer.setCustomer(principal);
		requestOffer.setDescription(requestOfferForm.getDescription());
		requestOffer.setMoment(requestOfferForm.getMoment());
		requestOffer.setRequestOrOffer(requestOfferForm.getRequestOrOffer());
		requestOffer.setTitle(requestOfferForm.getTitle());
		requestOffer.setOriginPlace(originPlace);
		requestOffer.setDestinationPlace(destinationPlace);

		return requestOffer;

	}

	public Collection<RequestOffer> findAllRequestFromPrincipal(final Customer cus) {
		Assert.notNull(cus);

		Collection<RequestOffer> reqs;

		reqs = this.requestOfferRepository.findAllRequestFromPrincipal(cus.getId());

		return reqs;
	}

	public Collection<RequestOffer> findAllOffersFromPrincipal(final Customer cus) {
		Assert.notNull(cus);

		Collection<RequestOffer> offs;

		offs = this.requestOfferRepository.findAllOffersFromPrincipal(cus.getId());

		return offs;
	}

	public Collection<RequestOffer> findAllRequestNotBanned() {

		Collection<RequestOffer> reqs;

		reqs = this.requestOfferRepository.findAllRequestNotBanned();

		return reqs;
	}

	public Collection<RequestOffer> findAllOffersNotBanned() {

		Collection<RequestOffer> offs;

		offs = this.requestOfferRepository.findAllOffersNotBanned();

		return offs;
	}

	public Collection<RequestOffer> findAllBannedRequestsOffersFromPrincipal(final Customer cus) {
		Assert.notNull(cus);

		Collection<RequestOffer> banned;

		banned = this.requestOfferRepository.findAllBannedRequestsOffersFromPrincipal(cus.getId());

		return banned;
	}

	public Collection<RequestOffer> findRequestOfferByKeyword(final String keyword) {
		Assert.notNull(keyword);
		Collection<RequestOffer> coll;

		coll = this.requestOfferRepository.searchByKeyword(keyword);
		Assert.notNull(coll);
		return coll;
	}

	public void flush() {
		this.requestOfferRepository.flush();

	}

	public Double ratioOffersVsRequest() {
		final Double res = this.requestOfferRepository.ratioOffersVsRequest();
		return res;
	}

	public Double averageNumberOfOffersPerCustomer() {
		final Double res = this.requestOfferRepository.averageNumberOfOffersPerCustomer();
		return res;
	}

	public Double averageNumberOfRequestPerCustomer() {
		final Double res = this.requestOfferRepository.averageNumberOfRequestPerCustomer();
		return res;
	}

}
