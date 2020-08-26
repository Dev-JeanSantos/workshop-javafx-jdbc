package modelo.dao;

import java.util.List;

import modelo.entidades.Departamento;
import modelo.entidades.Vendedor;

public interface VendedorDao {
	
	void inserir (Vendedor obj);
	void update (Vendedor obj);
	void deletarPorId (Integer id);
	Vendedor pesquisarPorId (Integer id);
	List<Vendedor> buscarTodos();
	List<Vendedor> buscarPorDepartamento(Departamento departamento);
}
