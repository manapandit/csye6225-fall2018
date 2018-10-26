package demo.models;

import org.hibernate.annotations.Fetch;

import javax.persistence.*;

@Entity
@Table
public class UserTransaction {
	@Id
	@Column(name = "id", length = 40)
	private String id;
	@Column(name = "description", length = 40)
	private String description;
	@Column(name = "merchant", length = 40)
	private String merchant;
	@Column(name = "amount", length = 40)
	private String amount;
	@Column(name = "date", length = 40)
	private String date;
	@Column(name = "category", length = 40)
	private String category;

	public UserTransaction() {

	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private User user;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Attachments attachments;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Reciept reciept;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Attachments getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachments attachments) {
		this.attachments = attachments;
	}

	public Reciept getReciept() {
		return reciept;
	}

	public void setReciept(Reciept reciept) {
		this.reciept = reciept;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
