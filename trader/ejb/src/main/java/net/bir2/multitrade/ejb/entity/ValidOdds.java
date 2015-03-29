package net.bir2.multitrade.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


@NamedQueries( {
	@NamedQuery(name = "GetValidOddsRangeCount", query = "select count(v) FROM ValidOdds v where :value between v.lowMargin and v.highMargin"),
	@NamedQuery(name = "GetValidOddsRange", query = "select v FROM ValidOdds v where :value between v.lowMargin and v.highMargin"),
		@NamedQuery(name = "GetMaxHighMargin",  query = "select max(v.highMargin) FROM ValidOdds v where v.highMargin < :value"),
		@NamedQuery(name = "GetMinLowMargin",   query = "select min(v.lowMargin) FROM ValidOdds v where v.lowMargin > :value")
})

@Entity
public class ValidOdds implements java.io.Serializable {

	private static final long serialVersionUID = -211149267941714710L;
	private Double lowMargin;
	private Double highMargin;
	private Double step;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getLowMargin() {
		return lowMargin;
	}

	public void setLowMargin(Double lowMargin) {
		this.lowMargin = lowMargin;
	}

	public Double getHighMargin() {
		return highMargin;
	}

	public void setHighMargin(Double highMargin) {
		this.highMargin = highMargin;
	}

	public Double getStep() {
		return step;
	}

	public void setStep(Double step) {
		this.step = step;
	}

	@Override
	public String toString() {
		return "ValidOdds [ "
				+ "lowMargin=" + lowMargin + " highMargin=" + highMargin + ", step=" + step + ']';
	}
	
	
}
