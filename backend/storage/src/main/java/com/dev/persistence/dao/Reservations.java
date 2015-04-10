package com.dev.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.Query;

import com.dev.persistence.dbaccess.PersistenceManager;
import com.dev.persistence.dbaccess.PersistenceManagerFactory;

@Entity
@Cacheable(false)
@Table(name = "reservations")
public class Reservations implements java.io.Serializable {	
	private static Logger logger = Logger.getLogger(Reservations.class.getName());
	private static final long serialVersionUID = 1454644747L;
	
	private long reservationId;
	private long tableId;
	private String startTime;
	private String userName;
	private String userPhone;
	private String userEmail;
	private Date created;
	private Date modified;
	
	@Id
	@Column(name = "reservation_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}

	@Column(name = "table_id")
	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	@Column(name = "start_time")
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "user_phone")
	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	@Column(name = "user_email")
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Column(name = "created")
	@Temporal(TemporalType.DATE)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Column(name = "modified")
	@Temporal(TemporalType.DATE)
	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public void save() throws Exception
	{
		this.setModified(new Date());
		PersistenceManagerFactory.getInstance().getPersistenceManager().saveOrUpdate(this);
	}

	public static Reservations loadReservation(Long reservationId) throws Exception {

		PersistenceManager persistMgr = PersistenceManagerFactory.getInstance().getPersistenceManager();

		Query query = persistMgr.getSession().createSQLQuery("select * from reservations where reservation_id = :reservationId").addEntity(Reservations.class);
		query.setParameter("reservationId", reservationId);
		logger.info("Executing Query : " + query.getQueryString());

		@SuppressWarnings("unchecked")
		List<Reservations> list = query.list();

		if (list == null || list.size() != 1){

			throw new NoResultException("No result found in reservationId:  " + reservationId);
		}

		return list.get(0);
	}
}