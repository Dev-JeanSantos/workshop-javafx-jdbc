package modelo.service;

import java.util.List;

import modelo.dao.DaoFabrica;
import modelo.dao.DepartamentoDao;
import modelo.entidades.Departamento;

public class DepartamentoService {
	
	private DepartamentoDao depDao = DaoFabrica.iniciarDepartamentoDao();
		
	public List<Departamento> buscarTodos(){
			
		return depDao.buscarTodos();
		
	}
}
