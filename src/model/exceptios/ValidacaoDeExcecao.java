package model.exceptios;

import java.util.HashMap;
import java.util.Map;

public class ValidacaoDeExcecao extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> erros = new HashMap<>();
	
	public ValidacaoDeExcecao(String msg) {		
		super(msg);
	}

	public Map<String, String> getErros() {
		return erros;
	}
	
	public void addErros(String nomeCampo, String msgErro) {
		
		erros.put(nomeCampo, msgErro);
		
	}
}
