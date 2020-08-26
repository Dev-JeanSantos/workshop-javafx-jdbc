package db;

public class DbExcecaoInteridade extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DbExcecaoInteridade (String msg) {
		super(msg);
	}
	
}
