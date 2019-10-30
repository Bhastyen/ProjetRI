public class Pair<L,R> {
	  private L numOcc;
	  private R value;

	  public Pair(L left, R right) {
	    this.numOcc = left;
	    this.value = right;
	  }

	  public L getNumOcc() {
		return numOcc;
	}

	public void setNumOcc(L numOcc) {
		this.numOcc = numOcc;
	}

	public void setValue(R value) {
		this.value = value;
	}

	public R getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (numOcc == null) {
			if (other.numOcc != null)
				return false;
		} else if (!numOcc.equals(other.numOcc))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Pair [numOcc=" + numOcc + ", value=" + value + "]";
	}  
}
