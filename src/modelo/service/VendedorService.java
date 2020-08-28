package modelo.service;

import java.util.List;

import modelo.dao.DaoFabrica;
import modelo.dao.VendedorDao;
import modelo.entidades.Vendedor;

public class VendedorService {
	
	private VendedorDao vedDao = DaoFabrica.criarVendedorDao();
		
	public List<Vendedor> buscarTodos(){
			
		return vedDao.buscarTodos();
		
	}
	
	public void salvarOuAtualizar(Vendedor obj) {
		
		if (obj.getId() == null) {
			
			vedDao.inserir(obj);
		}else {
			
			vedDao.update(obj);
		}
			
	}
	
	public void remover(Vendedor obj) {
		
		vedDao.deletarPorId(obj.getId());
			
	}
}
